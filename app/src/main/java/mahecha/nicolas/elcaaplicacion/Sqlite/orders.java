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

    /////////////////////**************OBTIENE USUARIO**************///////////OK
    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> get_orders_auto(String user_id) {
        ArrayList<HashMap<String, String>> wordList;
        //crea lista
        wordList = new ArrayList<>();
        String selectQuery = "SELECT id_order, customer_id, description, aux_order FROM orders where fk_user_id = "+user_id+" and finish = 0 and aux_order = 0 ";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id_order", cursor.getString(0));
                map.put("customer_id", cursor.getString(1));
                map.put("description", cursor.getString(2));
                map.put("aux_order", cursor.getString(3));
                map.put("fk_user_id", user_id);

                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }


    /////////////////////**************OBTIENE USUARIO**************///////////OK
    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> get_orders(String user_id) {
        ArrayList<HashMap<String, String>> wordList;
        //crea lista
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT id_order, customer_id, description FROM orders where fk_user_id = "+user_id+" and finish = 0";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id_order", cursor.getString(0));
                map.put("customer_id", cursor.getString(1));
                map.put("description", cursor.getString(2));
                map.put("fk_user_id", user_id);

                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }


    ////////////////////////***************QUERY INSERT PEDIDOS****************///////////////////

    /**
     * Inserts User into SQLite DB
     * @param queryValues
     */
    public boolean insert_order(HashMap<String, String> queryValues, int manual) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String query = "SELECT id_order FROM orders where id_order ="+queryValues.get("id_order");
        Cursor cursor = database.rawQuery(query, null);

        values.put("id_order", queryValues.get("id_order"));
        values.put("fk_user_id", queryValues.get("tecnic_id"));
        values.put("description", queryValues.get("description"));
        values.put("address", queryValues.get("address"));
        values.put("customer_id", queryValues.get("customer_id"));
        values.put("city_id", queryValues.get("city_id"));
        values.put("created_at", queryValues.get("created_at"));
        values.put("install_date", queryValues.get("install_date"));

        if (manual == 1){
            values.put("aux_order", 1);
        }else {
            values.put("aux_order", 0);
        }

        if (!cursor.moveToFirst()) {
            long check = database.insert("orders", null, values);
            database.close();
            if(check > 0){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            long check= database.update("orders", values ,"id_order"+"="+queryValues.get("id_order"), null);
            database.close();
            if(check > 0){
                return true;
            }
            else{
                return false;
            }
        }
    }
}
