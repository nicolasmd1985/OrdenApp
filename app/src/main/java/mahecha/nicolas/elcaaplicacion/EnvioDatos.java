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
import mahecha.nicolas.elcaaplicacion.Sqlite.users;

/**
 * Created by nicolas on 27/04/2017.
 * ENVIO Y RECEPCION DE DATOS DE LA UBICACON GPS DEL TECNICO
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
        users users = new users(context);

        String lon=null,lat=null;
        ArrayList<HashMap<String,String>> listgps = dbController.getgps();
        lon = String.valueOf(listgps.get(0));
        lat = String.valueOf(listgps.get(1));


        ArrayList token = users.tokenExp();

        if (token != null){
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Content-type", "application/json;charset=utf-8");
            client.addHeader("Authorization", token.get(3).toString());
            RequestParams params = new RequestParams();
            params.add("latitude", lat);
            params.add("longitude", lon);
            try{
                client.post(Constans.API_END + Constans.SEND_GPS , params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        Toast.makeText(context, "Ubicacion Enviada",
                                Toast.LENGTH_LONG).show();
                        System.out.println(response);
                    }
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        System.out.println(statusCode);
                    }
                });}catch (Exception e){}
        }



    }



}
