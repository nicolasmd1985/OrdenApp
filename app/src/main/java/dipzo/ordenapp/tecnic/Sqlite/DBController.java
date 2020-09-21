package dipzo.ordenapp.tecnic.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by nicolas on 27/07/2016.
 * MANEJO DE BASE DE DATOS SQLITE
 */
public class DBController extends SQLiteOpenHelper {


    private static final String NOMBRE_BASE_DATOS = "ordenapp18092020.db";
    private static final int VERSION_ACTUAL = 4;
    private final Context contexto;




    public DBController(Context contexto) {
        super(contexto,NOMBRE_BASE_DATOS, null, VERSION_ACTUAL);
        this.contexto = contexto;
    }




    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query;

        ///////////////USERS//////////////////
        query = "CREATE TABLE users ( user_id INTEGER PRIMARY KEY, first_name TEXT, last_name TEXT, token TEXT, exp TEXT, email TEXT, status_id TEXT)";
        sqLiteDatabase.execSQL(query);
        ///////////////ORDERS//////////////////
        query = "CREATE TABLE orders ( id_order INTEGER PRIMARY KEY, fk_user_id INTEGER REFERENCES users(user_id) ON UPDATE CASCADE ON DELETE CASCADE, description TEXT, address TEXT, city_id TEXT, priority TEXT, created_at TEXT, supervisor_id TEXT, customer_id TEXT, install_date TEXT , install_time TEXT, limit_time TEXT, category_id Text, finish INTEGER DEFAULT 0, aux_order INTEGER DEFAULT 0)";
        sqLiteDatabase.execSQL(query);
        ///////////////THINGS//////////////////
        query = "CREATE TABLE things ( id_thing INTEGER PRIMARY KEY, fk_order_id TEXT REFERENCES orders(id_order) ON UPDATE CASCADE ON DELETE CASCADE , code_scan TEXT, name TEXT, description TEXT, comments TEXT, latitude TEXT, longitude TEXT, time_install TEXT, photos TEXT, price TEXT, warranty TEXT)";
        sqLiteDatabase.execSQL(query);
        ///////////////REFERRALS//////////////////
        query = "CREATE TABLE referrals ( id_referral INTEGER PRIMARY KEY, fk_order_id TEXT REFERENCES orders(id_order) ON UPDATE CASCADE ON DELETE CASCADE, comment TEXT, signature TEXT, full_name TEXT, final_time TEXT, email TEXT)";
        sqLiteDatabase.execSQL(query);
        /////////////////UBICACION GPS//////////////////////////
        query = "CREATE TABLE GPSlogs (id_gps INTEGER PRIMARY KEY, longitude TEXT, latitude TEXT )";
        sqLiteDatabase.execSQL(query);
        ///////////////CUSTOMERS//////////////////
        query = "CREATE TABLE customers ( customer_id INTEGER PRIMARY KEY, first_name TEXT, last_name TEXT, email TEXT, phone_number TEXT, city TEXT)";
        sqLiteDatabase.execSQL(query);



    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS users";
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);

        query = "DROP TABLE IF EXISTS orders";
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);

        query = "DROP TABLE IF EXISTS things";
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);

        query = "DROP TABLE IF EXISTS referrals";
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);

        query = "DROP TABLE IF EXISTS GPSlogs";
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);

        query = "DROP TABLE IF EXISTS customers";
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onOpen(SQLiteDatabase sqLiteDatabase)
    {
        super.onOpen(sqLiteDatabase);
        sqLiteDatabase.execSQL("PRAGMA foreign_keys=ON;");
    }





