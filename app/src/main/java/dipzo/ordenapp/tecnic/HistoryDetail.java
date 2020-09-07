package dipzo.ordenapp.tecnic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import dipzo.ordenapp.tecnic.DataHistoryAdapter;
import dipzo.ordenapp.tecnic.HistoryArray;


public class HistoryDetail extends AppCompatActivity {
    String comment;
    String tecnic;
    String created_at;
    String photos;

    private String[] android_image_urls;


    TextView description_int, tecnic_charge, date_history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        comment = getIntent().getStringExtra("comment");
        tecnic = getIntent().getStringExtra("tecnic");
        created_at = getIntent().getStringExtra("created_at");
        photos = getIntent().getStringExtra("photos");

        description_int = (TextView) findViewById(R.id.comment_inter);
        tecnic_charge = (TextView) findViewById(R.id.tecnic_in_charge);
        date_history = (TextView) findViewById(R.id.date_time);

        description_int.setText(comment);
        tecnic_charge.setText(tecnic);
        date_history.setText(created_at);



        Gson gson = new Gson();
        android_image_urls = gson.fromJson(photos, String[].class);
        initViews();

    }

    private void initViews(){
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.rv_images);
        recyclerView.setHasFixedSize(true);
        int numberOfColumns = 2;

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        ArrayList androidVersions = prepareData();
        DataHistoryAdapter adapter = new DataHistoryAdapter(getApplicationContext(),androidVersions);
        recyclerView.setAdapter(adapter);

    }

    private ArrayList prepareData(){

        ArrayList historiesPhotos = new ArrayList<>();
        for(int i=0;i<android_image_urls.length;i++){
            HistoryArray histories = new HistoryArray();
            histories.setPhoto_image_url("https://ordenappbucket.s3.amazonaws.com/"+android_image_urls[i]);
            historiesPhotos.add(histories);
        }
        return historiesPhotos;
    }



}