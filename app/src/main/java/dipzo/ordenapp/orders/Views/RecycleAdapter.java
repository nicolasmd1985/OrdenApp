package dipzo.ordenapp.orders.Views;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import dipzo.ordenapp.orders.Controllers.MenuCamera;
import dipzo.ordenapp.orders.R;

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
