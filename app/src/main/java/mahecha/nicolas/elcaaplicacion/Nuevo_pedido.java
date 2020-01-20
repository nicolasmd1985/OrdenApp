package mahecha.nicolas.elcaaplicacion;

/**
* *****CREAR NUEVO PEDIDO MANUALMENTE**************
* */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mahecha.nicolas.elcaaplicacion.Model.Customer;
import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;

public class Nuevo_pedido extends AppCompatActivity {

    Spinner opciones;
    EditText descripcion,cliente,calle,numero,ciudad,provincia;
    DBController controller = new DBController(this);
    String idusuar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_pedido);

        opciones = (Spinner)findViewById(R.id.spinner);
        calle = (EditText) findViewById(R.id.address);
        numero = (EditText) findViewById(R.id.city);
        ciudad = (EditText) findViewById(R.id.phone);
        provincia = (EditText) findViewById(R.id.email);
        descripcion = (EditText) findViewById(R.id.descripcion);


        onPostExecute();




    }

    /**
     * Called when Save button is clicked
     * @param view
     */
    public void addNewUser(View view) {
        HashMap<String, String> queryValues = new HashMap<String, String>();

        queryValues.put("idtecnico", idusuar);
        queryValues.put("idauxpedido", UUID.randomUUID().toString());
        //queryValues.put("idauxpedido", "hola");
        queryValues.put("descripcion", descripcion.getText().toString());
        queryValues.put("cliente", cliente.getText().toString());
        queryValues.put("calle", calle.getText().toString());
        queryValues.put("numero", numero.getText().toString());
        queryValues.put("ciudad", ciudad.getText().toString());
        queryValues.put("provincia", provincia.getText().toString());



        if (cliente.getText().toString() != null
                && cliente.getText().toString().trim().length() != 0) {
            if (descripcion.getText().toString() != null
                    && descripcion.getText().toString().trim().length() != 0) {
                if (calle.getText().toString() != null
                        && calle.getText().toString().trim().length() != 0) {

                    controller.insert_order(queryValues);
                    this.callHomeActivity(view);

                }else {
                    Toast.makeText(getApplicationContext(), "Instroduzca Domicilio",
                            Toast.LENGTH_LONG).show();}

            }else {
                Toast.makeText(getApplicationContext(), "Instroduzca descripcion",
                        Toast.LENGTH_LONG).show();}
        } else {
            Toast.makeText(getApplicationContext(), "Instroduzca cliente",
                    Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Navigate to Home Screen
     * @param view
     */
    public void callHomeActivity(View view) {
        Intent objIntent = new Intent(getApplicationContext(),
                Pedidos.class);
        objIntent.putExtra("idusuario",idusuar );
        startActivity(objIntent);
    }

    /**
     * Called when Cancel button is clicked
     * @param view
     */
    public void cancelAddUser(View view) {
        this.callHomeActivity(view);
    }

    protected void onPostExecute() {

        ArrayList customers;
        List<Customer> customerList = new ArrayList<>();
        Map<String, Customer> map = new HashMap<>();
        int i = 0;


        customers = controller.customers();
        ArrayList<HashMap<String, String>> userList =  customers;
        for (HashMap<String, String> hashMap : userList) {
            map.put("customer"+i, new Customer(Integer.parseInt(hashMap.get("customer_id")), hashMap.get("first_name"),hashMap.get("last_name")));
            customerList.add(map.get("customer"+i));
        }

        ArrayAdapter<Customer> adapter = new ArrayAdapter<Customer>(this, android.R.layout.simple_spinner_item, customerList );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        opciones.setAdapter(adapter);
        opciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Customer customer = (Customer) adapterView.getSelectedItem();
                diplayCustomerData(customer);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void diplayCustomerData(Customer custumer){
        int customer_id = custumer.getCustomer_id();
        String first_name = custumer.getFirst_name();

        String customerData = "customer_id: " + customer_id + "\nFirst Name:" + first_name ;
        Toast.makeText(this, customerData, Toast.LENGTH_LONG).show();
    }
}