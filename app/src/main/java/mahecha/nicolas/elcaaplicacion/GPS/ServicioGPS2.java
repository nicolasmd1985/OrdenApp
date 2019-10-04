package mahecha.nicolas.elcaaplicacion.GPS;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;

import mahecha.nicolas.elcaaplicacion.Sqlite.DBController;

/**
 * Created by nicolas on 23/01/2017.
 * ENVIO DE PARAMENTROS DE UBICACION DE LOS TECNICOS AL SERVIDOR
 */

public class ServicioGPS2 extends Service implements LocationListener {

    LocationManager locationManager;
    String lat, lon;
    HashMap<String, String> queryValues;
    DBController controller = new DBController(this);


    @Override
    public void onCreate() {
        super.onCreate();
        //Toast.makeText(this, "Servicio creado!", Toast.LENGTH_SHORT).show();
       locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          return;
        }
        //ENVIO CADA 10MIN
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1000*60*5,
                0,
                this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        ////////////PROBLEMA!!!!////
       // dato= (String) intent.getExtras().get("Tecnico");
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
           queryValues = new HashMap<String, String>();
           queryValues.put("latitud",lat);
           queryValues.put("longitud",lon);
           ArrayList<HashMap<String,String>> listgps = controller.getgps();

           if(listgps.size()==0){
               controller.upgps(queryValues);
              // Toast.makeText(this, "nuevo", Toast.LENGTH_SHORT).show();
           }else{
               controller.updGPS(queryValues);
             // Toast.makeText(this, "viejo", Toast.LENGTH_SHORT).show();

           }
           envioGPS();
           //Toast.makeText(this, lat+""+lon, Toast.LENGTH_SHORT).show();

       }catch (Exception e){
           //Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
       }

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
        ArrayList token = controller.tokenExp();

        if (token != null){
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Content-type", "application/json;charset=utf-8");
            client.addHeader("Authorization", token.get(0).toString());
            RequestParams params = new RequestParams();
            params.add("latitude", lat);
            params.add("longitude", lon);
            try{
                client.post("http://186.155.202.23:3000/api/v1/send_gps", params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(String response) {
                        System.out.println(response);
                    }
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                                          String content) {
                        System.out.println(statusCode);
                    }
                });}catch (Exception e){}
        }

   }





}