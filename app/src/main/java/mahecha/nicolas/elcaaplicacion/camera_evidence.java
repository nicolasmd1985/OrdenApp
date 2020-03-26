package mahecha.nicolas.elcaaplicacion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import mahecha.nicolas.elcaaplicacion.Controllers.uploader;
import mahecha.nicolas.elcaaplicacion.android.IntentIntegrator;
import mahecha.nicolas.elcaaplicacion.android.IntentResult;

import static mahecha.nicolas.elcaaplicacion.thing_detail.REQUEST_IMAGE_CAPTURE;

public class camera_evidence extends AppCompatActivity {
    ImageView Imagetake;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_evidence);
        Imagetake = (ImageView) findViewById(R.id.imageView3);
    }

    /////////////************************OBTIENE INFO DEL SCANER*****************////////////////
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        uploader uploads3 = new uploader(this);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = intent.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Imagetake.setImageBitmap(imageBitmap);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File filesDir = this.getFilesDir();
            File imageFile = new File(filesDir, imageFileName +".jpg");

            OutputStream os;
            try {
                os = new FileOutputStream(imageFile);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
                uploads3.uploadtos3(this, imageFile);

            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
            }

        }
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }
    }

    ////////////////*********************CLICK EN EL BOTON*************////////////
    public void camera_click(View view) {
        dispatchTakePictureIntent();
    }
}
