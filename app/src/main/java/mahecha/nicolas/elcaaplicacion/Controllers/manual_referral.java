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
                }
            });}catch (Exception e){}
    }


    public void send_manual_referral(ArrayList<HashMap<String, String>> pending){
        DBController controller = new DBController(context);

        int i=0;
        if(pending.size()!=0 ) {
            for (HashMap<String, String> hashMap : pending) {
                i=i+1;
                ArrayList<HashMap<String, String>> things =  controller.getdisp(hashMap.get("fk_order_id"));
                ArrayList<HashMap<String, String>> referral = controller.get_referral(hashMap.get("fk_order_id"));
                pendientes(hashMap.get("fk_order_id"), things, referral, hashMap.get("aux_order"));
            }

        }
    }

    //////////////***********ENVIO DE REMITOS*************************//////////////
    public void pendientes(final String id_order, final ArrayList<HashMap<String, String>> thigs, ArrayList<HashMap<String, String>> referral, String aux_order) {
        final DBController controller = new DBController(context);
        users users = new users(context);

        ArrayList token = users.tokenExp();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.addHeader("Content-type", "application/json;charset=utf-8");
        client.addHeader("Authorization", token.get(3).toString());
        params.put("order",aux_order);
        params.put("things", thigs);
        params.put("referral", referral);

        try {
            client.post(Constans.API_END + "/referrals", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    controller.elim_aux(id_order);
                    System.out.println(response);
                    count_referrals count_referrals = new count_referrals(context);
                    Toast.makeText(context, "Remitos enviados satisfactoriamente", Toast.LENGTH_LONG).show();
                    String suma = String.valueOf(count_referrals.count());
                    Toast.makeText(context, "Tiene "+ suma+ " ordenes finalizas por enviar",
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, Throwable error,
                                      String content) {
                    Toast.makeText(context, "ups! ocurrio un error en remitos enviados", Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception e){}

    }
}
