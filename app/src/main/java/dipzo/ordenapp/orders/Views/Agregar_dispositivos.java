package dipzo.ordenapp.orders.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

import dipzo.ordenapp.orders.R;
import dipzo.ordenapp.orders.Sqlite.DBController;

public class Agregar_dispositivos extends AppCompatActivity {

    DBController controller = new DBController(this);
    String id_order,id_tecnic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_dispositivos);
        id_order = getIntent().getStringExtra("id_order");
        id_tecnic = getIntent().getStringExtra("id_tecnic");
        cargadisp(id_order);
        final ListView lista = (ListView) findViewById(android.R.id.list);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView parent, View view, int i, long l) {
                ArrayList<HashMap<String, String>> dipslist =  controller.getdisp(id_order);
                int cont = 0;
                for (HashMap<String, String> hashMap : dipslist) {
                    if(i==cont)
                    {
                        String code = hashMap.get("code_scan");
                        Intent objIntent = new Intent(getApplicationContext(), Mod_dispositivo.class);
                        objIntent.putExtra("id_order", id_order );
                        objIntent.putExtra("code_scan", code);
                        objIntent.putExtra("id_tecnic",id_tecnic );
                        startActivity(objIntent);

                    }
                    cont=cont+1;
                }
            }
        });

        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView adapterView, View view, int i, long l) {
                ArrayList<HashMap<String, String>> dipslist =  controller.getdisp(id_order);
                int cont = 0;
                for (HashMap<String, String> hashMap : dipslist) {
                    if(i==cont)
                    {
                        String code = hashMap.get("code_scan");
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
            ListAdapter adapter = new SimpleAdapter( Agregar_dispositivos.this,dipslist, R.layout.view_disp, new String[] { "code_scan","name","description"}, new int[] {R.id.cod_disp, R.id.nomdisp, R.id.descdisp});
            ListView myList=(ListView)findViewById(android.R.id.list);
            myList.setAdapter(adapter);
        }else{
            Toast.makeText(getApplicationContext(), "No tiene dispositivos instalados", Toast.LENGTH_LONG).show();}
    }



    ////////////////////******************AGREGA PEDIDO******************//////////////////
    public void adddisp(View view) {
        Intent objIntent = new Intent(getApplicationContext(), thing_detail.class);
        objIntent.putExtra("id_order", id_order );
        objIntent.putExtra("id_tecnic",id_tecnic );
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
                        controller.dipsup(code);
                        reloadactivity();
                    }
                });
        helpBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }

////////////////////////********************RECARGA ACTIVIDAD********************//////////////////
    public void reloadactivity() {
        Intent objIntent = new Intent(getApplicationContext(),
                Agregar_dispositivos.class);
        objIntent.putExtra("id_order", id_order );
        objIntent.putExtra("id_tecnic",id_tecnic );
        startActivity(objIntent);
    }

////////////////////////////*********************TERMINAR****************///////////////
    public void terminar(View view) {

        Intent objIntent = new Intent(getApplicationContext(),
                Referrals.class);
        objIntent.putExtra("id_order", id_order );
        objIntent.putExtra("id_tecnic",id_tecnic );
        startActivity(objIntent);
    }


    //****************ESTO ES PARA DEVOLVERSE*****************

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            Intent i = new Intent(Agregar_dispositivos.this, OrderDetails.class);
            i.putExtra("id_order", id_order );
            i.putExtra("id_tecnic",id_tecnic );
            startActivity(i);
        }
        return super.onKeyDown(keyCode, event);
    }


}
