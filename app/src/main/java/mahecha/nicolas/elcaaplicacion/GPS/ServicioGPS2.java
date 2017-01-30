package mahecha.nicolas.elcaaplicacion.GPS;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;

/**
 * Created by nicolas on 23/01/2017.
 */

public class ServicioGPS2 extends Service implements LocationListener {

    LocationManager locationManager;
    String lat, lon, dato;
    HashMap<String, String> queryValues;
    DBController controller = new DBController(this);


    @Override
    public void onCreate() {
        super.onCreate();
     //   Toast.makeText(this, "Servicio creado!", Toast.LENGTH_SHORT).show();



        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER,
                1000*60,
                0,
                this);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        dato= (String) intent.getExtras().get("Tecnico");
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      //  Toast.makeText(this, "Servicio destruído!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public IBinder onBind(Intent intent) {


        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location loc) {
       try {
           lat= ""+loc.getLatitude();
           lon= ""+loc.getLongitude();
         //  System.out.println(loc.getLatitude());
         //  System.out.println(loc.getLongitude());
           queryValues = new HashMap<String, String>();
           queryValues.put("latitud",lat);
           queryValues.put("longitud",lon);
           ArrayList<HashMap<String,String>> listgps = controller.getgps();
           if(listgps.size()==0){
               controller.upgps(queryValues);
           }else{controller.updGPS(queryValues);}


       }catch (Exception e){System.out.println(e);}


        envioGPS();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    public void envioGPS ()
    {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.add("lat", lat);
        params.add("lon", lon);
        params.add("tecnico", dato);

        client.get("http://elca.sytes.net:2122/app_elca/ElcaGPS/getgps.php", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {

                //System.out.println(response);
            }


            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // TODO Auto-generated method stub
                // Hide ProgressBar
                //prgDialog.hide();
                if (statusCode == 404) {
                  //  Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    //Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(getApplicationContext(), "Dispositivo Sin Conexión a Internet",
                      //      Toast.LENGTH_LONG).show();
                   // System.out.println("nada");
                }
            }



        });



    }



}