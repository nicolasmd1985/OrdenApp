package mahecha.nicolas.elcaaplicacion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import com.google.gson.JsonArray;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mahecha.nicolas.elcaaplicacion.GPS.ServicioGPS2;
import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;


public class Pedidos extends AppCompatActivity {

    DBController controller = new DBController(this);
    EnvioDatos envioDatos = new EnvioDatos(this);

    ProgressDialog prgDialog;
    HashMap<String, String> queryValues;
    TextView contador;
    int rems=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        Intent GPS = new Intent(Pedidos.this, ServicioGPS2.class);
        cargabdl();
        startService(GPS);
        contador=(TextView)findViewById(R.id.contador);
        contadores();
    }
    ////////////////////******************AGREGA PEDIDO******************//////////////////

    public void addPedidos(View view) {
        Intent objIntent = new Intent(getApplicationContext(), Nuevo_pedido.class);
        startActivity(objIntent);
    }

    ////////////////*****************CARGA BASE DE DATOS SQLITE************************
    public void cargabdl()
    {
        ArrayList user_id = controller.tokenExp();
        ArrayList<HashMap<String, String>> userList =  controller.get_orders(user_id.get(0).toString());
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
//                    Toast.makeText(getApplicationContext(), "presiono" + i, Toast.LENGTH_SHORT).show();
                    Map<String, Object> map = (Map<String, Object>)myList.getItemAtPosition(i);
                    String id_order = (String) map.get("id_order");
                    String id_tecnic = (String) map.get("fk_user_id");
                    showSimplePopUp(id_order);
                    return true;
                }

            });

        }
    }

    //////////******************POP UP**************//////////////
    private void showSimplePopUp(final String idped) {

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Eliminar");
        helpBuilder.setMessage("Realmente desea elimiar el pedido"+idped);
        helpBuilder.setPositiveButton("Si",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        deleteSync(idped);
                    }
                });
        helpBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("no");
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
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Sincronizando Pedidos, espere un momento............");
        prgDialog.setCancelable(false);
        prgDialog.show();

        ArrayList token = controller.tokenExp();

        if (token != null){
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            client.addHeader("Content-type", "application/json;charset=utf-8");
            client.addHeader("Authorization", token.get(3).toString());
            client.get(Constans.API_END + Constans.ORDERS, params, new AsyncHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Throwable error,
                                      String content) {
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

                @Override
                public void onSuccess(String response) {
                    System.out.println(response);
                    prgDialog.hide();
                    updateSQLite(response);
                }
            });
        }


    }


    /////////////////////////////*******************ACTUALIZA SQLITE*********************////////////////////
    public void updateSQLite(String response){
        Gson gson = new GsonBuilder().create();
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

                    controller.insert_order(queryValues);


                }
