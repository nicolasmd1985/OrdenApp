package mahecha.nicolas.elcaaplicacion.Controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import mahecha.nicolas.elcaaplicacion.Constans;
import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;
import mahecha.nicolas.elcaaplicacion.Sqlite.users;

public class auto_referral {
    private Context context;

    public auto_referral(Context context) {
        super();
        this.context = context;
    }

    public void send_auto_referral(ArrayList<HashMap<String, String>> pending){
        DBController controller = new DBController(context);

        int i=0;
        if(pending.size()!=0 ) {
            for (HashMap<String, String> hashMap : pending) {
                i=i+1;
                ArrayList<HashMap<String, String>> things =  controller.getdisp(hashMap.get("fk_order_id"));
                ArrayList<HashMap<String, String>> referral = controller.get_referral(hashMap.get("fk_order_id"));
                pendientes(hashMap.get("fk_order_id"), things, referral);
            }

        }
    }

    //////////////***********ENVIO DE REMITOS*************************//////////////
    public void pendientes(final String id_order, final ArrayList<HashMap<String, String>> thigs, ArrayList<HashMap<String, String>> referral) {
        final DBController controller = new DBController(context);
        users users = new users(context);

        ArrayList token = users.tokenExp();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.setBearerAuth(token.get(3).toString());
        params.put("order",id_order);
        params.put("things", thigs);
        params.put("referral", referral);

        try {
            client.post(Constans.API_END + "/referrals", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    controller.elim_aux(id_order);
                    count_referrals count_referrals = new count_referrals(context);
                    Toast.makeText(context, "Remitos enviados satisfactoriamente", Toast.LENGTH_LONG).show();
                    String suma = String.valueOf(count_referrals.count());
                    Toast.makeText(context, "Tiene "+ suma+ " ordenes finalizas por enviar",
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(context, "ups! ocurrio un error en remitos enviados", Toast.LENGTH_LONG).show();
                    try {
                        String str = new String(responseBody, "UTF-8");
                        System.out.println(str);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }


            });
        }catch (Exception e){}

    }
}
