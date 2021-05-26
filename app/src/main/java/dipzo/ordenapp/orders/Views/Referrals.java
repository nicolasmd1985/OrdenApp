package dipzo.ordenapp.orders.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dipzo.ordenapp.orders.Model.SubStatus;
import dipzo.ordenapp.orders.R;
import dipzo.ordenapp.orders.Sqlite.DBController;
import dipzo.ordenapp.orders.Sqlite.substatuses_db;

public class Referrals extends AppCompatActivity {


    Spinner status_spinner, sub_status_spinner;
    String id_order,id_tecnic, status_selected, sub_status_selected;
    private DrawingView drawView;
    EditText observaciones,aclaracion,email;
    DBController controller = new DBController(this);
    substatuses_db statusDb = new substatuses_db(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referals);
        id_order = getIntent().getStringExtra("id_order");
        id_tecnic = getIntent().getStringExtra("id_tecnic");

        drawView = (DrawingView)findViewById(R.id.drawing);
        observaciones = (EditText)findViewById(R.id.observaciones);
        aclaracion = (EditText)findViewById(R.id.aclaracion);
        email=(EditText)findViewById(R.id.email);

        status_spinner = (Spinner)findViewById(R.id.spinnerStatus);
        sub_status_spinner = (Spinner)findViewById(R.id.spinnerSubStatus);

        select_status();
    }


    public void savere()
    {
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Guardar Orden Finalizada");
        saveDialog.setMessage("Desea guardar la orden finalizada?");
        saveDialog.setPositiveButton("Si", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
            drawView.setDrawingCacheEnabled(true);
            Bitmap image = drawView.getDrawingCache();
            image = redimensionarImagenMaximo(image,200,55);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 50 , stream);
            byte[] byte_arr = stream.toByteArray();
            String encodedString = Base64.encodeToString(byte_arr, 0);
            HashMap<String, String> queryValues = new HashMap<String, String>();
            queryValues.put("id_order", id_order);
            queryValues.put("comment",observaciones.getText().toString());
            queryValues.put("full_name",aclaracion.getText().toString());
            queryValues.put("signature",encodedString);
            queryValues.put("final_time", tiempo());
            queryValues.put("email", email.getText().toString());
            queryValues.put("status_id", status_selected);
            queryValues.put("sub_status_id", sub_status_selected);
            controller.insert_referral(queryValues);
            controller.finish_order(Integer.valueOf(id_order));
            drawView.destroyDrawingCache();
            Intent i = new Intent(Referrals.this, Orders.class);
            i.putExtra("idpedido", id_order );
            i.putExtra("id_tecnic",id_tecnic );
            startActivity(i);
            }
        });
        saveDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        saveDialog.show();
    }


    ////////////////////***************OBTIENE TIEMPO**************///////////////////
    public String tiempo()
    {
        Date date = new Date();
        CharSequence s  = DateFormat.format("d/M/yyyy H:mm:ss", date.getTime());
        String time = s.toString();
        return time ;
    }

/////////////////***************REDIMENSIONA IMAGEN***********/////////////////////

    public Bitmap redimensionarImagenMaximo(Bitmap mBitmap, float newWidth, float newHeigth){
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeigth) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);
    }


    //****************ESTO ES PARA DEVOLVERSE*****************

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            Intent i = new Intent(Referrals.this, Agregar_dispositivos.class);
            i.putExtra("id_order", id_order );
            i.putExtra("id_tecnic",id_tecnic );
            startActivity(i);
            //return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    ////////////////////**************ENVIAR ORDEN**********************
    public void send_order(View view) {
        savere();
    }

    public void erase_sign(View view){
        drawView.destroyDrawingCache();
        drawView.startNew();
    }

    public void come_back(View view){
        onBackPressed();
    }



    public void select_status(){
        List<String> StatusList = new ArrayList<>();
        StatusList.add("Pendiente");
        StatusList.add("Centro de servicio");
        StatusList.add("Cuenta por Cobrar");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, StatusList );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        status_spinner.setAdapter(adapter);
        status_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String status = (String) adapterView.getSelectedItem();
                switch(status) {
                    case "Pendiente":
                        status_selected = "506";
                        loadSubStates("506");
                        break;
                    case "Centro de servicio":
                        status_selected = "508";
                        loadSubStates("508");
                        break;
                    default:
                        status_selected  = "507";
                        loadSubStates("507");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    public void loadSubStates(String subStatusId){
        ArrayList substatus;
        List<SubStatus> subList = new ArrayList<>();

        Map<String, SubStatus> map = new HashMap<>();
        int i = 0;
        substatus = statusDb.get_substatus(subStatusId);
        ArrayList<HashMap<String, String>> subStatusList =  substatus;
        for (HashMap<String, String> hashMap : subStatusList) {
            map.put("substatus"+i, new SubStatus(
                    Integer.parseInt(hashMap.get("id_substatus")),
                    hashMap.get("description")));
            subList.add(map.get("substatus"+i));
        }


        ArrayAdapter<SubStatus> adapterSub = new ArrayAdapter<SubStatus>(this, android.R.layout.simple_spinner_item, subList );
        adapterSub.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sub_status_spinner.setAdapter(adapterSub);
        sub_status_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SubStatus subStatus = (SubStatus) adapterView.getSelectedItem();
                sub_status_selected = String.valueOf(subStatus.getId_substatus());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}
