package dipzo.ordenapp.tecnic;

//***********************MUESTRA PANATALLA CON LOS DATOS BASICOS DEL PEDIDO*******************/////////


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import dipzo.ordenapp.tecnic.Sqlite.DBController;
import dipzo.ordenapp.tecnic.Sqlite.users;

public class OrderDetails extends AppCompatActivity implements View.OnClickListener{

    DBController controller = new DBController(this);
    users users = new users(this);

    private TextView company,address,city,description, install_time, limit_time,  category_id, comment;

    private Button scanBtn;

    String id_order,id_tecnic;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_pedido);
        company = (TextView) findViewById(R.id.company);
        address = (TextView) findViewById(R.id.address);
        city = (TextView) findViewById(R.id.city);
        install_time = (TextView) findViewById(R.id.install_time);
        limit_time = (TextView) findViewById(R.id.limit_time);
        category_id = (TextView) findViewById(R.id.category);

        description = (TextView) findViewById(R.id.description);
        comment = (TextView) findViewById((R.id.comment));

        scanBtn = (Button)findViewById(R.id.button2);
        scanBtn.setOnClickListener(this);
        id_order = getIntent().getStringExtra("id_order");
        id_tecnic = getIntent().getStringExtra("id_tecnic");
        detalle(id_order);
        }


    private void detalle(String idpedido) {

        ArrayList<HashMap<String, String>> listdetalle = controller.listdetalle(idpedido);
        for (HashMap<String, String> hashMap : listdetalle) {
            description.setText(hashMap.get("description"));
            address.setText(hashMap.get("address"));
            city.setText(hashMap.get("city_id"));
            company.setText(users.customer_name(hashMap.get("customer_id")));
            install_time.setText(hashMap.get("install_time"));
            limit_time.setText(hashMap.get("limit_time"));
            category_id.setText(hashMap.get("category_id"));
            comment.setText(hashMap.get("comment"));
        }
    }

    @Override
    public void onClick(View view) {

        //Toast.makeText(this,"hola",Toast.LENGTH_LONG).show();
        //**************FORZA LA INICIALIZACION DEL LOCALIZADOR GPS**********/////////
        try {
            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                buildAlertMessageNoGps();
            }else
            {
                status_510();
            }

        }catch(Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();}




    }


    //****************ESTO ES PARA DEVOLVERSE*****************

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            Intent i = new Intent(OrderDetails.this, Orders.class);
            i.putExtra("id_order", id_order );
            i.putExtra("id_tecnic", id_tecnic );
            startActivity(i);
            //return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /////////////********GPS*********//////////////
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El GPS esta desactivado, desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    ///////////********SEND STATUS 510 ****///////////////

    public void status_510()
    {
        android.app.AlertDialog.Builder saveDialog = new android.app.AlertDialog.Builder(this);
        saveDialog.setTitle("Empezar Orden");
        saveDialog.setMessage("Se dara inicio a la orden una vez sea aceptada");
        saveDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                send_gps_status();
                Intent i = new Intent(OrderDetails.this, Agregar_dispositivos.class);
                i.putExtra("id_order", id_order );
                i.putExtra("id_tecnic", id_tecnic );
                startActivity(i);
            }
        });
        saveDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        saveDialog.show();
    }


    public void send_gps_status()
    {
        String lon=null,lat=null;
        ArrayList<HashMap<String,String>> listgps = controller.getgps();
        lon = String.valueOf(listgps.get(0));
        lat = String.valueOf(listgps.get(1));


        ArrayList token = users.tokenExp();

        if (token != null){
            AsyncHttpClient client = new AsyncHttpClient();
            client.setBearerAuth(token.get(3).toString());
            RequestParams params = new RequestParams();
            params.add("latitude", lat);
            params.add("longitude", lon);
            try{
                client.put(Constans.API_END + Constans.ARRIVE + id_order, params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(getApplicationContext(), "Se informo de llegada",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    }

                });}catch (Exception e){}
        }



    }

}
