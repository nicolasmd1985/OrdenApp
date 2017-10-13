package mahecha.nicolas.elcaaplicacion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;

public class Remito extends AppCompatActivity {

    String idped,idusuar;
    private DrawingView drawView;
    EditText observaciones,aclaracion,email;
    DBController controller = new DBController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remito);
        idped = getIntent().getStringExtra("idpedido");
        idusuar = getIntent().getStringExtra("idusuario");

       // cargadisp(idped);

        drawView = (DrawingView)findViewById(R.id.drawing);
        observaciones = (EditText)findViewById(R.id.observaciones);
        aclaracion = (EditText)findViewById(R.id.aclaracion);
        email=(EditText)findViewById(R.id.email);
    }

//    ////////////////*****************CARGA BASE DE DATOS SQLITE************************
//    public void cargadisp(String idped)
//    {
//        ArrayList<HashMap<String, String>> dipslist =  controller.getdisp(idped);
//        if(dipslist.size()!=0){
//            ListAdapter adapter = new SimpleAdapter( Remito.this,dipslist, R.layout.view_remito, new String[] { "nombre", "descripcion"}, new int[] {R.id.nomdipo, R.id.descdisp});
//            ListView myList=(ListView)findViewById(android.R.id.list);
//            myList.setAdapter(adapter);
//        }else{
//            Toast.makeText(getApplicationContext(), "ATENCION!!! No tiene ningun Item", Toast.LENGTH_LONG).show();}
//    }
//

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
                queryValues.put("idpedido", idped);
                queryValues.put("observaciones",observaciones.getText().toString());
                queryValues.put("aclaracion",aclaracion.getText().toString());
                queryValues.put("firma",encodedString);
                queryValues.put("horafinal", tiempo());
                queryValues.put("email", email.getText().toString());
                controller.upfoto(queryValues);
                controller.upload_aux(idped);
                drawView.destroyDrawingCache();
                Intent i = new Intent(Remito.this, Pedidos.class);
                i.putExtra("idpedido", idped );
                i.putExtra("idusuario",idusuar );
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
            i.putExtra("idpedido", idped );
            i.putExtra("idusuario",idusuar );
            startActivity(i);
            //return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    ////////////////////**************MENU ENVIAR**********************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.enviar, menu);
        return true;
    }


    ////////////////////////////////*************BOTON DE SINCRONIZACION DE BD*******************////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.enviar) {
            //Toast.makeText(getApplicationContext(), "ATENCION!!! No tiene ningun Item", Toast.LENGTH_LONG).show();
            savere();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
