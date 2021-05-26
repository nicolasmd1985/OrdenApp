package dipzo.ordenapp.orders.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import dipzo.ordenapp.orders.R;


public class DataHistoryAdapter extends RecyclerView.Adapter<DataHistoryAdapter.ViewHolder> {
    private ArrayList<HistoryArray> history_array;
    private Context context;

    public DataHistoryAdapter(Context context,ArrayList<HistoryArray> android_versions) {
        this.context = context;
        this.history_array = android_versions;

    }

    @Override
    public DataHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_image, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Glide.with(context).load(history_array.get(i).getPhoto_image_url())
                .override(200, 400)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.iv_photo);
    }

    @Override
    public int getItemCount() {
        return history_array.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_photo;
        public ViewHolder(View view) {
            super(view);
            iv_photo = (ImageView)view.findViewById(R.id.iv_photo);
        }
    }}
