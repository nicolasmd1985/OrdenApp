package dipzo.ordenapp.tecnic;


import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dipzo.ordenapp.tecnic.Controllers.MenuCamera;

import static dipzo.ordenapp.tecnic.thing_detail.REQUEST_IMAGE_CAPTURE;

public class camera_evidence extends AppCompatActivity {
    String id_order,id_tecnic, code_scan, global_storage;
    Fragment newFragment;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_evidence);

        MenuCamera menuCamera = new MenuCamera();

        id_tecnic = getIntent().getStringExtra("id_tecnic");
        id_order = getIntent().getStringExtra("id_order");
        code_scan = getIntent().getStringExtra("codigo");

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/" + id_order + "/" + code_scan  );
        global_storage = String.valueOf(storageDir);
        bundle = new Bundle();
        bundle.putString("id_order", id_order);
        bundle.putString("code_scan", code_scan);
        bundle.putString("global_storage", global_storage);

        create_fragment();

    }

    /////////////************************OBTIENE INFO DEL SCANER*****************////////////////
    public void onActivityResult(int requestCode, int resultCode, Intent data) {



        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                create_fragment();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        }


    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "dipzo.ordenapp.tecnic",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }



    ////////////////*********************CLICK EN EL BOTON*************////////////
    public void camera_click(View view) {
        dispatchTakePictureIntent();
    }

    public void create_fragment(){

        newFragment = new DialogFragmentGaleria();
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        newFragment.setArguments(bundle);
        transaction.replace(R.id.container_frag,newFragment,id_order);
        transaction.commitAllowingStateLoss();

    }


    public void back_button(View view) {
        onBackPressed();    //Call the back button's method
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/" + id_order + "/" + code_scan  );
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir     /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
