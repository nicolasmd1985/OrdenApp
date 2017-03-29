package mahecha.nicolas.elcaaplicacion;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
    ProgressDialog prgDialog;
    HashMap<String, String> queryValues;
    String idusuar;
    TextView contador;
    int rems=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        idusuar = getIntent().getStringExtra("idusuario");
        Intent GPS = new Intent(Pedidos.this, ServicioGPS2.class);
        cargabdl(idusuar);
        GPS.putExtra("Tecnico",idusuar);
        startService(GPS);
        contador=(TextView)findViewById(R.id.contador);
        contadores();


    }
    ////////////////////******************AGREGA PEDIDO******************//////////////////

    public void addPedidos(View view) {
        Intent objIntent = new Intent(getApplicationContext(), Nuevo_pedido.class);
        objIntent.putExtra("idusuario",idusuar );
        startActivity(objIntent);
    }

    ////////////////*****************CARGA BASE DE DATOS SQLITE************************
    public void cargabdl(final String idusuar)
    {
        ArrayList<HashMap<String, String>> userList =  controller.get_auxped(idusuar);
        if(userList.size()!=0) {
            ListAdapter adapter = new SimpleAdapter(Pedidos.this, userList, R.layout.view_pedidos, new String[]{"cliente", "descripcion"}, new int[]{R.id.clieteid, R.id.detalleid});
            final ListView myList = (ListView) findViewById(android.R.id.list);
            myList.setAdapter(adapter);
            myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View view, int i, long l) {

                    Map<String, Object> map = (Map<String, Object>) myList.getItemAtPosition(i);
                    String idpedido = (String) map.get("idauxpedido");
                    Intent x = new Intent(Pedidos.this, Detalles_pedido.class);
                    x.putExtra("idpedido", idpedido);
                    x.putExtra("idusuario", idusuar);
                    startActivity(x);
                }
            });
            myList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView adapterView, View view, int i, long l) {
//                    Toast.makeText(getApplicationContext(), "presiono" + i, Toast.LENGTH_SHORT).show();
                    Map<String, Object> map = (Map<String, Object>)myList.getItemAtPosition(i);
                    String idpedido = (String) map.get("idauxpedido");
//                    System.out.println(idpedido);
                    showSimplePopUp(idpedido);

                    return true;
                }

            });

        }
    }

    //////////******************POP UP**************//////////////
    private void showSimplePopUp(final String idped) {

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Eliminar");
        helpBuilder.setMessage("Realmente desea elimiar el pedido");
        helpBuilder.setPositiveButton("Si",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        updatestado(idped);
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


    //////////////***********actualiza status del estado*************************//////////////

    public void updatestado(final String idped) {
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Eliminando Pedido............");
        prgDialog.setCancelable(false);
        prgDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        Gson gson = new GsonBuilder().create();
        ArrayList<HashMap<String, String>> usersynclist;
        usersynclist = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Id", idped);
        usersynclist.add(map);
        String json = gson.toJson(usersynclist);
        params.put("estado", json);
        client.post("http://elca.sytes.net:2122/app_elca/detalles_pedidov7/deletepedido.php", params, new AsyncHttpResponseHandler() {
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
                prgDialog.hide();
            }
        });
    }


    ///////////////////////////********RECARGA ACTIVIDAD//////////////////
    public void reloadActivity() {
        Intent objIntent = new Intent(getApplicationContext(), Pedidos.class);
        objIntent.putExtra("idusuario",idusuar );
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

    //////////////************************OBTIENE DATOS DE PHP************************


    public void syncSQLiteMySQLDB() {
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Sincronizando Pedidos, espere un momento............");
        prgDialog.setCancelable(false);
        prgDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("idusuar", idusuar);
        client.post("http://elca.sytes.net:2122/app_elca/detalles_pedidov7/get_pedido.php", params, new AsyncHttpResponseHandler() {
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
                updateSQLite(response);
            }

        });
    }



    /////////////////////////////*******************ACTUALIZA SQLITE*********************////////////////////

    public void updateSQLite(String response){
        ArrayList<HashMap<String, String>> usersynclist;
        usersynclist = new ArrayList<HashMap<String, String>>();
        Gson gson = new GsonBuilder().create();
        try {
            JSONArray arr = new JSONArray(response);
            if(arr.length() != 0){
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = (JSONObject) arr.get(i);
                    queryValues = new HashMap<String, String>();
                    queryValues.put("idauxpedido", obj.get("idauxpedido").toString());
                    queryValues.put("idtecnico", obj.get("idtecnico").toString());
                    queryValues.put("descripcion", obj.get("descripcion").toString());
                    queryValues.put("idnumsoporte", obj.get("idnumsoporte").toString());
                    queryValues.put("cliente", obj.get("cliente").toString());
                    queryValues.put("calle", obj.get("calle").toString());
                    queryValues.put("numero", obj.get("numero").toString());
                    queryValues.put("ciudad", obj.get("ciudad").toString());
                    queryValues.put("provincia", obj.get("provincia").toString());
                    queryValues.put("fechacr", obj.get("fechacr").toString());
                    queryValues.put("fechack", obj.get("fechack").toString());
                    controller.inser_auxped(queryValues);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("Id", obj.get("idauxpedido").toString());
                    map.put("status", "1");
                    usersynclist.add(map);
                }
                updateMySQLSyncSts(gson.toJson(usersynclist));
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

    //////////////***********actualiza status del estado*************************//////////////

    public void updateMySQLSyncSts(String json) {
        System.out.println(json);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        ArrayList<HashMap<String, String>> userList = controller.get_auxped(idusuar);

        if (userList.size() != 0) {
            params.put("estado", json);
            client.post("http://elca.sytes.net:2122/app_elca/detalles_pedidov7/updatesyncsts.php", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Toast.makeText(getApplicationContext(), "Se ha informado al supervisor de la sincronización", Toast.LENGTH_LONG).show();
                    send_remito();
                }
                @Override
                public void onFailure(int statusCode, Throwable error,String content) {
                    Toast.makeText(getApplicationContext(), "ups! ocurrio un error", Toast.LENGTH_LONG).show();
                    send_remito();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No tiene Pedidos pendientes", Toast.LENGTH_LONG).show();
            send_remito();
        }

    }


    public void send_remito()
    {

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Enviando y Recibiendo Pedidos Pendientes, espere un momento............");
        prgDialog.setCancelable(false);
        Gson gson = new GsonBuilder().create();
        prgDialog.show();
        ArrayList<HashMap<String, String>> aux_pen= controller.conidsop();
        //////////////ENVIA PEDIDOS CREADOS MANUALMENTE////////////////////////
        if(aux_pen.size()!=0)
        {
            String new_ped = gson.toJson(aux_pen);
            send_aux_ped(new_ped);
        }else{enviaremito();}



    }


    //////////////***********ENVIA AUX_PEDIDOS NUEVOS*************************//////////////
    public void send_aux_ped(String json) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("aux_ped", json);
        client.post("http://elca.sytes.net:2122/app_elca/detalles_pedidov7/aux_pedidos.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                enviaremito();
            }
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                Toast.makeText(getApplicationContext(), "ups! ocurrio un error", Toast.LENGTH_LONG).show();
                prgDialog.hide();
            }
        });
    }