///////////////////*****************INSERT NEW CUSTOMERS***************/////////////ok

    /**
     * Inserts User into SQLite DB   * @param queryValues
     */
    public boolean insertCustomers(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String id = queryValues.get("customer_id");
        String query = "SELECT customer_id FROM customers where customer_id ="+id;
        Cursor cursor = database.rawQuery(query, null);
        values.put("customer_id", queryValues.get("customer_id"));
        values.put("first_name", queryValues.get("first_name"));
        values.put("last_name", queryValues.get("last_name"));
        values.put("email", queryValues.get("email"));
        values.put("phone_number", queryValues.get("phone_number"));
        values.put("city", queryValues.get("city"));
        if (!cursor.moveToFirst()) {
            long check = database.insert("customers", null, values);
            database.close();
            if(check > 0){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            long check= database.update("customers", values ,"customer_id"+"="+id, null);
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

    public ArrayList customers()
    {
        ArrayList<String> data = new ArrayList<String>();
        String query = "SELECT customer_id,first_name,last_name, email, phone_number, city  FROM customers ORDER BY customer_id";
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("customer_id", cursor.getString(0));
                map.put("first_name", cursor.getString(1));
                map.put("last_name", cursor.getString(2));
                map.put("email", cursor.getString(3));
                map.put("phone_number", cursor.getString(4));
                map.put("city", cursor.getString(5));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }











//



//    ///////////////////////QUERY PARA ACCESO////////////////////////////////////
    public ArrayList<HashMap<String, String>> listdetalle(String id_order) {
//        description TEXT, address TEXT, city_id TEXT, priority TEXT, created_at TEXT, supervisor_id TEXT, customer_id TEXT, install_date TEXT , install_time TEXT, limit_time TEXT, category_id Text, finish INTEGER DEFAULT 0, aux_order INTEGER DEFAULT 0
        ArrayList<HashMap<String, String>> detalle;
        detalle = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT description, address, city_id, customer_id, install_time, category_id, limit_time FROM orders where id_order = '"+id_order+"' ";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("description", cursor.getString(0));
                map.put("address", cursor.getString(1));
                map.put("city_id", cursor.getString(2));
                map.put("customer_id", cursor.getString(3));
                map.put("install_time", cursor.getString(4));
                map.put("category_id", cursor.getString(5));
                map.put("limit_time", cursor.getString(6));


                detalle.add(map);

            }while (cursor.moveToNext());
        }
        database.close();
        return detalle;

    }


      ////////////////////***********OBTENER DISPOSITIVOS***********////////////

    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> getdisp(String idped) {
        ArrayList<HashMap<String, String>> wordList;
        //crea lista
        wordList = new ArrayList<HashMap<String, String>>();

        ///////QUERY DE DISPOSITIVOS
        String selectQuery = "SELECT  code_scan,name,description,comments,latitude,longitude,time_install,fk_order_id,photos, price, warranty FROM things where fk_order_id = '"+idped+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("code_scan", cursor.getString(0));
                map.put("name", cursor.getString(1));
                map.put("description", cursor.getString(2));
                map.put("comments", cursor.getString(3));
                map.put("latitude", cursor.getString(4));
                map.put("longitude", cursor.getString(5));
                map.put("time_install", cursor.getString(6));
                map.put("fk_order_id", cursor.getString(7));
                map.put("photos", cursor.getString(8));
                map.put("price", cursor.getString(9));
                map.put("warranty", cursor.getString(10));

                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }


    ////////////////////////***************QUERY INSERT PEDIDOS****************///////////////////


    public void inserdips(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("code_scan", queryValues.get("code_scan"));
        values.put("name", queryValues.get("name"));
        values.put("description", queryValues.get("description"));
        values.put("comments", queryValues.get("comments"));
        values.put("latitude", queryValues.get("latitude"));
        values.put("longitude", queryValues.get("longitude"));
        values.put("time_install", queryValues.get("time_install"));
        values.put("fk_order_id", queryValues.get("fk_order_id"));
        values.put("photos", queryValues.get("photos"));
        values.put("price", queryValues.get("price"));
        values.put("warranty", queryValues.get("warranty"));

        
        //values.put("udpateStatus", "no");
        database.insert("things", null, values);
        database.close();
    }


    ////////////////*********************ELIMINA DISPOSITIVO**************///////////////

    public void dipsup (String iddisp) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete("things", "code_scan='"+iddisp+"'", null);
    }


////////////////////***********OBTENER DISPOSITIVOS POR CODIGO***********////////////

    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> getdispcod(String cod) {
        ArrayList<HashMap<String, String>> wordList;
        //crea lista
        wordList = new ArrayList<HashMap<String, String>>();

        ///////QUERY DE DISPOSITIVOS

        String selectQuery = "SELECT  code_scan,name,description FROM things where code_scan ='"+cod+"'";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("code_scan", cursor.getString(0));
                map.put("name", cursor.getString(1));
                map.put("description", cursor.getString(2));


                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }


//////////////////////************************MODIFICAR DISPOSITIVOS*********/////////////////
    public void updips(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("code_scan", queryValues.get("code_scan"));
        values.put("name", queryValues.get("name"));
        values.put("description", queryValues.get("description"));
        String id =  "'"+queryValues.get("code_scan")+"'";
        database.update("things", values ,"code_scan"+"="+id, null);
        database.close();
    }

////////////////*****************INSERTAR REMITO***********////////////////

    public void insert_referral(HashMap<String, String> queryValues) {
        {
            SQLiteDatabase database = this.getWritableDatabase();


            ContentValues values = new ContentValues();
            values.put("fk_order_id", queryValues.get("id_order"));
            values.put("comment", queryValues.get("comment"));
            values.put("full_name", queryValues.get("full_name"));
            values.put("signature", queryValues.get("signature"));
            values.put("final_time", queryValues.get("final_time"));
            values.put("email", queryValues.get("email"));

            database.insert("referrals", null,values );
            database.close();

        }

    }






    ////////////////*********************ELIMINA AUX-PEDIDO**************///////////////

    public void elim_aux (String idped) {


        SQLiteDatabase database = this.getWritableDatabase();

        database.delete("orders", "id_order='"+idped+"'", null);

    }

////////////////*********************CONSULTA REMITO POR PEDIDO**************///////////////


    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> get_referral(String idped) {
        ArrayList<HashMap<String, String>> wordList;
        //crea lista
        wordList = new ArrayList<HashMap<String, String>>();

         String selectQuery = "SELECT  comment,signature,full_name,final_time,email FROM referrals where fk_order_id ='"+idped+"'";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("comment", cursor.getString(0));
                map.put("signature", cursor.getString(1));
                map.put("full_name", cursor.getString(2));
                map.put("final_time", cursor.getString(3));
                map.put("email", cursor.getString(4));

                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }





    public Boolean finish_order(Integer id_order){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("finish", 1);
        long check= database.update("orders", values ,"id_order"+"="+id_order, null);
        database.close();
        if(check > 0){
            return true;
        }
        else{
            return false;
        }
    }






    //////////////////*******************ACTUALIZA SEGUIMIENTO GPS********************//////////////////////



    public void upgps(HashMap<String, String> queryValues) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("longitude", queryValues.get("longitude"));
        values.put("latitude", queryValues.get("latitude"));
        database.insert("GPSlogs", null,values );
        database.close();
    }


    public ArrayList getgps()
    {
        ArrayList<String> data = new ArrayList<String>();
        String query = "SELECT longitude, latitude FROM GPSlogs ORDER BY id_gps DESC LIMIT 1";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToLast();
        data.add(cursor.getString(0));
        data.add(cursor.getString(1));
        return data;
    }





}
