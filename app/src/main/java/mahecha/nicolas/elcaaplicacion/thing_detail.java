package mahecha.nicolas.elcaaplicacion;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import mahecha.nicolas.elcaaplicacion.Controllers.uploader;
import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;
import mahecha.nicolas.elcaaplicacion.android.IntentIntegrator;
import mahecha.nicolas.elcaaplicacion.android.IntentResult;

public class thing_detail extends AppCompatActivity implements View.OnClickListener {

    DBController controller = new DBController(this);
    EditText codigo, nombre, descripcion, latitud, longitud, tiemp;
    private Button scanBtn;
    String id_order, id_tecnic;
    ImageView Imagetake;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thing_detail);

        id_tecnic = getIntent().getStringExtra("id_tecnic");
        id_order = getIntent().getStringExtra("id_order");

        scanBtn = (Button) findViewById(R.id.scan_button);
        codigo = (EditText) findViewById(R.id.codigo);
        nombre = (EditText) findViewById(R.id.nomdisp);
        descripcion = (EditText) findViewById(R.id.descripcion);
        latitud = (EditText) findViewById(R.id.latitud);
        longitud = (EditText) findViewById(R.id.longitud);
        tiemp = (EditText) findViewById(R.id.tiempo);
        Imagetake = (ImageView) findViewById(R.id.Imagetake);
        scanBtn.setOnClickListener(this);
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
        objIntent.putExtra("id_order", id_order );
        objIntent.putExtra("id_tecnic",id_tecnic );
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


        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = intent.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Imagetake.setImageBitmap(imageBitmap);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File filesDir = this.getFilesDir();
            File imageFile = new File(filesDir, imageFileName + ".jpg");

            OutputStream os;

            try {
                os = new FileOutputStream(imageFile);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();

            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
            }




        }
    }


    ////////////////*********************CLICK EN EL BOTON*************////////////
    public void adddip(View view) {


        ArrayList listgps = controller.getgps();

        HashMap<String, String> queryValues = new HashMap<String, String>();
        queryValues.put("code_scan", codigo.getText().toString());
        queryValues.put("name", nombre.getText().toString());
        queryValues.put("description", descripcion.getText().toString());
        queryValues.put("latitude",listgps.get(1).toString());
        queryValues.put("longitude", listgps.get(0).toString());
        queryValues.put("time_install", tiempo());
        queryValues.put("fk_order_id", id_order);
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



    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }
    }



    ////////////////*********************CLICK EN EL BOTON*************////////////
    public void camera_click(View view) {
        dispatchTakePictureIntent(123);
    }




}
