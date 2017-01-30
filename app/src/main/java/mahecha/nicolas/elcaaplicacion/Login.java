package mahecha.nicolas.elcaaplicacion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import mahecha.nicolas.elcaaplicacion.GPS.ServicioGPS2;
import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;

public class Login extends Activity implements View.OnClickListener{


    private EditText user, pass;
    private Button mSubmit;
    ProgressDialog prgDialog;
    HashMap<String, String> queryValues;

    DBController controller = new DBController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        mSubmit = (Button) findViewById(R.id.login);
        mSubmit.setOnClickListener(this);

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Conectando......");
        prgDialog.setCancelable(false);



    }




    @Override
    public void onClick(View view) {
        registro();
    }


    ////////////**********************FUNCION DE INGRESO**********************/////////
    public void registro()
    {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        String username = user.getText().toString();
        String password = pass.getText().toString();


        prgDialog.show();
        params.add("username", username);
        params.add("password", password);
        client.post("http://elca.sytes.net:2122/app_elca/logintecnicosV2/logintec.php", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                prgDialog.hide();
                try {
                    int success;
                    JSONArray arr = new JSONArray(response);
                    int z=0;
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = (JSONObject) arr.get(i);
                        success = Integer.parseInt(obj.get("success").toString());


                        if (success==1)
                        {

                            ArrayList<HashMap<String, String>> userList =  controller.getUsers();

                            if(userList.size()!=0) {

                                for (HashMap<String, String> hashMap : userList) {

                                    if ((obj.get("Nombre").toString().equals(hashMap.get("nombre"))) && (obj.get("Apellido").toString().equals(hashMap.get("apellido")))) {

                                        bdregistro();
                                        z = z + 1;
                                    }

                                }

                            }

                            if(z!=1)
                            {

                                queryValues = new HashMap<String, String>();
                                queryValues.put("nombre", obj.get("Nombre").toString());
                                queryValues.put("apellido", obj.get("Apellido").toString());
                                queryValues.put("usuario", obj.get("Usuario").toString());
                                queryValues.put("pass", obj.get("pass").toString());
                                queryValues.put("tecnico", obj.get("tecnico").toString());
                                controller.insertUsers(queryValues);

                                Intent x = new Intent(Login.this, Pedidos.class);
                                Toast.makeText(Login.this, obj.get("message").toString(), Toast.LENGTH_LONG).show();
                                x.putExtra("idusuario", obj.get("tecnico").toString() );
                                prgDialog.dismiss();
                                startActivity(x);
                            }




                        } else {

                            Toast.makeText(Login.this, obj.get("message").toString(), Toast.LENGTH_LONG).show();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {

                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Dispositivo Sin Conexión a Internet",
                            Toast.LENGTH_LONG).show();
                }
                prgDialog.hide();

                bdregistro();

            }

        });
    }



    private void bdregistro() {

        String username = user.getText().toString();
        String password = pass.getText().toString();


        ArrayList<HashMap<String, String>> userList =  controller.getUsers();
        if(userList.size()!=0) {
            ArrayList<HashMap<String, String>> loginlist = controller.login();
            int i=0;
            for (HashMap<String, String> hashMap : loginlist) {

                if (username.equals(hashMap.get("usuario"))&&password.equals(hashMap.get("pass"))) {

                    Intent x = new Intent(Login.this, Pedidos.class);
                    Toast.makeText(getApplicationContext(), "Login Correcto", Toast.LENGTH_LONG).show();
                    x.putExtra("idusuario",hashMap.get("idusuario")  );
                    startActivity(x);

                }else{i++;}
                if(userList.size()==i){Toast.makeText(getApplicationContext(), "Usuario ó Contraseña Incorrecta", Toast.LENGTH_LONG).show();}

            }
        }else{Toast.makeText(getApplicationContext(), "No existe ningun usuario registrado", Toast.LENGTH_LONG).show();}
     }
}


