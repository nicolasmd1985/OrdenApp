package dipzo.ordenapp.orders.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class substatuses_db extends DBController{

    public substatuses_db(Context contexto) {
        super(contexto);
    }


    ///////////////////*****************INSERT NEW CUSTOMERS***************/////////////ok

    /**
     * Inserts User into SQLite DB   * @param queryValues
     */
    public boolean insertSubstatus(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String id = queryValues.get("id_substatus");
        String query = "SELECT id_substatus FROM substatus where id_substatus ="+id;
        Cursor cursor = database.rawQuery(query, null);
        values.put("id_substatus", queryValues.get("id_substatus"));
        values.put("substatus_type", queryValues.get("substatus_type"));
        values.put("status_id", queryValues.get("status_id"));
        values.put("description", queryValues.get("description"));
        if (!cursor.moveToFirst()) {
            long check = database.insert("substatus", null, values);
            database.close();
            if(check > 0){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            long check= database.update("substatus", values ,"id_substatus"+"="+id, null);
            database.close();
            if(check > 0){
                return true;
            }
            else{
                return false;
            }
        }
    }


    //////////////////************GET LIST CUSTOMERS***************///////////

    public ArrayList get_substatus(String status_id)
    {
        ArrayList<String> data = new ArrayList<String>();
        String query = "SELECT id_substatus,substatus_type,status_id,description  FROM substatus where status_id = '"+status_id+"' ORDER BY id_substatus ";
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id_substatus", cursor.getString(0));
                map.put("substatus_type", cursor.getString(1));
                map.put("status_id", cursor.getString(2));
                map.put("description", cursor.getString(3));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }






}
