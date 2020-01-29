package mahecha.nicolas.elcaaplicacion.Controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import mahecha.nicolas.elcaaplicacion.Constans;
import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;
import mahecha.nicolas.elcaaplicacion.Sqlite.users;

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
            prgDialog.setCancelable(false);
            prgDialog.show();
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            client.addHeader("Content-type", "application/json;charset=utf-8");
            client.addHeader("Authorization", token.get(3).toString());
            client.get(Constans.API_END + Constans.CUSTOMERS, params, new AsyncHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Throwable error,
                                      String content) {
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

                @Override
                public void onSuccess(String response) {
                    save_customers(response);
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
                    queryValues.put("phone_number", obj.get("phone_number").toString());
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
