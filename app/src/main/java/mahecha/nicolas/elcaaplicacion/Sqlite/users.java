package mahecha.nicolas.elcaaplicacion.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class users extends DBController{

    public users(Context contexto) {
        super(contexto);
    }

    ///////////////////*****************INSERT NEW USER***************/////////////ok

    /**
     * Inserts User into SQLite DB   * @param queryValues
     */
    public boolean insertUser(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", queryValues.get("user_id"));
        values.put("first_name", queryValues.get("first_name"));
        values.put("last_name", queryValues.get("last_name"));
        values.put("token", queryValues.get("token"));
        values.put("exp", queryValues.get("exp"));
        values.put("email", queryValues.get("email"));

        long check = database.insert("users", null, values);
        database.close();
        if(check > 0){
            return true;
        }
        else{
            return false;
        }
    }

    //////////////////************OBTENER TOKEN NO EXPIRADO***************///////////

    public ArrayList tokenExp()
    {
        ArrayList<String> data = new ArrayList<String>();
        String query = "SELECT * FROM users ORDER BY user_id DESC LIMIT 1";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if(!(cursor.moveToFirst()) || cursor.getCount() ==0){
            database.close();
            return data;
        }
        else{
            cursor.moveToLast();
            data.add(cursor.getString(0));
            data.add(cursor.getString(1));
            data.add(cursor.getString(2));
            data.add(cursor.getString(3));
            data.add(cursor.getString(4));
            database.close();
            return data;
        }

    }


    //////////////////************GET TECNIC ID***************///////////

    public String tecnic_id()
    {
        String query = "SELECT user_id FROM users ORDER BY user_id DESC LIMIT 1";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if(!(cursor.moveToFirst()) || cursor.getCount() ==0){
            database.close();
            return "null";
        }
        else{
            cursor.moveToLast();
            String tecnico = cursor.getString(0);
            database.close();
            return tecnico;
        }

    }

    ///////////////////***********UPDATE USER**************//////////////////////

    public boolean updateUser(HashMap<String, String> queryValues){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("token", queryValues.get("token"));
        values.put("exp", queryValues.get("exp"));

        String id =  "'"+queryValues.get("user_id")+"'";

        long check= database.update("users", values ,"user_id"+"="+id, null);
        database.close();
        if(check > 0){
            return true;
        }
        else{
            return false;
        }
    }
    //////////////////************GET TECNIC ID***************///////////

    public String customer_name(String customer_id)
    {
        String query = "SELECT first_name, last_name FROM customers ORDER BY customer_id = "+customer_id+" DESC LIMIT 1";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if(!(cursor.moveToFirst()) || cursor.getCount() ==0){
            database.close();
            return "null";
        }
        else{
            cursor.moveToLast();
            String customer = cursor.getString(0) + " " + cursor.getString(1);
            database.close();
            return customer;
        }

    }


}
