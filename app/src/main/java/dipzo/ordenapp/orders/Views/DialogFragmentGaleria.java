package dipzo.ordenapp.orders.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import dipzo.ordenapp.orders.Controllers.MenuCamera;
import dipzo.ordenapp.orders.R;


public class DialogFragmentGaleria extends Fragment {
    View view;
    TextView titulo;
    RecyclerView recyclerView;
    ImageView imageView;
    ArrayList<MenuCamera> listaMenuGaleries;
    RecycleAdapter adapter;
    String global_storage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dialog_galeria, container, false);
        titulo= (TextView)view.findViewById(R.id.titulo);
        imageView= (ImageView)view.findViewById(R.id.imageView2);
        recyclerView = (RecyclerView)view.findViewById(R.id.galery);

        global_storage = getArguments().getString("global_storage");
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        String strtext = global_storage;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        listaMenuGaleries = new MenuCamera().listaMenu(strtext);
        adapter =  new RecycleAdapter(listaMenuGaleries, new RecycleAdapter.OnClickRecycler() {
            @Override
            public void OnClickItemRecycler(MenuCamera menu) {
                Glide.with(imageView.getContext()).load(menu.getIdImage()).into(imageView);
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }
}