////////////////////*****************REVISA SI TIENE PEDIDOS NUEVOS*********//////////////////

    private void enviaremito(){
        String nn = "";
        Gson gson = new GsonBuilder().create();
        ArrayList<HashMap<String, String>> pendiente= controller.consulrem();


        ArrayList<HashMap<String, String>> rem;
        rem = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> map = new HashMap<String, String>();

        int i=0;
        if(pendiente.size()!=0 ) {
            for (HashMap<String, String> hashMap : pendiente) {
                i=i+1;
                ArrayList<HashMap<String, String>> dispList =  controller.getdisp(hashMap.get("fkidauxpedido"));
                ArrayList<HashMap<String, String>> remlist = controller.getremito(hashMap.get("fkidauxpedido"));
                dispList.addAll(remlist);
                map.put(""+i,gson.toJson(dispList));
            }
            rem.add(map);
            nn= gson.toJson(rem);
            pendientes(nn,""+i);

        }else{
            prgDialog.hide();
            reloadActivity();
        }

    }



    //////////////***********ENVIO DE REMITOS*************************//////////////
    public void pendientes(String json,String i) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("remito", json);
        params.put("cont", i);
        client.setTimeout(30000);
        try {
            client.post("http://elca.sytes.net:2122/app_elca/detalles_pedidov7/remito_envia.php", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    //System.out.println(response);

                    ArrayList<HashMap<String, String>> pendiente = controller.consulrem();
                    for (HashMap<String, String> hashMap : pendiente) {
                        controller.elim_aux(hashMap.get("fkidauxpedido"));

                    }
                    contadores();
                    prgDialog.hide();
                    Toast.makeText(getApplicationContext(), "Remitos enviados satisfactoriamente", Toast.LENGTH_LONG).show();
                    reloadActivity();
                }

                @Override
                public void onFailure(int statusCode, Throwable error,
                                      String content) {
                    prgDialog.hide();
                    Toast.makeText(getApplicationContext(), "ups! ocurrio un error", Toast.LENGTH_LONG).show();
                    reloadActivity();
                }
            });
        }catch (Exception e){}

    }

    //////////////***********CONTADORES*************************//////////////

    public void contadores()
    {
        ArrayList<HashMap<String, String>> pendiente= controller.consulrem();
        contador.setText(String.valueOf(pendiente.size()));
    }


}
