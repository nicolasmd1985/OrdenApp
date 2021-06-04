package dipzo.ordenapp.orders.Views;

/**
 * Created by nicolas on 27/04/2017.
 * ENVIO Y RECEPCION DE DATOS DE LA UBICACON GPS DEL TECNICO
 */

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import dipzo.ordenapp.orders.Constans;
import dipzo.ordenapp.orders.R;
import dipzo.ordenapp.orders.Sqlite.users;

public class Login extends AppCompatActivity implements View.OnClickListener{


    private EditText user, pass;
    private Button mSubmit;
    ProgressDialog prgDialog;
    HashMap<String, String> queryValues;
    private String token_message = "";

    users users = new users(this);


    private static final int PERMISSION_REQUEST_CODE = 1;
    private boolean LocationAvailable;
    private static final String TAG = "Pedidos";



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
        intialize_firebase();
        create_token();
        check_handle();
    }

    private void check_handle() {
        ArrayList token = users.tokenExp();
        if (token.size() > 0){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy hh:mm", Locale.ENGLISH);
                String time_token = token.get(4).toString();
                String status_id = token.get(6).toString();
                try {
                    Date date = formatter.parse(time_token);
                    Date date2 = new Date();
                    if (date.after(date2)  && status_id.contentEquals("202")){
                        startprogram();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    private void create_token() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        token_message = task.getResult().getToken();
                    }
                });
    }

    private void intialize_firebase() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH));
        }
        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.default_notification_channel_name))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        String msg = getString(R.string.msg_subscribed);
//                        if (!task.isSuccessful()) {
//                            msg = getString(R.string.msg_subscribe_failed);
//                        }
//                        Log.d(TAG, msg);
                    }
                });

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
        create_token();

        prgDialog.show();
        params.add("email", username);
        params.add("password", password);
        params.add("token_params", token_message);
        client.post(Constans.API_END + Constans.AUTH, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String str = new String(responseBody, "UTF-8");
                    prgDialog.hide();
                    try {
                     JSONObject obj = new JSONObject(str);
                     if (obj.getString("token") != null){
                         if(insertUser(obj)){
                        startprogram();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
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
        ArrayList tokenExp = users.tokenExp();
    }
    private boolean insertUser(JSONObject obj) throws JSONException {

        String tecnic_id =  users.tecnic_id();
        if (tecnic_id.contentEquals("null") || (!tecnic_id.equals(String.valueOf(obj.getInt("user_id"))))){
            InsertNewUser(obj);
        }
        else {
            UpdateUser(obj);
        }

        return true;

    }
    private void startprogram(){
        Intent x = new Intent(Login.this, Orders.class);
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
        queryValues.put("status_id", obj.getString("status_id"));
        return users.insertUser(queryValues);
    }

    private boolean UpdateUser(JSONObject obj)throws JSONException{
        queryValues = new HashMap<>();
        queryValues.put("user_id", String.valueOf(obj.getInt("user_id")));
        queryValues.put("token", obj.getString("token"));
        queryValues.put("exp", obj.getString("exp"));
        queryValues.put("status_id", obj.getString("status_id"));
        return users.updateUser(queryValues);
    }

}