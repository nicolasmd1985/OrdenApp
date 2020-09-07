package dipzo.ordenapp.tecnic.GPS;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import dipzo.ordenapp.tecnic.Sqlite.DBController;
import dipzo.ordenapp.tecnic.Constans;
import dipzo.ordenapp.tecnic.Sqlite.users;

/**
 * Created by nicolas on 23/01/2017.
 * ENVIO DE PARAMENTROS DE UBICACION DE LOS TECNICOS AL SERVIDOR
 */

public class ServicioGPS2 extends Service implements LocationListener {

    LocationManager locationManager;
    String lat, lon;
    HashMap<String, String> queryValues;
    DBController controller = new DBController(this);
    users users = new users(this);


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
           queryValues.put("latitude",lat);
           queryValues.put("longitude",lon);
           controller.upgps(queryValues);
           envioGPS();
       }catch (Exception e){
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
        ArrayList token = users.tokenExp();

        if (token != null){
            AsyncHttpClient client = new AsyncHttpClient();
            client.setBearerAuth(token.get(3).toString());
            RequestParams params = new RequestParams();
            params.add("latitude", lat);
            params.add("longitude", lon);
            try{
                client.post(Constans.API_END + Constans.SEND_GPS , params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }

                });}catch (Exception e){}
        }

   }





}