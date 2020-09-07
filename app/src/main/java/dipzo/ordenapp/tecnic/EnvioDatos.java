package dipzo.ordenapp.tecnic;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import dipzo.ordenapp.tecnic.Constans;
import dipzo.ordenapp.tecnic.Sqlite.DBController;
import dipzo.ordenapp.tecnic.Sqlite.users;

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
            client.setBearerAuth(token.get(3).toString());
            RequestParams params = new RequestParams();
            params.add("latitude", lat);
            params.add("longitude", lon);
            try{
                client.post(Constans.API_END + Constans.SEND_GPS , params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(context, "Ubicacion Enviada",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    }

                });}catch (Exception e){}
        }



    }



}
