package dipzo.ordenapp.tecnic;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dipzo.ordenapp.tecnic.Controllers.MenuCamera;

import static dipzo.ordenapp.tecnic.thing_detail.REQUEST_IMAGE_CAPTURE;

public class camera_evidence extends AppCompatActivity {
    String id_order, id_tecnic, code_scan, global_storage;
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

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/" + id_order + "/" + code_scan);
        global_storage = String.valueOf(storageDir);
        bundle = new Bundle();
        bundle.putString("id_order", id_order);
        bundle.putString("code_scan", code_scan);
        bundle.putString("global_storage", global_storage);

        create_fragment();

    }

    /////////////************************OBTIENE INFO DEL SCANER*****************////////////////
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            try {
                create_fragment();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = takePictureIntent.getExtras();
            Bitmap bitmap;
            if (extras.containsKey("data")) {
                bitmap = (Bitmap) extras.get("data");
            } else {
                bitmap = getBitmapFromUri(Uri.fromFile(photoFile));
            }
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(currentPhotoPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
        }

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public Bitmap getBitmapFromUri(Uri imageUri) {
        getContentResolver().notifyChange(imageUri, null);
        ContentResolver cr = getContentResolver();
        Bitmap bitmap;
        try {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imageUri);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static final int REQUEST_TAKE_PHOTO = 1;
    private Intent takePictureIntent;
    File photoFile;

    private void dispatchTakePictureIntent() {
        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
             photoFile = null;
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

                Uri imageUri = takePictureIntent.getData();
                String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
                Cursor cur = managedQuery(imageUri, orientationColumn, null, null, null);
                int orientation = -1;
                if (cur != null && cur.moveToFirst()) {
                    orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);
            }
        }
    }


    ////////////////*********************CLICK EN EL BOTON*************////////////
    public void camera_click(View view) {
        dispatchTakePictureIntent();
    }

    public void create_fragment() {

        newFragment = new DialogFragmentGaleria();
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        newFragment.setArguments(bundle);
        transaction.replace(R.id.container_frag, newFragment, id_order);
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
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/" + id_order + "/" + code_scan);
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
