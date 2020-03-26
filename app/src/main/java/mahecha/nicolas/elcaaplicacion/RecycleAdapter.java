package mahecha.nicolas.elcaaplicacion;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import mahecha.nicolas.elcaaplicacion.Controllers.MenuCamera;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyViewHolder> {
    private ArrayList<MenuCamera> listaMenuGaleries;
    private OnClickRecycler listener;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_adapter,viewGroup, false);
        MyViewHolder viewHolder =  new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        MenuCamera menu = listaMenuGaleries.get(i);
        myViewHolder.bind(menu,listener);

    }

    @Override
    public int getItemCount() {
        return listaMenuGaleries.size();
    }

    public interface OnClickRecycler{
        void OnClickItemRecycler(MenuCamera menu);
    }

    public RecycleAdapter(ArrayList<MenuCamera> listaMenuGaleries, OnClickRecycler listener){
        this.listaMenuGaleries = listaMenuGaleries;
        this.listener = listener;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.imagengaleria);
        }

        public void bind(final MenuCamera menu, final OnClickRecycler listener){
            Glide.with(imageView.getContext()).load(menu.getIdImage()).into(imageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnClickItemRecycler(menu);
                }
            });
        }
    }
}
