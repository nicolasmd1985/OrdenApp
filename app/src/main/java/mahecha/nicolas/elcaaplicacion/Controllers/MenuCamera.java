package mahecha.nicolas.elcaaplicacion.Controllers;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import mahecha.nicolas.elcaaplicacion.R;


public class MenuCamera {
    private Context context;
    private String idImage;
    private String titulo;
    private String id_order;

    public MenuCamera(){
        idImage="";
        titulo="";
        id_order= "129";
        this.context = context;
    }

    public MenuCamera(String idImage, String titulo){
        this.idImage=idImage;
        this.titulo=titulo;
    }

    public String getIdImage() {
        return idImage;
    }

    public String getTitulo() {
        return titulo;
    }

    public ArrayList<MenuCamera>listaMenu(){
        MenuCamera menu;
        ArrayList<MenuCamera> lista = new ArrayList<MenuCamera>();



        String path = "/data/user/0/mahecha.nicolas.ordenapp/files/129";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null){
            String[] Idmagenes = new String[files.length];
            Log.d("Files", "Size: "+ files.length);
            for (int i = 0; i < files.length; i++)
            {
//                Log.d("Files", "FileName:" + files[i].getName());
                Idmagenes[i] = files[i].getAbsolutePath();
                System.out.println(Idmagenes[0]);
                String[]titulos= new String[]{"logo", "send", "font", "font"};

                for (int j = 0; j<Idmagenes.length; j++){
                    menu = new MenuCamera(Idmagenes[j], titulos[j]);
                    lista.add(menu);
                }
            }
        }



        return lista;
    }

}
