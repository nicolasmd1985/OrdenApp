package mahecha.nicolas.elcaaplicacion.Controllers;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class send_photos {
    private Context context;

    public send_photos(Context context) {
        this.context = context;
    }

    public void photos(String dir,String id_order, String codigo){
        uploader upimage = new uploader(context);
        resize_image resizeImage = new resize_image();
        ArrayList<String> list = new ArrayList<String>();
        String storageDir = Environment.DIRECTORY_PICTURES + "/" + id_order + "/";
        String path = String.valueOf(storageDir);
//        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null){
            String[] Idmagenes = new String[files.length];
//            Log.d("Files", "Size: "+ files.length);
            for (int i = 0; i < files.length; i++)
            {
                Idmagenes[i] = files[i].getName();
            }
            Arrays.sort(Idmagenes, Collections.reverseOrder());
            for (int j = 0; j<Idmagenes.length; j++){
                list.add(Idmagenes[j]);
            }
        }

    }
}
