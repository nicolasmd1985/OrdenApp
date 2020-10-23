package dipzo.ordenapp.tecnic.Controllers;

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
import dipzo.ordenapp.tecnic.Constans;
import dipzo.ordenapp.tecnic.Sqlite.DBController;
import dipzo.ordenapp.tecnic.Sqlite.users;

public class customer_controller {

    private Context context;
    private ProgressDialog prgDialog;



    public customer_controller(Context context){
        super();
        this.context = context;
    }


    public String customer_request() {

        users users = new users(context);

        ArrayList token = users.tokenExp();
        if (token != null){

            prgDialog = new ProgressDialog(context);
            prgDialog.setMessage("Actualizando BD de Clientes");
            prgDialog.setCancelable(true);
            prgDialog.show();
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            client.setBearerAuth(token.get(3).toString());
            client.get(Constans.API_END + Constans.CUSTOMERS, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String str = new String(responseBody, "UTF-8");
                        save_customers(str);
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


    private void save_customers(String response){
        DBController controller = new DBController(context);
        HashMap<String, String> queryValues;
        try {
            JSONArray arr = new JSONArray(response);
            if(arr.length() != 0){

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = (JSONObject) arr.get(i);
                    queryValues = new HashMap<String, String>();
                    queryValues.put("customer_id", obj.get("customer_id").toString());
                    queryValues.put("first_name", obj.get("first_name").toString());
                    queryValues.put("last_name", obj.get("last_name").toString());
                    queryValues.put("email", obj.get("email").toString());
                    queryValues.put("phone_number", obj.get("phone_number_1").toString());
                    queryValues.put("city", obj.get("city").toString());
                    controller.insertCustomers(queryValues);
                }
            }else {
                prgDialog.hide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
