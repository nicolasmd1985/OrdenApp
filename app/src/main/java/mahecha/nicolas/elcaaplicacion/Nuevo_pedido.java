package mahecha.nicolas.elcaaplicacion;

/**
* *****CREAR NUEVO PEDIDO MANUALMENTE**************
* */

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import mahecha.nicolas.elcaaplicacion.Model.Customer;
import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;
import mahecha.nicolas.elcaaplicacion.Sqlite.orders;
import mahecha.nicolas.elcaaplicacion.Sqlite.users;

public class Nuevo_pedido extends AppCompatActivity {

    Spinner customer_options;
    Spinner category_options;
    Spinner data_time_options;

    EditText description_field,
            address_field,
            city_field,
            phone_field,
            email_field,
            limit_time;
    DBController controller = new DBController(this);
    String customer_id_field;
    String date_time, category;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_pedido);

        customer_options = (Spinner)findViewById(R.id.spinner);
        category_options = (Spinner)findViewById(R.id.spinner2);
        data_time_options = (Spinner)findViewById(R.id.spinner3);

        address_field = (EditText) findViewById(R.id.address);
        city_field = (EditText) findViewById(R.id.city);
        phone_field = (EditText) findViewById(R.id.phone);
        email_field = (EditText) findViewById(R.id.email);
        description_field = (EditText) findViewById(R.id.description);
        limit_time = (EditText) findViewById(R.id.limit_time);


        onPostExecute();

    }

    /**
     * Called when Save button is clicked
     * @param view
     */
    public void addNewOrder(View view) {
        users users = new users(this);
        orders orders = new orders(this);
        Random random = new Random();

        int x = random.nextInt(900) + 1000000;
        System.out.println(x);
        String tecnic_id = users.tecnic_id();
        HashMap<String, String> queryValues = new HashMap<String, String>();

        queryValues.put("id_order", String.valueOf(x));
        queryValues.put("tecnic_id", tecnic_id);
        queryValues.put("description", description_field.getText().toString());
        queryValues.put("address", address_field.getText().toString());
        queryValues.put("customer_id", customer_id_field);
        queryValues.put("city_id", city_field.getText().toString());
        queryValues.put("created_at", tiempo());
        queryValues.put("install_date", tiempo());
        queryValues.put("limit_time", limit_time.getText().toString()+"-"+date_time);
        queryValues.put("category_id", category);

        orders.insert_order(queryValues, 1);
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
        List<String> categoryList = new ArrayList<>();
        List<String> dataTimeList = new ArrayList<>();
        Map<String, Customer> map = new HashMap<>();
        int i = 0;

        categoryList.add("install");
        categoryList.add("maintenance");
        categoryList.add("repair");


        dataTimeList.add("horas");
        dataTimeList.add("dias");

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


        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataTimeList );
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        data_time_options.setAdapter(adapter3);
        data_time_options.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                date_time = (String) adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryList );
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_options.setAdapter(adapter2);
        category_options.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = (String) adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



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

    public void diplayCustomerData(Customer costumer){
        int customer_id = costumer.getCustomer_id();
        String city = costumer.getCity();
        String email = costumer.getEmail();
        String phone_number = costumer.getPhone_number();


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