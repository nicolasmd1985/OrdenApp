package mahecha.nicolas.elcaaplicacion.Controllers;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;



public class MenuCamera {
    private Context context;
    private String idImage;


    public MenuCamera(){
        idImage="";
        this.context = context;
    }

    public MenuCamera(String idImage){
        this.idImage=idImage;
    }

    public String getIdImage() {
        return idImage;
    }


    public ArrayList<MenuCamera>listaMenu(String strtext){
        MenuCamera menu;
        ArrayList<MenuCamera> lista = new ArrayList<MenuCamera>();



        String path = strtext;
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null){
            String[] Idmagenes = new String[files.length];
            for (int i = 0; i < files.length; i++)
            {
                Idmagenes[i] = files[i].getAbsolutePath();
            }
            Arrays.sort(Idmagenes, Collections.reverseOrder());
            for (int j = 0; j<Idmagenes.length; j++){
                menu = new MenuCamera(Idmagenes[j]);
                lista.add(menu);
            }
        }

        return lista;
    }

}
