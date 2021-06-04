package dipzo.ordenapp.orders.Controllers;


import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import dipzo.ordenapp.orders.Constans;

import dipzo.ordenapp.orders.Sqlite.users;
import dipzo.ordenapp.orders.Sqlite.substatuses_db;

public class substatuses {

    private Context context;
    private ProgressDialog prgDialog;



    public substatuses(Context context){
        super();
        this.context = context;
    }


    public String substatus_request(String status) {

        users users = new users(context);

        ArrayList token = users.tokenExp();
        if (token != null){

            prgDialog = new ProgressDialog(context);
            prgDialog.setMessage("Actualizando Estados");
            prgDialog.setCancelable(true);
            prgDialog.show();
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            client.setBearerAuth(token.get(3).toString());
            client.get(Constans.API_END + status, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String str = new String(responseBody, "UTF-8");
//                        System.out.println(str);
                        save_substatus(str);
                        prgDialog.hide();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    prgDialog.hide();
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


        return super.toString();
    }


    private void save_substatus(String response){
        substatuses_db substatuses_db = new substatuses_db(context);
        HashMap<String, String> queryValues;
        try {
            JSONArray arr = new JSONArray(response);
            if(arr.length() != 0){

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = (JSONObject) arr.get(i);
                    queryValues = new HashMap<String, String>();
                    queryValues.put("id_substatus", obj.get("id").toString());
                    queryValues.put("substatus_type", obj.get("substatus_type").toString());
                    queryValues.put("description", obj.get("description").toString());
                    queryValues.put("status_id", obj.get("status_id").toString());

                    substatuses_db.insertSubstatus(queryValues);
                }
            }else {
                prgDialog.hide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
