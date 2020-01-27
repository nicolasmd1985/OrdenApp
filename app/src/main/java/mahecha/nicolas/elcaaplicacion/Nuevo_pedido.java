package mahecha.nicolas.elcaaplicacion;

/**
* *****CREAR NUEVO PEDIDO MANUALMENTE**************
* */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import mahecha.nicolas.elcaaplicacion.Model.Customer;
import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;
import mahecha.nicolas.elcaaplicacion.Sqlite.users;

public class Nuevo_pedido extends AppCompatActivity {

    Spinner customer_options;
    EditText description_field,
            address_field,
            city_field,
            phone_field,
            email_field;
    DBController controller = new DBController(this);
    String customer_id_field;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_pedido);

        customer_options = (Spinner)findViewById(R.id.spinner);

        address_field = (EditText) findViewById(R.id.address);
        city_field = (EditText) findViewById(R.id.city);
        phone_field = (EditText) findViewById(R.id.phone);
        email_field = (EditText) findViewById(R.id.email);
        description_field = (EditText) findViewById(R.id.description);


        onPostExecute();




    }

    /**
     * Called when Save button is clicked
     * @param view
     */
    public void addNewUser(View view) {
        users users = new users(this);

        String tecnic_id = users.tecnic_id();
        HashMap<String, String> queryValues = new HashMap<String, String>();

        queryValues.put("tecnic_id", tecnic_id);
        queryValues.put("description", description_field.getText().toString());
        queryValues.put("address", address_field.getText().toString());
        queryValues.put("customer_id", customer_id_field);
        queryValues.put("city_id", city_field.getText().toString());
        queryValues.put("created_at", tiempo());
        queryValues.put("install_date", tiempo());
        queryValues.put("aux_order", String.valueOf(1) );


        controller.insert_order(queryValues);
        this.callHomeActivity(view);

    }

    /**
     * Navigate to Home Screen
     * @param view
     */
    public void callHomeActivity(View view) {
        Intent objIntent = new Intent(getApplicationContext(),
                Pedidos.class);
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
            map.put("customer"+i, new Customer(
                    Integer.parseInt(hashMap.get("customer_id")),
                    hashMap.get("first_name"),
                    hashMap.get("last_name"),
                    hashMap.get("email"),
                    hashMap.get("phone_number"),
                    hashMap.get("city")));
            customerList.add(map.get("customer"+i));
        }

        ArrayAdapter<Customer> adapter = new ArrayAdapter<Customer>(this, android.R.layout.simple_spinner_item, customerList );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        customer_options.setAdapter(adapter);
        customer_options.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        String city = custumer.getCity();
        String email = custumer.getEmail();
        String phone_number = custumer.getPhone_number();

//        String customerData = "customer_id: " + customer_id + "\nFirst Name:" + first_name + "\nemail:" + email ;
//        Toast.makeText(this, customerData, Toast.LENGTH_LONG).show();

//        address_field.setText(address);
        city_field.setText(city);
        phone_field.setText(phone_number);
        email_field.setText(email);
        customer_id_field = String.valueOf(customer_id);

    }


    private String tiempo()
    {
        Date date = new Date();
        CharSequence s  = DateFormat.format("d/M/yyyy H:m", date.getTime());
        String time = s.toString();
        return time ;
    }
}