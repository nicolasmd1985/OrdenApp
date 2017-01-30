package mahecha.nicolas.elcaaplicacion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;
import mahecha.nicolas.elcaaplicacion.android.IntentIntegrator;
import mahecha.nicolas.elcaaplicacion.android.IntentResult;

public class Mod_dispositivo extends AppCompatActivity implements View.OnClickListener {

    DBController controller = new DBController(this);
    EditText nombre, descripcion;
    TextView codigo;
    String idped, code, idusuar;
    private Button scanBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_dispositivo);
        codigo = (TextView) findViewById(R.id.codigo);
        nombre = (EditText) findViewById(R.id.nomdisp);
        descripcion = (EditText) findViewById(R.id.descripcion);
        scanBtn = (Button) findViewById(R.id.scan_button);
        idped = getIntent().getStringExtra("idpedido");
        idusuar = getIntent().getStringExtra("idusuario");
        code = getIntent().getStringExtra("codigoscan");
        scanBtn.setOnClickListener(this);
        carga_datos(code);
    }


    //////////////**********************CARGAR DATOS************/////////////////


    private void carga_datos(String code) {
        ArrayList<HashMap<String, String>> dipslist = controller.getdispcod(code);
        for (HashMap<String, String> hashMap : dipslist) {
            codigo.setText(hashMap.get("codigoscan"));
            nombre.setText(hashMap.get("nombre"));
            descripcion.setText(hashMap.get("descripcion"));
        }
    }
    /////////////************************OBTIENE INFO DEL SCANER*****************////////////////

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            descripcion.setText(scanContent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    //////////////////////**********************HOME******************/////////////////
    public void callHomeActivity(View view) {
        Intent objIntent = new Intent(getApplicationContext(),
                Agregar_dispositivos.class);
        objIntent.putExtra("idpedido", idped);
        objIntent.putExtra("idusuario", idusuar);

        startActivity(objIntent);
    }

//////////////////**********************CANCELAR**************//////////////////////////
    public void canceldisp(View view) {
        this.callHomeActivity(view);
    }


    ////////////////*********************CLICK EN EL BOTON*************////////////

    public void moddisp(View view) {
        HashMap<String, String> queryValues = new HashMap<String, String>();

        queryValues.put("codigo", codigo.getText().toString());
        queryValues.put("nombre", nombre.getText().toString());
        queryValues.put("descripcion", descripcion.getText().toString());
        controller.updips(queryValues);
        this.callHomeActivity(view);
    }




    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.scan_button){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }

    }
}