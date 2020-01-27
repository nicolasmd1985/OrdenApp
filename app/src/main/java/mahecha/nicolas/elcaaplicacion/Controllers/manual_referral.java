package mahecha.nicolas.elcaaplicacion.Controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;

import mahecha.nicolas.elcaaplicacion.Constans;
import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;
import mahecha.nicolas.elcaaplicacion.Sqlite.users;
import mahecha.nicolas.elcaaplicacion.Sqlite.orders;

public class manual_referral {
    private Context context;
    private ProgressDialog prgDialog;

    public manual_referral(Context context) {
        super();
        this.context = context;
    }

    public void send_manual_order(final HashMap<String, String> json){
        users users = new users(context);


        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        ArrayList user_id = users.tokenExp();

        client.addHeader("Content-type", "application/json;charset=utf-8");
        client.addHeader("Authorization", user_id.get(3).toString());

        params.put("aux_order", json);

        try {
            client.post(Constans.API_END + Constans.ORDERS, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    orders orders = new orders(context);
                    orders.update_aux_order(response, json.get("id_order"));

                }
                @Override
                public void onFailure(int statusCode, Throwable error,
                                      String content) {
                    Toast.makeText(context, "ups! ocurrio un error en pedidos Nuevos", Toast.LENGTH_LONG).show();
                    prgDialog.hide();
                }
            });}catch (Exception e){}
    }
}
