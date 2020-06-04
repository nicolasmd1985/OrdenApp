package mahecha.nicolas.elcaaplicacion;

import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;
import mahecha.nicolas.elcaaplicacion.android.IntentIntegrator;
import mahecha.nicolas.elcaaplicacion.android.IntentResult;

public class thing_detail extends AppCompatActivity {

    DBController controller = new DBController(this);
    EditText codigo, nombre, descripcion, latitud, longitud, tiemp;
    private Button camera;
    String id_order, id_tecnic;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thing_detail);

        id_tecnic = getIntent().getStringExtra("id_tecnic");
        id_order = getIntent().getStringExtra("id_order");

        camera = (Button) findViewById(R.id.camera);
        codigo = (EditText) findViewById(R.id.codigo);
        nombre = (EditText) findViewById(R.id.nomdisp);
        descripcion = (EditText) findViewById(R.id.descripcion);
        latitud = (EditText) findViewById(R.id.latitud);
        longitud = (EditText) findViewById(R.id.longitud);
        tiemp = (EditText) findViewById(R.id.tiempo);
        tiemp.setText(tiempo());
        tiemp.setText(tiempo());

        codigo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 3){
                    camera.setEnabled(true);
                }else {camera.setEnabled(false);}
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


    }


    //////////////////************************CLICK SCANNEAR**************///////////
    public void camera_scan(View v) {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    ///////////////////////*******************OBTENER TIEMPO**************/////////////////

    public String tiempo()
    {
        Date date = new Date();
        CharSequence s  = DateFormat.format("d/M/yyyy H:m", date.getTime());
        String time = s.toString();
        return time ;
    }


  ////////////////////////////*******************CALL HOME*********************///////////////
    public void callHomeActivity(View view) {
        Intent objIntent = new Intent(getApplicationContext(),
                Agregar_dispositivos.class);
        objIntent.putExtra("id_order", id_order );
        objIntent.putExtra("id_tecnic",id_tecnic );
        startActivity(objIntent);
    }


    /////////////************************OBTIENE INFO DEL SCANER*****************////////////////
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            codigo.setText(scanContent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }


    ////////////////*********************CLICK EN EL BOTON*************////////////
    public void adddip(View view) {

//
        ArrayList listgps = controller.getgps();

        HashMap<String, String> queryValues = new HashMap<String, String>();
        queryValues.put("code_scan", codigo.getText().toString());
        queryValues.put("name", nombre.getText().toString());
        queryValues.put("description", descripcion.getText().toString());
        queryValues.put("latitude",listgps.get(1).toString());
        queryValues.put("longitude", listgps.get(0).toString());
        queryValues.put("time_install", tiempo());
        queryValues.put("fk_order_id", id_order);

        ArrayList<String> list = new ArrayList<String>();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/" + id_order + "/" + codigo.getText().toString() );
        String path = String.valueOf(storageDir);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null){
            String[] Idmagenes = new String[files.length];
            for (int i = 0; i < files.length; i++)
            {
                Idmagenes[i] = files[i].getName();
            }
            Arrays.sort(Idmagenes, Collections.reverseOrder());
            for (int j = 0; j<Idmagenes.length; j++){
                list.add(Idmagenes[j]);
            }
        }

        Gson gson = new Gson();

        queryValues.put("photos", String.valueOf(gson.toJson(list)));
        System.out.println(gson.toJson(list));

        controller.inserdips(queryValues);
        this.callHomeActivity(view);

    }



    ////////////********************CANCELAR************///////////////////////
    public void canceldisp(View view) {
        this.callHomeActivity(view);
    }

    /////////////////****************ESTO ES PARA DEVOLVERSE*****************/////////////////////

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            Intent i = new Intent(thing_detail.this, Agregar_dispositivos.class);
            i.putExtra("id_order", id_order );
            i.putExtra("id_tecnic",id_tecnic );
            startActivity(i);
            //return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    ////////////////*********************CLICK EN EL BOTON*************////////////
    public void evidence_intent(View view) {

        Intent i = new Intent(thing_detail.this, camera_evidence.class);
        i.putExtra("id_order", id_order );
        i.putExtra("id_tecnic",id_tecnic );
        i.putExtra("codigo", codigo.getText().toString() );
        startActivity(i);
    }




}
