package mahecha.nicolas.elcaaplicacion;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;

public class Detalles_pedido extends ActionBarActivity implements View.OnClickListener{

    DBController controller = new DBController(this);


    private TextView Emp,prob,cal,num,ciu,prov;

    private Button scanBtn;

    String idpedido,idusuar;

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
        idpedido = getIntent().getStringExtra("idpedido");
        idusuar = getIntent().getStringExtra("idusuario");
        System.out.println("idusu"+idusuar);
        detalle(idpedido);
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
        Intent i = new Intent(Detalles_pedido.this, Agregar_dispositivos.class);
        i.putExtra("idpedido", idpedido );
        i.putExtra("idusuario",idusuar );
        startActivity(i);
    }
}
