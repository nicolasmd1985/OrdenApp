package mahecha.nicolas.elcaaplicacion;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;

public class Agregar_dispositivos extends AppCompatActivity {

    DBController controller = new DBController(this);
    String idped,idusuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_dispositivos);
        idped = getIntent().getStringExtra("idpedido");
        idusuar = getIntent().getStringExtra("idusuario");
        cargadisp(idped);
        final ListView lista = (ListView) findViewById(android.R.id.list);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView parent, View view, int i, long l) {
               // Toast.makeText(getApplicationContext(), "presiono " + i, Toast.LENGTH_SHORT).show();


                ArrayList<HashMap<String, String>> dipslist =  controller.getdisp(idped);
                int cont = 0;
                for (HashMap<String, String> hashMap : dipslist) {
                  //  System.out.println(cont);
                    if(i==cont)
                    {
                        String code = hashMap.get("codigoscan");
                        Intent objIntent = new Intent(getApplicationContext(), Mod_dispositivo.class);
                        objIntent.putExtra("idpedido", idped );
                        objIntent.putExtra("codigoscan", code);
                        objIntent.putExtra("idusuario",idusuar );
                        startActivity(objIntent);

                    }
                    cont=cont+1;
                }
            }
        });

        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView adapterView, View view, int i, long l) {
               // Toast.makeText(getApplicationContext(), "presiono hola" + i, Toast.LENGTH_SHORT).show();
                ArrayList<HashMap<String, String>> dipslist =  controller.getdisp(idped);
                int cont = 0;
                for (HashMap<String, String> hashMap : dipslist) {
                    if(i==cont)
                    {
                        String code = hashMap.get("codigoscan");
                        showSimplePopUp(code);
                    }
                    cont=cont+1;
                }
                return true;
            }
        });
    }


    ////////////////*****************CARGA BASE DE DATOS SQLITE************************
    public void cargadisp(String idped)
    {
        ArrayList<HashMap<String, String>> dipslist =  controller.getdisp(idped);
        if(dipslist.size()!=0){
            ListAdapter adapter = new SimpleAdapter( Agregar_dispositivos.this,dipslist, R.layout.view_disp, new String[] { "codigoscan","nombre","descripcion"}, new int[] {R.id.cod_disp, R.id.nomdisp, R.id.descdisp});
            ListView myList=(ListView)findViewById(android.R.id.list);
            myList.setAdapter(adapter);
        }else{
            Toast.makeText(getApplicationContext(), "No tiene dispositivos instaldos", Toast.LENGTH_LONG).show();}
    }



    ////////////////////******************AGREGA PEDIDO******************//////////////////
    public void adddisp(View view) {
        Intent objIntent = new Intent(getApplicationContext(), Scaner_dispositivo.class);
        objIntent.putExtra("idpedido", idped );
        objIntent.putExtra("idusuario",idusuar );
        startActivity(objIntent);
    }


    ///////////******************POP UP**************//////////////
    private void showSimplePopUp(final String code) {

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Eliminar");
        helpBuilder.setMessage("Realmente desea elimiar el dispositivo");
        helpBuilder.setPositiveButton("Si",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("si");
                        controller.dipsup(code);
                        reloadactivity();
                    }
                });
        helpBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("no");
            }
        });

        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }

////////////////////////********************RECARGA ACTIVIDAD********************//////////////////
    public void reloadactivity() {
        Intent objIntent = new Intent(getApplicationContext(),
                Agregar_dispositivos.class);
        objIntent.putExtra("idpedido", idped );
        objIntent.putExtra("idusuario",idusuar );
        startActivity(objIntent);
    }

////////////////////////////*********************TERMINAR****************///////////////
    public void terminar(View view) {

        Intent objIntent = new Intent(getApplicationContext(),
                Remito.class);
        objIntent.putExtra("idpedido", idped );
        objIntent.putExtra("idusuario",idusuar );
        startActivity(objIntent);
    }


    //****************ESTO ES PARA DEVOLVERSE*****************

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            Intent i = new Intent(Agregar_dispositivos.this, Detalles_pedido.class);
            i.putExtra("idpedido", idped );
            i.putExtra("idusuario",idusuar );
            startActivity(i);
            //return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
