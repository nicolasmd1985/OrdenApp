package mahecha.nicolas.elcaaplicacion.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class orders extends DBController {
    public orders(Context contexto) {
        super(contexto);
    }

    public void update_aux_order(String aux_order, String id_order){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("aux_order", aux_order );
        database.update("orders", values ,"id_order='"+id_order+"'", null);
        database.close();
    }


    //////////////////*****************MANUAL ORDERS**********/////////


    public ArrayList<HashMap<String, String>> manual_order()
    {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT id_order, description, address, customer_id, city_id, created_at, install_date, aux_order  FROM orders where aux_order = 1";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {

            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id_order", cursor.getString(0));
                map.put("description", cursor.getString(1));
                map.put("address", cursor.getString(2));
                map.put("customer_id", cursor.getString(3));
                map.put("city_id", cursor.getString(4));
                map.put("created_at", cursor.getString(5));
                map.put("install_date", cursor.getString(6));

                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }
}
