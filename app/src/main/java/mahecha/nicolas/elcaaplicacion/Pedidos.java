package mahecha.nicolas.elcaaplicacion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import mahecha.nicolas.elcaaplicacion.Controllers.auto_referral;
import mahecha.nicolas.elcaaplicacion.Controllers.count_referrals;
import mahecha.nicolas.elcaaplicacion.Controllers.customer_controller;
import mahecha.nicolas.elcaaplicacion.Controllers.manual_referral;
import mahecha.nicolas.elcaaplicacion.GPS.ServicioGPS2;
import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;
import mahecha.nicolas.elcaaplicacion.Sqlite.orders;
import mahecha.nicolas.elcaaplicacion.Sqlite.referrals;
import mahecha.nicolas.elcaaplicacion.Sqlite.users;


public class Pedidos extends AppCompatActivity {

    DBController controller = new DBController(this);
    users users = new users(this);
    orders orders = new orders(this);
    referrals referrals = new referrals(this);



    EnvioDatos envioDatos = new EnvioDatos(this);
    customer_controller customers = new customer_controller(this);
    manual_referral manual_referral = new manual_referral(this);
    count_referrals count_referrals = new count_referrals(this);



    ProgressDialog prgDialog;
    HashMap<String, String> queryValues;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        Intent GPS = new Intent(Pedidos.this, ServicioGPS2.class);
        cargabdl();
        startService(GPS);

    }


    ////////////////////******************AGREGA PEDIDO******************//////////////////

    public void addPedidos(View view) {
        Intent objIntent = new Intent(getApplicationContext(), Nuevo_pedido.class);
        startActivity(objIntent);
    }

    ////////////////*****************CARGA BASE DE DATOS SQLITE************************
    public void cargabdl()
    {
        ArrayList user_id = users.tokenExp();
        ArrayList<HashMap<String, String>> userList =  orders.get_orders(user_id.get(0).toString());
        if(userList.size()!=0) {
            ListAdapter adapter = new SimpleAdapter(Pedidos.this, userList, R.layout.view_pedidos, new String[]{"customer_id", "description"}, new int[]{R.id.clieteid, R.id.detalleid});
            final ListView myList = (ListView) findViewById(android.R.id.list);
            myList.setAdapter(adapter);
            myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View view, int i, long l) {

                    Map<String, Object> map = (Map<String, Object>) myList.getItemAtPosition(i);
                    String id_order = (String) map.get("id_order");
                    String id_tecnic = (String) map.get("fk_user_id");
                    Intent x = new Intent(Pedidos.this, Detalles_pedido.class);
                    x.putExtra("id_order", id_order);
                    x.putExtra("id_tecnic", id_tecnic);
                    startActivity(x);
                }
            });
            myList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView adapterView, View view, int i, long l) {
                    Map<String, Object> map = (Map<String, Object>)myList.getItemAtPosition(i);
                    String id_order = (String) map.get("id_order");
                    String aux_order = (String) map.get("aux_order");

                    showSimplePopUp(id_order, aux_order);
                    return true;
                }

            });

        }
    }

    //////////******************POP UP**************//////////////
    private void showSimplePopUp(final String idped, final String aux_order) {

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Eliminar");
        helpBuilder.setMessage("Realmente desea elimiar el pedido"+idped+" "+aux_order);
        helpBuilder.setPositiveButton("Si",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        deleteSync(idped, aux_order);
                    }
                });
        helpBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }


    ///////////////////////////********RECARGA ACTIVIDAD//////////////////
    public void reloadActivity() {
        Intent objIntent = new Intent(getApplicationContext(), Pedidos.class);
        startActivity(objIntent);
    }

    ////////////////////**************MENU ACTUALIZAR**********************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    ////////////////////////////////*************BOTON DE SINCRONIZACION DE BD*******************////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.refresh) {
            syncSQLiteMySQLDB();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////************************get data orders************************
    public void syncSQLiteMySQLDB() {

        customers.customer_request();
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Sincronizando Pedidos, espere un momento............");
        prgDialog.setCancelable(false);
        prgDialog.show();

        ArrayList token = users.tokenExp();

        if (token != null){
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            client.setBearerAuth(token.get(3).toString());
            client.get(Constans.API_END + Constans.ORDERS, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String str = new String(responseBody, "UTF-8");
                        updateSQLite(str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    prgDialog.hide();
                    if (statusCode == 404) {
                        Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                    } else if (statusCode == 500) {
                        Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Dispositivo Sin Conexión a Internet",
                                Toast.LENGTH_LONG).show();
                    }
                }

            });
        }


    }


    /////////////////////////////*******************ACTUALIZA SQLITE*********************////////////////////
    public void updateSQLite(String response){
        try {
            JSONArray arr = new JSONArray(response);
            if(arr.length() != 0){

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = (JSONObject) arr.get(i);
                    queryValues = new HashMap<String, String>();
                    queryValues.put("id_order", obj.get("id").toString());
                    queryValues.put("tecnic_id", obj.get("tecnic_id").toString());
                    queryValues.put("description", obj.get("description").toString());
                    queryValues.put("address", obj.get("address").toString());
                    queryValues.put("customer_id", obj.get("customer_id").toString());
                    queryValues.put("city_id", obj.get("city_id").toString());
                    queryValues.put("created_at", obj.get("created_at").toString());
                    queryValues.put("install_date", obj.get("install_date").toString());
                    queryValues.put("limit_time", obj.get("limit_time").toString());
                    queryValues.put("category_id", obj.get("category_id").toString());

                    orders.insert_order(queryValues, 0);


                }
//
                updateMySQLSyncSts();
            }else {
                Toast.makeText(getApplicationContext(), "No Tiene Pedidos Para Sincronizar",
                        Toast.LENGTH_LONG).show();
                prgDialog.hide();
                send_referrals();
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //////////////***********send update sync: true*************************//////////////
    public void updateMySQLSyncSts(){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        ArrayList user_id = users.tokenExp();

        client.setBearerAuth(user_id.get(3).toString());


        ArrayList<HashMap<String, String>> order_list_to_sync =  orders.get_orders_auto(user_id.get(0).toString());
        if (order_list_to_sync.size() != 0) {
            params.put("_json", order_list_to_sync);
            client.post(Constans.API_END + Constans.SYNC, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(getApplicationContext(), "Se ha informado al supervisor de la sincronización", Toast.LENGTH_LONG).show();
                    send_referrals();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getApplicationContext(), "ups! ocurrio un error", Toast.LENGTH_LONG).show();
                    send_referrals();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No tiene Pedidos pendientes", Toast.LENGTH_LONG).show();
            send_referrals();
        }

    }

    //////////////***********Update Sync: false and dele SQlite bd Order*************************//////////////
    public void deleteSync(final String idped, final String aux_ped) {
        if(!aux_ped.contentEquals("1")){
            String id_ped = idped;
            if(!aux_ped.contentEquals("0")){
                id_ped = aux_ped;
            }

            prgDialog = new ProgressDialog(this);
            prgDialog.setMessage("Eliminando Pedido............");
            prgDialog.setCancelable(false);
            prgDialog.show();

            ArrayList token = users.tokenExp();

            AsyncHttpClient client = new AsyncHttpClient();
            client.setBearerAuth(token.get(3).toString());

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id", id_ped);
            client.put(Constans.API_END + Constans.DSYNC + id_ped, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(getApplicationContext(), "Se ha informado al supervisor de la sincronización", Toast.LENGTH_LONG).show();
                    prgDialog.hide();
                    controller.elim_aux(idped);
                    reloadActivity();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
                    prgDialog.hide();
                }

            });
        }
        else {
            controller.elim_aux(idped);
            Toast.makeText(getApplicationContext(), "Se elimino la orden manual: "+ idped , Toast.LENGTH_LONG).show();
            reloadActivity();
        }

    }


    public void send_referrals()
    {
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Enviando y Recibiendo Pedidos Pendientes, espere un momento............");
        prgDialog.setCancelable(false);
        prgDialog.show();

        ArrayList<HashMap<String, String>> aux_pen= orders.manual_order();
        if(aux_pen.size()!=0)
        {
            for (int i = 0; i < aux_pen.size(); i++) {
                manual_referral.send_manual_order(aux_pen.get(i));
            }

            prgDialog.hide();

        }else{enviaremito();}
    }


////////////////////*****************REVISA SI TIENE PEDIDOS NUEVOS*********//////////////////

    private void enviaremito(){
        auto_referral auto_referral = new auto_referral(this);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);


        ArrayList<HashMap<String, String>> pending= referrals.get_refferals();
        auto_referral.send_auto_referral(pending, String.valueOf(storageDir));


        ArrayList<HashMap<String, String>> pending_manual= referrals.get_manual_refferals();
        manual_referral.send_manual_referral(pending_manual, String.valueOf(storageDir));



        prgDialog.hide();
        reloadActivity();
    }


    /////////****************ESTO ES PARA DEVOLVERSE*****************///////////////

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            Intent i = new Intent(Pedidos.this, Login.class);
            startActivity(i);
            //return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    ///////////////////////*************UBICACION MANUAL*************////////////
    public void ubicacion (View view)
    {
        try {
            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                buildAlertMessageNoGps();
            }
            else{
                envioDatos.enviar();
            }
        }catch(Exception e){Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();}

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


}
