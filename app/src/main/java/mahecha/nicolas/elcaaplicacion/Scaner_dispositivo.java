package mahecha.nicolas.elcaaplicacion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;
import mahecha.nicolas.elcaaplicacion.android.IntentIntegrator;
import mahecha.nicolas.elcaaplicacion.android.IntentResult;

public class Scaner_dispositivo extends AppCompatActivity implements View.OnClickListener {

    DBController controller = new DBController(this);
    EditText codigo, nombre, descripcion, latitud, longitud, tiemp;
    private Button scanBtn;
    String idped, idusuar, lat,lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scaner_dispositivo);
        idusuar = getIntent().getStringExtra("idusuario");
        scanBtn = (Button) findViewById(R.id.scan_button);
        codigo = (EditText) findViewById(R.id.codigo);
        nombre = (EditText) findViewById(R.id.nomdisp);
        descripcion = (EditText) findViewById(R.id.descripcion);
        latitud = (EditText) findViewById(R.id.latitud);
        longitud = (EditText) findViewById(R.id.longitud);
        tiemp = (EditText) findViewById(R.id.tiempo);
        scanBtn.setOnClickListener(this);
        idped = getIntent().getStringExtra("idpedido");
        tiemp.setText(tiempo());

    }


    //////////////////************************CLICK SCANNEAR**************///////////
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.scan_button){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
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
        objIntent.putExtra("idpedido", idped );
        objIntent.putExtra("idusuario",idusuar );
        startActivity(objIntent);
    }


    /////////////************************OBTIENE INFO DEL SCANER*****************////////////////
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            if(scanContent.length()>15)
            {
                descripcion.setText(scanContent);
            }else {codigo.setText(scanContent);}
  }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    ////////////////*********************CLICK EN EL BOTON*************////////////
    public void adddip(View view) {


        ArrayList<HashMap<String,String>> listgps = controller.getgps();
        for (HashMap<String, String> hashMap : listgps) {
                lon = hashMap.get("longitud");
                lat = hashMap.get("latitud");
            System.out.println(lat+"-"+lon);
        }

        HashMap<String, String> queryValues = new HashMap<String, String>();
        queryValues.put("codigo", codigo.getText().toString());
        queryValues.put("nombre", nombre.getText().toString());
        queryValues.put("descripcion", descripcion.getText().toString());
        queryValues.put("latitud",lat);
        queryValues.put("longitud", lon);
        queryValues.put("tiempo", tiempo());
        queryValues.put("fkidpedido", idped);
        controller.inserdips(queryValues);
        this.callHomeActivity(view);
    }



    ////////////********************CANCELAR************///////////////////////
    public void canceldisp(View view) {
        this.callHomeActivity(view);
    }




}
