package mahecha.nicolas.elcaaplicacion;

//***********************MUESTRA PANATALLA CON LOS DATOS BASICOS DEL PEDIDO*******************/////////


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;

public class Detalles_pedido extends ActionBarActivity implements View.OnClickListener{

    DBController controller = new DBController(this);


    private TextView Emp,prob,cal,num,ciu,prov;

    private Button scanBtn;

    String id_order,id_tecnic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_pedido);
        Emp = (TextView) findViewById(R.id.empresa);
        cal = (TextView) findViewById(R.id.calle);
        num = (TextView) findViewById(R.id.Numero);
        ciu = (TextView) findViewById(R.id.ciudad);
        prov = (TextView) findViewById(R.id.provincia);
        prob = (TextView) findViewById(R.id.problema);
        scanBtn = (Button)findViewById(R.id.button2);
        scanBtn.setOnClickListener(this);
        id_order = getIntent().getStringExtra("id_order");
        id_tecnic = getIntent().getStringExtra("id_tecnic");
//        System.out.println(id_order+" "+id_tecnic);
        detalle(id_order);
        }

    private void detalle(String idpedido) {

        ArrayList<HashMap<String, String>> listdetalle = controller.listdetalle(idpedido);
        for (HashMap<String, String> hashMap : listdetalle) {
            Emp.setText(hashMap.get("cliente"));
            cal.setText(hashMap.get("calle"));
            num.setText(hashMap.get("numero"));
            ciu.setText(hashMap.get("ciudad"));
            prov.setText(hashMap.get("provincia"));
            prob.setText(hashMap.get("descripcion"));
        }
    }

    @Override
    public void onClick(View view) {

        //Toast.makeText(this,"hola",Toast.LENGTH_LONG).show();
        //**************FUERZA LA INICIALIZACION DEL LOCALIZADOR GPS**********/////////
        try {
            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                buildAlertMessageNoGps();
            }else
            {
                Intent i = new Intent(Detalles_pedido.this, Agregar_dispositivos.class);
                i.putExtra("id_order", id_order );
                i.putExtra("id_tecnic", id_tecnic );
                startActivity(i);
            }

        }catch(Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();}




    }


    //****************ESTO ES PARA DEVOLVERSE*****************

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            Intent i = new Intent(Detalles_pedido.this, Pedidos.class);
            i.putExtra("id_order", id_order );
            i.putExtra("id_tecnic", id_tecnic );
            startActivity(i);
            //return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /////////////********GPS*********//////////////
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El GPS esta desactivado, desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
