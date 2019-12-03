package mahecha.nicolas.elcaaplicacion.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by nicolas on 27/07/2016.
 * MANEJO DE BASE DE DATOS SQLITE
 */
public class DBController extends SQLiteOpenHelper {


    private static final String NOMBRE_BASE_DATOS = "ordenapp3.db";
    private static final int VERSION_ACTUAL = 1;
    private final Context contexto;




    public DBController(Context contexto) {
        super(contexto,NOMBRE_BASE_DATOS, null, VERSION_ACTUAL);
        this.contexto = contexto;
    }




    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query;

        ///////////////USERS//////////////////
        query = "CREATE TABLE users ( user_id INTEGER PRIMARY KEY, first_name TEXT, last_name TEXT, token TEXT, exp TEXT, email TEXT)";
        sqLiteDatabase.execSQL(query);
        ///////////////ORDERS//////////////////
        query = "CREATE TABLE orders ( id_order INTEGER PRIMARY KEY, fk_user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE, description TEXT, address TEXT, city_id TEXT, priority TEXT, created_at TEXT, supervisor_id TEXT, customer_id TEXT, install_date TEXT , install_time TEXT, finish INTEGER DEFAULT 0)";
        sqLiteDatabase.execSQL(query);
        ///////////////THINGS//////////////////
        query = "CREATE TABLE things ( id_thing INTEGER PRIMARY KEY, code_scan TEXT, name TEXT, description TEXT, latitude TEXT, longitude TEXT, time_install TEXT, fk_order_id TEXT REFERENCES orders(id_order) ON DELETE CASCADE)";
        sqLiteDatabase.execSQL(query);
        ///////////////REFERRALS//////////////////
        query = "CREATE TABLE referrals ( id_referral INTEGER PRIMARY KEY, fk_id_order TEXT REFERENCES orders(id_order) ON DELETE CASCADE, comment TEXT, signature TEXT, full_name TEXT, final_time TEXT, email TEXT)";
        sqLiteDatabase.execSQL(query);
        /////////////////UBICACION GPS//////////////////////////
        query = "CREATE TABLE GPSlogs (id_gps INTEGER PRIMARY KEY, longitude TEXT, latitude TEXT )";
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
    }

    @Override
    public void onOpen(SQLiteDatabase sqLiteDatabase)
    {
        super.onOpen(sqLiteDatabase);
        sqLiteDatabase.execSQL("PRAGMA foreign_keys=ON;");
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

//////////////////************OBTENER TOKEN NO EXPIRADO***************///////////

    public ArrayList tokenExp()
    {
        ArrayList<String> data = new ArrayList<String>();
        String query = "SELECT * FROM users ORDER BY user_id DESC LIMIT 1";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToLast();
        data.add(cursor.getString(0));
        data.add(cursor.getString(1));
        data.add(cursor.getString(2));
        data.add(cursor.getString(3));
        data.add(cursor.getString(4));

        return data;
    }

//////////////////************GET TECNIC ID***************///////////

    public String tecnic_id()
    {
        String query = "SELECT user_id FROM users ORDER BY user_id DESC LIMIT 1";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if(!(cursor.moveToFirst()) || cursor.getCount() ==0){
            return "null";
        }
        else{
            cursor.moveToLast();
            String tecnico = cursor.getString(0);
            return tecnico;
        }

    }


////////////////////*************OBTENER LISTA DE USUARIOS***********///////////ok
//
//
//    /**
//     * Get list of Users from SQLite DB as Array List
//     * @return
//     */
//    public ArrayList<HashMap<String, String>> getUsers() {
//        ArrayList<HashMap<String, String>> wordList;
//        //crea lista
//        wordList = new ArrayList<HashMap<String, String>>();
//
//        String selectQuery = "SELECT  nombre,apellido,usuario,pass FROM usuarios ";
//
//        SQLiteDatabase database = this.getWritableDatabase();
//        Cursor cursor = database.rawQuery(selectQuery, null);
//        if (cursor.moveToFirst()) {
//            do {
//                HashMap<String, String> map = new HashMap<String, String>();
//                map.put("nombre", cursor.getString(0));
//                map.put("apellido", cursor.getString(1));
//                map.put("usuario", cursor.getString(2));
//                map.put("pass", cursor.getString(3));
//               // map.put("tecnico", cursor.getString(4));
//
//
//                wordList.add(map);
//            } while (cursor.moveToNext());
//        }
//        database.close();
//        return wordList;
//    }


    ///////////////////////QUERY PARA ACCESO////////////////////////////////////ok
//    public ArrayList<HashMap<String, String>> login() {
//        ArrayList<HashMap<String, String>> logind;
//        logind = new ArrayList<HashMap<String, String>>();
//        String selectQuery = "SELECT  idusuario,usuario,pass FROM usuarios";
//        SQLiteDatabase database = this.getWritableDatabase();
//        Cursor cursor = database.rawQuery(selectQuery, null);
//        if (cursor.moveToFirst()) {
//            do {
//                HashMap<String, String> map = new HashMap<String, String>();
//
//                map.put("idusuario", cursor.getString(0));
//                map.put("usuario", cursor.getString(1));
//                map.put("pass", cursor.getString(2));
//
//                logind.add(map);
//
//            }while (cursor.moveToNext());
//        }
//        database.close();
//        return logind;
//
//    }




    ////////////////////////***************QUERY INSERT PEDIDOS****************///////////////////

    /**
     * Inserts User into SQLite DB
     * @param queryValues
     */
    public void insert_order(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id_order", queryValues.get("id_order"));
        values.put("fk_user_id", queryValues.get("tecnic_id"));
        values.put("description", queryValues.get("description"));
        values.put("address", queryValues.get("address"));
        values.put("customer_id", queryValues.get("customer_id"));
        values.put("city_id", queryValues.get("city_id"));
        values.put("created_at", queryValues.get("created_at"));
        values.put("install_date", queryValues.get("install_date"));


        //values.put("udpateStatus", "no");
        database.insert("orders", null, values);
        database.close();
    }



//
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


//    ///////////////////////QUERY PARA ACCESO////////////////////////////////////
    public ArrayList<HashMap<String, String>> listdetalle(String id_order) {
        ArrayList<HashMap<String, String>> detalle;
        detalle = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  customer_id,address,city_id,description FROM orders where id_order = '"+id_order+"' ";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("cliente", cursor.getString(0));
                map.put("calle", cursor.getString(1));
                map.put("ciudad", cursor.getString(2));
                map.put("descripcion", cursor.getString(3));

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
        String selectQuery = "SELECT  code_scan,name,description,latitude,longitude,time_install,fk_order_id FROM things where fk_order_id = '"+idped+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("code_scan", cursor.getString(0));
                map.put("name", cursor.getString(1));
                map.put("description", cursor.getString(2));
                map.put("latitude", cursor.getString(3));
                map.put("longitude", cursor.getString(4));
                map.put("time_install", cursor.getString(5));
                map.put("fk_order_id", cursor.getString(6));

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
    public void inserdips(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("code_scan", queryValues.get("code_scan"));
        values.put("name", queryValues.get("name"));
        values.put("description", queryValues.get("description"));
        values.put("latitude", queryValues.get("latitude"));
        values.put("longitude", queryValues.get("longitude"));
        values.put("time_install", queryValues.get("time_install"));
        values.put("fk_order_id", queryValues.get("fk_order_id"));



        //values.put("udpateStatus", "no");
        database.insert("things", null, values);
        database.close();
    }




    ////////////////*********************ELIMINA DISPOSITIVO**************///////////////

    public void dipsup (String iddisp) {


        SQLiteDatabase database = this.getWritableDatabase();

        database.delete("things", "codigoscan='"+iddisp+"'", null);

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

    /**
     * Inserts User into SQLite DB
     * @param queryValues
     */
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
            values.put("fk_id_order", queryValues.get("id_order"));
            values.put("comment", queryValues.get("comment"));
            values.put("full_name", queryValues.get("full_name"));
            values.put("signature", queryValues.get("signature"));
            values.put("final_time", queryValues.get("final_time"));
            values.put("email", queryValues.get("email"));

            database.insert("referrals", null,values );
            database.close();

        }

    }



    //////////////////////***********************ACTUALIZAR ESTADO DEL PEDIDO*********/////////////////

    /**
     * Inserts User into SQLite DB
     * //@param queryValues
     */
//    public void upload_aux(String idped) {
//        SQLiteDatabase database = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        //values.put("idauxpedido", queryValues.get("idauxpedido"));
//
//        values.put("finalizado", "1" );
//
//        database.update("aux_pedido", values ,"idauxpedido='"+idped+"'", null);
//        database.close();
//    }





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

        ///////QUERY DE DISPOSITIVOS
//        query = "CREATE TABLE referrals ( id_referral INTEGER PRIMARY KEY, fk_id_order TEXT REFERENCES orders(id_order) ON DELETE CASCADE, comment TEXT, signature TEXT, full_name TEXT, final_time TEXT, email TEXT)";


        String selectQuery = "SELECT  comment,signature,full_name,final_time,email FROM referrals where fk_id_order ='"+idped+"'";

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

    ///////////////////////**************CONSULTA REMITOS***************///////////////////


    public ArrayList<HashMap<String, String>> get_refferals() {
        ArrayList<HashMap<String, String>> wordList;
        //crea lista
        wordList = new ArrayList<HashMap<String, String>>();

        ///////QUERY DE DISPOSITIVOS
        String selectQuery = "SELECT fk_id_order FROM referrals";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("fk_id_order", cursor.getString(0));

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


    //////////////////**********************QUERY IDNUMSOP**********/////////


    public ArrayList<HashMap<String, String>> conidsop()
    {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT idauxpedido,fkidusuario,cliente,descripcion,calle,numero,ciudad,provincia FROM aux_pedido where idnumsoporte is null";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {

            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("idauxpedido", cursor.getString(0));
                map.put("fkidusuario", cursor.getString(1));
                map.put("cliente", cursor.getString(2));
                map.put("descripcion", cursor.getString(3));
                map.put("calle", cursor.getString(4));
                map.put("numero", cursor.getString(5));
                map.put("ciudad", cursor.getString(6));
                map.put("provincia", cursor.getString(7));

                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
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
