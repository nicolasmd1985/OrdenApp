package mahecha.nicolas.elcaaplicacion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mahecha.nicolas.elcaaplicacion.Controllers.MenuCamera;


public class DialogFragmentGaleria extends Fragment {
    View view;
    TextView titulo;
    RecyclerView recyclerView;
    ImageView imageView;
    ArrayList<MenuCamera> listaMenuGaleries;
    RecycleAdapter adapter;
    String id_order, code_scan;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dialog_galeria, container, false);
        titulo= (TextView)view.findViewById(R.id.titulo);
        imageView= (ImageView)view.findViewById(R.id.imageView2);
        recyclerView = (RecyclerView)view.findViewById(R.id.galery);

        id_order = getArguments().getString("id_order");
        code_scan = getArguments().getString("code_scan");
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        String strtext = id_order + "/" + code_scan;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        listaMenuGaleries = new MenuCamera().listaMenu(strtext);
        adapter =  new RecycleAdapter(listaMenuGaleries, new RecycleAdapter.OnClickRecycler() {
            @Override
            public void OnClickItemRecycler(MenuCamera menu) {
//                Glide.with(getContext()).load((menu.getIdImage())).into(imageView);

                Glide.with(imageView.getContext()).load(menu.getIdImage()).into(imageView);
                titulo.setText(menu.getTitulo());
            }
        });

        recyclerView.setAdapter(adapter);

        return view;
    }
}
