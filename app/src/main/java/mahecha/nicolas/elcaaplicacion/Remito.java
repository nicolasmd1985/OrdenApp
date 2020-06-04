package mahecha.nicolas.elcaaplicacion;

import android.app.AlertDialog;
import android.app.VoiceInteractor;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;

import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;

public class Remito extends AppCompatActivity {

    String id_order,id_tecnic;
    private DrawingView drawView;
    EditText observaciones,aclaracion,email;
    DBController controller = new DBController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remito);
        id_order = getIntent().getStringExtra("id_order");
        id_tecnic = getIntent().getStringExtra("id_tecnic");

        drawView = (DrawingView)findViewById(R.id.drawing);
        observaciones = (EditText)findViewById(R.id.observaciones);
        aclaracion = (EditText)findViewById(R.id.aclaracion);
        email=(EditText)findViewById(R.id.email);
    }


    public void savere()
    {
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Guardar Remito");
        saveDialog.setMessage("Desea guardar el remito?");
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
            controller.insert_referral(queryValues);
            controller.finish_order(Integer.valueOf(id_order));
            drawView.destroyDrawingCache();
            Intent i = new Intent(Remito.this, Pedidos.class);
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
        CharSequence s  = DateFormat.format("d/M/yyyy H:m", date.getTime());
       // System.out.println (s);
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
            Intent i = new Intent(Remito.this, Agregar_dispositivos.class);
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




}
