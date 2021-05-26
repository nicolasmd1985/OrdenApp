package dipzo.ordenapp.orders.Controllers;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import dipzo.ordenapp.orders.Constans;
import dipzo.ordenapp.orders.Sqlite.DBController;
import dipzo.ordenapp.orders.Sqlite.users;

public class auto_referral {
    private Context context;

    public auto_referral(Context context) {
        super();
        this.context = context;
    }

    public void send_auto_referral(ArrayList<HashMap<String, String>> pending, String path){
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
        client.setTimeout(200000);
        params.put("order",id_order);
        params.put("things", thigs);
        params.put("referral", referral);

        client.post(Constans.API_END + "/referrals", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                controller.elim_aux(id_order);
                count_referrals count_referrals = new count_referrals(context);
                Toast.makeText(context, "Ordenes enviadas satisfactoriamente", Toast.LENGTH_LONG).show();
                String suma = String.valueOf(count_referrals.count());
                Toast.makeText(context, "Tiene "+ suma+ " ordenes finalizadas por enviar",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
                    Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(context, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Dispositivo sin conexión a Internet",
                            Toast.LENGTH_LONG).show();
                }
            }


        });


    }
}
