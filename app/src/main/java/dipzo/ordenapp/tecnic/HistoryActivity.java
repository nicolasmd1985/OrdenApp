package dipzo.ordenapp.tecnic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HistoryActivity extends AppCompatActivity {
    String id_order;
    String id_tecnic;
    String code_scan;
    String histories;
    JSONObject json_data;
    HashMap<String, String> queryValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        id_tecnic = getIntent().getStringExtra("id_tecnic");
        id_order = getIntent().getStringExtra("id_order");
        code_scan = getIntent().getStringExtra("codigo");
        histories = getIntent().getStringExtra("histories");


        try {
            JSONArray arr = new JSONArray(histories);
            ArrayList<HashMap<String, String>> wordList;
            wordList = new ArrayList<HashMap<String, String>>();
            for(int i=0; i < arr.length() ; i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                json_data = arr.getJSONObject(i);
                String id = json_data.get("id").toString();
                String tecnic = json_data.get("tecnic").toString();
                String created_at = json_data.get("created_at").toString();
                String comment = json_data.getString("description");
                String photos = json_data.getString("photos");
                map.put("id", id);
                map.put("tecnic", tecnic);
                map.put("created_at", created_at);
                map.put("comment", comment);
                map.put("photos", photos);
                wordList.add(map);
            }
            if(wordList.size()!=0) {
                ListAdapter adapter = new SimpleAdapter(
                        HistoryActivity.this,
                        wordList,
                        R.layout.view_histories,
                        new String[]{"created_at", "comment", "tecnic"},
                        new int[]{R.id.date, R.id.comment, R.id.tecnic});

                final ListView myList = (ListView) findViewById(android.R.id.list);
                myList.setAdapter(adapter);
                myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View view, int i, long l) {
                        Map<String, Object> map = (Map<String, Object>) myList.getItemAtPosition(i);
                        String created_at = (String) map.get("created_at");
                        String comment = (String) map.get("comment");
                        String tecnic = (String) map.get("tecnic");
                        String photos = (String) map.get("photos");

                        Intent x = new Intent(HistoryActivity.this, HistoryDetail.class);
                        x.putExtra("created_at", created_at);
                        x.putExtra("comment", comment);
                        x.putExtra("tecnic", tecnic);
                        x.putExtra("photos", photos);
                        startActivity(x);
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }
}