//
                updateMySQLSyncSts(arr);
            }else {
                Toast.makeText(getApplicationContext(), "No Tiene Pedidos Para Sincronizar",
                        Toast.LENGTH_LONG).show();
                prgDialog.hide();
                send_remito();
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //////////////***********send update sync: true*************************//////////////
    public void updateMySQLSyncSts(JSONArray json){
        System.out.println(json);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        ArrayList user_id = controller.tokenExp();

        client.addHeader("Content-type", "application/json;charset=utf-8");
        client.addHeader("Authorization", user_id.get(3).toString());

        ArrayList<HashMap<String, String>> userList =  controller.get_orders(user_id.get(0).toString());

        if (userList.size() != 0) {
            params.put("_json", userList);
            client.post(Constans.API_END + Constans.SYNC, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Toast.makeText(getApplicationContext(), "Se ha informado al supervisor de la sincronización", Toast.LENGTH_LONG).show();
                    System.out.print(response);
                    send_remito();
                    reloadActivity();
                }
                @Override
                public void onFailure(int statusCode, Throwable error,String content) {
                    Toast.makeText(getApplicationContext(), "ups! ocurrio un error", Toast.LENGTH_LONG).show();
                    System.out.print(error);
                    send_remito();
                    reloadActivity();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No tiene Pedidos pendientes", Toast.LENGTH_LONG).show();
            send_remito();
        }





    }

    //////////////***********Update Sync: false and dele SQlite bd Order*************************//////////////
    public void deleteSync(final String idped) {
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Eliminando Pedido............");
        prgDialog.setCancelable(false);
        prgDialog.show();

        ArrayList token = controller.tokenExp();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.addHeader("Content-type", "application/json;charset=utf-8");
        client.addHeader("Authorization", token.get(3).toString());

        Gson gson = new GsonBuilder().create();
        ArrayList<HashMap<String, String>> usersynclist;
        usersynclist = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", idped);
        usersynclist.add(map);
        String json = gson.toJson(usersynclist);
        client.put(Constans.API_END + Constans.DSYNC + idped, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(getApplicationContext(), "Se ha informado al supervisor de la sincronización", Toast.LENGTH_LONG).show();
                prgDialog.hide();
                controller.elim_aux(idped);
                reloadActivity();

            }
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
                System.out.print(statusCode);
                prgDialog.hide();
            }
        });
    }


    public void send_remito()
    {

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Enviando y Recibiendo Pedidos Pendientes, espere un momento............");
        prgDialog.setCancelable(false);
        Gson gson = new GsonBuilder().create();
        prgDialog.show();
        enviaremito();
//        ArrayList<HashMap<String, String>> aux_pen= controller.conidsop();
        ////////////ENVIA PEDIDOS CREADOS MANUALMENTE////////////////////////
//        if(aux_pen.size()!=0)
//        {
//            String new_ped = gson.toJson(aux_pen);
//            send_aux_ped(new_ped);
//        }else{enviaremito();}



    }


    //////////////***********ENVIA AUX_PEDIDOS NUEVOS*************************//////////////
    public void send_aux_ped(String json) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("aux_ped", json);

        try {
            client.setTimeout(40000);
            client.post("http://blueboxcol.com/dipzotecnico/detalles_pedidov7/aux_pedidos.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                enviaremito();
            }
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                Toast.makeText(getApplicationContext(), "ups! ocurrio un error en pedidos Nuevos", Toast.LENGTH_LONG).show();
                prgDialog.hide();
            }
        });}catch (Exception e){}
    }


////////////////////*****************REVISA SI TIENE PEDIDOS NUEVOS*********//////////////////

    private void enviaremito(){
        ArrayList<HashMap<String, String>> pendiente= controller.get_refferals();

        HashMap<String, String> map = new HashMap<String, String>();
        Gson gson = new Gson();

        int i=0;
        if(pendiente.size()!=0 ) {
            for (HashMap<String, String> hashMap : pendiente) {
                i=i+1;
                ArrayList<HashMap<String, String>> things =  controller.getdisp(hashMap.get("fk_id_order"));
                ArrayList<HashMap<String, String>> referral = controller.get_referral(hashMap.get("fk_id_order"));
                pendientes(hashMap.get("fk_id_order"), things, referral);
            }


        }else{
            prgDialog.hide();
            reloadActivity();
        }

    }


    //////////////***********ENVIO DE REMITOS*************************//////////////
    public void pendientes(final String id_order, ArrayList<HashMap<String, String>> thigs, ArrayList<HashMap<String, String>> referral) {
        ArrayList token = controller.tokenExp();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.addHeader("Content-type", "application/json;charset=utf-8");
        client.addHeader("Authorization", token.get(3).toString());
        params.put("order",id_order);
        params.put("things", thigs);
        params.put("referral", referral);

        try {
            client.setTimeout(40000);
            client.post(Constans.API_END + "/referrals", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    System.out.println(response);
                    controller.elim_aux(id_order);


//                    ArrayList<HashMap<String, String>> pendiente = controller.get_refferals();
//                    for (HashMap<String, String> hashMap : pendiente) {
//                        controller.elim_aux(hashMap.get("fk_id_order"));
//
//                    }
                    contadores();
                    prgDialog.hide();
                    Toast.makeText(getApplicationContext(), "Remitos enviados satisfactoriamente", Toast.LENGTH_LONG).show();
                    reloadActivity();
                }

                @Override
                public void onFailure(int statusCode, Throwable error,
                                      String content) {
                    prgDialog.hide();
                    Toast.makeText(getApplicationContext(), "ups! ocurrio un error en remitos enviados", Toast.LENGTH_LONG).show();
                    reloadActivity();
                }
            });
        }catch (Exception e){}

    }

    //////////////***********CONTADORES*************************//////////////

    public void contadores()
    {
        ArrayList<HashMap<String, String>> pendiente= controller.get_refferals();
        contador.setText(String.valueOf(pendiente.size()));
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
