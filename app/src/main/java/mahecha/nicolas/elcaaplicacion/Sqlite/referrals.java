package mahecha.nicolas.elcaaplicacion.Sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class referrals extends DBController {
    public referrals(Context contexto) {
        super(contexto);
    }

    ///////////////////////**************AUTO REFERRALS***************///////////////////


    public ArrayList<HashMap<String, String>> get_refferals() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT fk_order_id FROM referrals INNER JOIN orders ON referrals.fk_order_id = orders.id_order WHERE aux_order = 0";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("fk_order_id", cursor.getString(0));

                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }



    public ArrayList<HashMap<String, String>> get_manual_refferals() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT fk_order_id, aux_order FROM referrals INNER JOIN orders ON referrals.fk_order_id = orders.id_order WHERE aux_order >= 1";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("fk_order_id", cursor.getString(0));
                map.put("aux_order", cursor.getString(1));

                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }
}
