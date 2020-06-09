package mahecha.nicolas.elcaaplicacion.Controllers;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import mahecha.nicolas.elcaaplicacion.Constans;
import mahecha.nicolas.elcaaplicacion.Login;
import mahecha.nicolas.elcaaplicacion.Pedidos;
import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;
import mahecha.nicolas.elcaaplicacion.Sqlite.users;

public class update_user_status {
    private Context context;
    HashMap<String, String> queryValues;
    public update_user_status(Context context) {
        super();
        this.context = context;
    }


    //////////////***********ENVIO DE REMITOS*************************//////////////
    public void updateUserStatus() {
        users users = new users(context);


        ArrayList token = users.tokenExp();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.setBearerAuth(token.get(3).toString());
        String user_id = token.get(0).toString();

        queryValues = new HashMap<>();
        queryValues.put("user_id", user_id);
        queryValues.put("status_id", "201");
        System.out.println(users.updateStatusUser(queryValues));

        params.put("status_id", 201);


        try {
            client.put(Constans.API_END + "/users/" + user_id, params,   new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(context, "usuario actualizado correctamente", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(context, "ups! ocurrio un error", Toast.LENGTH_LONG).show();
                    try {
                        String str = new String(responseBody, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }


            });
        }catch (Exception e){}

    }
}
