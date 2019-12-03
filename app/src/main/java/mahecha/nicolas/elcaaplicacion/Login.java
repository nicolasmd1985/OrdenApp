package mahecha.nicolas.elcaaplicacion;

/**
 * Created by nicolas on 27/04/2017.
 * ENVIO Y RECEPCION DE DATOS DE LA UBICACON GPS DEL TECNICO
 */

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Debug;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import mahecha.nicolas.elcaaplicacion.GPS.ServicioGPS2;
import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;

public class Login extends AppCompatActivity  implements View.OnClickListener{


    private EditText user, pass;
    private Button mSubmit;
    ProgressDialog prgDialog;
    HashMap<String, String> queryValues;

    DBController controller = new DBController(this);
    EnvioDatos envioDatos = new EnvioDatos(this);

    private static final int PERMISSION_REQUEST_CODE = 1;
    private boolean LocationAvailable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        mSubmit = (Button) findViewById(R.id.login);
        mSubmit.setOnClickListener(this);

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Conectando......");
        prgDialog.setCancelable(false);


        LocationAvailable = false;

        if (checkPermission()) {
        } else {
            requestPermission();
        }

    }


    @Override
    public void onClick(View view) {
    //////***************FORZAR AL SERVICIO DE GPS ENVIAR UBICACION********///////////////
        try {
            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                buildAlertMessageNoGps();
            }else
            {

                registro();
            }

        }catch(Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();}


    }


    ////////////**********************FUNCION DE INGRESO**********************/////////
    public void registro()
    {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        String username = user.getText().toString();
        String password = pass.getText().toString();


        prgDialog.show();
        params.add("email", username);
        params.add("password", password);
        client.post(Constans.API_END + Constans.AUTH, params, new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(String response) {
            prgDialog.hide();
            try {
                JSONObject obj = new JSONObject(response);
                if (obj.getString("token") != null){
                    if(insertUser(obj)){
                        startprogram();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onFailure(int statusCode, Throwable error,
                              String content) {

            if (statusCode == 401) {
                Toast.makeText(getApplicationContext(), "Requested resource not found check credentials", Toast.LENGTH_LONG).show();
            } else if (statusCode == 500) {
                Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Device without connection",
                        Toast.LENGTH_LONG).show();
            }
            prgDialog.hide();
        }

        });
    }



//    private void bdregistro() {
//
//        String username = user.getText().toString();
//
//
//        ArrayList<HashMap<String, String>> userList =  controller.getUsers();
//        if(userList.size()!=0) {
//            ArrayList<HashMap<String, String>> loginlist = controller.login();
//            int i=0;
//            for (HashMap<String, String> hashMap : loginlist) {
//
//                if (username.equals(hashMap.get("usuario"))&&password.equals(hashMap.get("pass"))) {
//
//                    //envioDatos.enviar();
//                    Intent x = new Intent(Login.this, Pedidos.class);
//                    Toast.makeText(getApplicationContext(), "Login Correcto", Toast.LENGTH_LONG).show();
//                    x.putExtra("idusuario",hashMap.get("idusuario")  );
//                    startActivity(x);
//
//                }else{i++;}
//                if(userList.size()==i){Toast.makeText(getApplicationContext(), "Usuario ó Contraseña Incorrecta", Toast.LENGTH_LONG).show();}
//
//            }
//        }else{Toast.makeText(getApplicationContext(), "No existe ningun usuario registrado", Toast.LENGTH_LONG).show();}
//     }
//




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
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED){
            LocationAvailable = true;
            return true;
        } else {
            LocationAvailable = false;
            return false;
        }
    }
    private void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "This app relies on location data for it's main functionality. Please enable GPS data to access all features.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
        }
    }
    private void checkToken(JSONObject obj) throws JSONException {
        ArrayList tokenExp = controller.tokenExp();
        System.out.print(tokenExp);
    }
    private boolean insertUser(JSONObject obj) throws JSONException {

        String tecnic_id =  controller.tecnic_id();
        if (tecnic_id.contentEquals("null") || (!tecnic_id.equals(String.valueOf(obj.getInt("user_id"))))){
            InsertNewUser(obj);
        }
        else {
            UpdateUser(obj);
        }

        return true;

    }
    private void startprogram(){
        Intent x = new Intent(Login.this, Pedidos.class);
        Toast.makeText(Login.this, "Inicio Correcto", Toast.LENGTH_LONG).show();
        prgDialog.dismiss();
        startActivity(x);
    }


    private boolean InsertNewUser(JSONObject obj)throws JSONException{
        queryValues = new HashMap<>();
        queryValues.put("user_id", String.valueOf(obj.getInt("user_id")));
        queryValues.put("first_name", obj.getString("first_name"));
        queryValues.put("last_name", obj.getString("last_name"));
        queryValues.put("token", obj.getString("token"));
        queryValues.put("exp", obj.getString("exp"));
        queryValues.put("email", obj.getString("email"));
        return controller.insertUser(queryValues);
    }

    private boolean UpdateUser(JSONObject obj)throws JSONException{
        queryValues = new HashMap<>();
        queryValues.put("user_id", String.valueOf(obj.getInt("user_id")));
        queryValues.put("token", obj.getString("token"));
        queryValues.put("exp", obj.getString("exp"));
        return controller.updateUser(queryValues);
    }

}