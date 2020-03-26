package mahecha.nicolas.elcaaplicacion.Controllers;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import mahecha.nicolas.elcaaplicacion.R;

public class MenuCamera {
    private int idImage;
    private String titulo;

    public MenuCamera(){
        idImage=0;
        titulo="";
    }

    public MenuCamera(int idImage, String titulo){
        this.idImage=idImage;
        this.titulo=titulo;
    }

    public int getIdImage() {
        return idImage;
    }

    public String getTitulo() {
        return titulo;
    }

    public ArrayList<MenuCamera>listaMenu(){
        MenuCamera menu;
        ArrayList<MenuCamera> lista = new ArrayList<MenuCamera>();
        String path = "/data/user/0/mahecha.nicolas.ordenapp/files";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }

        Integer[]Idmagenes=new Integer[]{1, R.drawable.send, R.drawable.ic_action_refresh};
        System.out.println(R.drawable.logodip);
        String[]titulos= new String[]{"logo", "send", "font"};

        for (int i = 0; i<Idmagenes.length; i++){
            menu = new MenuCamera(Idmagenes[i], titulos[i]);
            lista.add(menu);
        }
        return lista;
    }
}
