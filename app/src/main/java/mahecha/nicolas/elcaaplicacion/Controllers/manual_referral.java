package mahecha.nicolas.elcaaplicacion.Controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
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

    public void send_manual_order(final HashMap<String, String> json)

    {
        users users = new users(context);


        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        ArrayList user_id = users.tokenExp();
        client.setBearerAuth(user_id.get(3).toString());


        params.put("aux_order", json);

        try {
            client.post(Constans.API_END + Constans.ORDERS, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String str = new String(responseBody, "UTF-8");
                        orders orders = new orders(context);
                        count_referrals count_referrals = new count_referrals(context);
                        orders.update_aux_order(str, json.get("id_order"));
                        Toast.makeText(context, "Las ordenes manuales han sido sincronizadas", Toast.LENGTH_LONG).show();
                        String suma = String.valueOf(count_referrals.count());
                        Toast.makeText(context, "Tiene "+ suma+ " ordenes finalizas por enviar",
                                Toast.LENGTH_LONG).show();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(context, "ups! ocurrio un error en pedidos Nuevos", Toast.LENGTH_LONG).show();

                }

            });}catch (Exception e){}
    }


    public void send_manual_referral(ArrayList<HashMap<String, String>> pending, String path){
        DBController controller = new DBController(context);
        uploader upImage = new uploader(context);
        resize_image resizeImage = new resize_image();

        int i=0;
        if(pending.size()!=0 ) {
            for (HashMap<String, String> hashMap : pending) {
                i=i+1;
                ArrayList<HashMap<String, String>> things =  controller.getdisp(hashMap.get("fk_order_id"));
                ArrayList<HashMap<String, String>> referral = controller.get_referral(hashMap.get("fk_order_id"));
                if(things.size()!=0 ) {
                    for (int j=0; j < things.size(); j++ ){
                        if (things.get(j).get("photos") != null){
                            String id_order = things.get(j).get("fk_order_id");
                            String code = things.get(j).get("code_scan");
                            String storageDir = path + "/" + id_order + "/" + code;
                            File directory = new File(storageDir);
                            File[] files = directory.listFiles();
                            if (files != null){
                                for (int k = 0; k < files.length; k++)
                                {
                                    upImage.uploadtos3(context, resizeImage.saveBitmapToFile(files[k]));
                                }
                            }
                        }
                    }

                }
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
        client.setBearerAuth(token.get(3).toString());
        params.put("order",aux_order);
        params.put("things", thigs);
        params.put("referral", referral);

        try {
            client.post(Constans.API_END + "/referrals", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String str = new String(responseBody, "UTF-8");
                        controller.elim_aux(id_order);
                        count_referrals count_referrals = new count_referrals(context);
                        Toast.makeText(context, "Ordenes finalizadas enviadas satisfactoriamente", Toast.LENGTH_LONG).show();
                        String suma = String.valueOf(count_referrals.count());
                        Toast.makeText(context, "Tiene "+ suma+ " ordenes finalizas por enviar",
                                Toast.LENGTH_LONG).show();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(context, "ups! ocurrio un error en ordenes finalizadas enviados", Toast.LENGTH_LONG).show();

                }

            });
        }catch (Exception e){}

    }
}
