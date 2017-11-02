package mahecha.nicolas.elcaaplicacion;

import android.content.Context;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;

/**
 * Created by nicolas on 27/04/2017.
 */

public class EnvioDatos {

    Context context;
    HashMap<String, String> queryValues;


    public EnvioDatos(Context context){
        super();
        this.context = context;
    }

    public void enviar()
    {



        DBController dbController = new DBController(context);
        String lon=null,lat=null,dato=null;
        ArrayList<HashMap<String,String>> listgps = dbController.getgps();
        for (HashMap<String, String> hashMap : listgps) {
            lon = hashMap.get("longitud");
            lat = hashMap.get("latitud");

        }

        //Toast.makeText(context,lon+" "+lat,Toast.LENGTH_LONG).show();

        dato = dbController.idtecnico();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        wordList.add(queryValues);


        params.add("lat", lat);
        params.add("lon", lon);
        params.add("tecnico", dato);


         client.get("http://elca.sytes.net:2122/app_elca/ElcaGPS/getgpsauto.php", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    //Toast.makeText(context, "Ubicacion Enviada",
                     //       Toast.LENGTH_LONG).show();
                    //System.out.println(jrep);
                    //System.out.println(response);

                }
                @Override
                public void onFailure(int statusCode, Throwable error,
                                      String content) {
                    if (statusCode == 404) {
                        Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                    } else if (statusCode == 500) {
                        Toast.makeText(context, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Dispositivo Sin Conexión a Internet",
                                Toast.LENGTH_LONG).show();
                    }

                }
            });



    }



}
