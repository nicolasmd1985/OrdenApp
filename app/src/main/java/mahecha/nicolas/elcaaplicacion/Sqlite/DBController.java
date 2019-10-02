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


    private static final String NOMBRE_BASE_DATOS = "remitodipzo.db";
    private static final int VERSION_ACTUAL = 1;
    private final Context contexto;




    public DBController(Context contexto) {
        super(contexto,NOMBRE_BASE_DATOS, null, VERSION_ACTUAL);
        this.contexto = contexto;
    }




    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query;
        ///////////////BASE DE USUARIOS//////////////////
        query = "CREATE TABLE usuarios ( idusuario INTEGER PRIMARY KEY, nombre TEXT, apellido TEXT, usuario TEXT, pass TEXT)";
        sqLiteDatabase.execSQL(query);
        ///////////////BASE AUX PEDIDOS//////////////////
        query = "CREATE TABLE aux_pedido ( idauxpedido TEXT PRIMARY KEY, fkidusuario INTEGER REFERENCES usuarios(idusuario) ON DELETE CASCADE, cliente TEXT, descripcion TEXT, idnumsoporte TEXT, calle TEXT, numero TEXT, ciudad TEXT, provincia TEXT, fechacr TEXT, fechack TEXT, finalizado TEXT)";
        sqLiteDatabase.execSQL(query);
        ///////////////BASE AUX PEDIDOS//////////////////
        query = "CREATE TABLE dispositivos ( id_dispositivo INTEGER PRIMARY KEY, codigoscan TEXT, nombre TEXT, descripcion TEXT, latitud TEXT, longitud TEXT, horasca TEXT,  fkidauxpedido TEXT REFERENCES aux_pedido(idauxpedido) ON DELETE CASCADE)";
        sqLiteDatabase.execSQL(query);
        ///////////////BASE DEL REMITO//////////////////
        query = "CREATE TABLE remito ( id_remito INTEGER PRIMARY KEY, fkidauxpedido TEXT REFERENCES aux_pedido(idauxpedido) ON DELETE CASCADE, observaciones TEXT, firma TEXT, aclaracion TEXT, horafinalizado TEXT, email TEXT)";
        sqLiteDatabase.execSQL(query);
        /////////////////UBICACION GPS//////////////////////////
        query = "CREATE TABLE GPSlogs (id_gps INTEGER PRIMARY KEY, longitud TEXT, latitud TEXT )";
        sqLiteDatabase.execSQL(query);
        ///////////////BASE DE TOKEN//////////////////
        query = "CREATE TABLE tokens ( id_token INTEGER PRIMARY KEY, token TEXT, exp TEXT, email TEXT)";
        sqLiteDatabase.execSQL(query);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS usuarios";
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);

        query = "DROP TABLE IF EXISTS aux_pedido";
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);

        query = "DROP TABLE IF EXISTS dispositivos";
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);

        query = "DROP TABLE IF EXISTS remito";
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);

        query = "DROP TABLE IF EXISTS GPSlogs";
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);

        query = "DROP TABLE IF EXISTS tokens";
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);


    }

    @Override
    public void onOpen(SQLiteDatabase sqLiteDatabase)
    {
        super.onOpen(sqLiteDatabase);
        sqLiteDatabase.execSQL("PRAGMA foreign_keys=ON;");
    }


///////////////////*****************TOKEN***************/////////////ok

    /**
     * Inserts User into SQLite DB   * @param queryValues
     */
    public boolean insertToken(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("token", queryValues.get("token"));
        values.put("exp", queryValues.get("exp"));
        values.put("email", queryValues.get("email"));
        long check = database.insert("tokens", null, values);
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
        String query = "SELECT * FROM tokens ORDER BY token DESC LIMIT 1";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToLast();
        data.add(cursor.getString(1));
        data.add(cursor.getString(2));

        return data;
    }

//////////////////************OBTENER IDTECNICO***************///////////

    public String idtecnico()
    {
        String query = "SELECT idusuario FROM usuarios";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        String tecnico = cursor.getString(0);

        return tecnico;
    }


////////////////////*************OBTENER LISTA DE USUARIOS***********///////////ok


    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> getUsers() {
        ArrayList<HashMap<String, String>> wordList;
        //crea lista
        wordList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT  nombre,apellido,usuario,pass FROM usuarios ";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("nombre", cursor.getString(0));
                map.put("apellido", cursor.getString(1));
                map.put("usuario", cursor.getString(2));
                map.put("pass", cursor.getString(3));
               // map.put("tecnico", cursor.getString(4));


                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }


    ///////////////////////QUERY PARA ACCESO////////////////////////////////////ok
    public ArrayList<HashMap<String, String>> login() {
        ArrayList<HashMap<String, String>> logind;
        logind = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  idusuario,usuario,pass FROM usuarios";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("idusuario", cursor.getString(0));
                map.put("usuario", cursor.getString(1));
                map.put("pass", cursor.getString(2));

                logind.add(map);

            }while (cursor.moveToNext());
        }
        database.close();
        return logind;

    }




    ////////////////////////***************QUERY INSERT PEDIDOS****************///////////////////

    /**
     * Inserts User into SQLite DB
     * @param queryValues
     */
    public void inser_auxped(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("idauxpedido", queryValues.get("idauxpedido"));
        values.put("fkidusuario", queryValues.get("idtecnico"));
        values.put("idnumsoporte", queryValues.get("idnumsoporte"));
        values.put("descripcion", queryValues.get("descripcion"));
        values.put("cliente", queryValues.get("cliente"));
        values.put("calle", queryValues.get("calle"));
        values.put("numero", queryValues.get("numero"));
        values.put("ciudad", queryValues.get("ciudad"));
        values.put("provincia", queryValues.get("provincia"));
        values.put("fechacr", queryValues.get("fechacr"));
        values.put("fechack", queryValues.get("fechack"));


        //values.put("udpateStatus", "no");
        database.insert("aux_pedido", null, values);
        database.close();
    }




///////////////////**************OBTIENE USUARIO**************///////////OK
    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> get_auxped(String idusuar) {
        ArrayList<HashMap<String, String>> wordList;
        //crea lista
        wordList = new ArrayList<HashMap<String, String>>();

        //String selectQuery = "SELECT  * FROM aux_pedido where fkidusuario = "+idusuar+" and finalizado is null  ";
        String selectQuery = "SELECT  * FROM aux_pedido where fkidusuario = '"+idusuar+"' and finalizado is null  ";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("idauxpedido", cursor.getString(0));
                //map.put("idtecnico", cursor.getString(1));
                map.put("cliente", cursor.getString(2));
                map.put("descripcion", cursor.getString(3));
                map.put("idnumsoporte", cursor.getString(4));

                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }


    ///////////////////////QUERY PARA ACCESO////////////////////////////////////
    public ArrayList<HashMap<String, String>> listdetalle(String idpedido) {
        ArrayList<HashMap<String, String>> detalle;
        detalle = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  cliente,calle,numero,ciudad,provincia,descripcion FROM aux_pedido where idauxpedido = '"+idpedido+"' ";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("cliente", cursor.getString(0));
                map.put("calle", cursor.getString(1));
                map.put("numero", cursor.getString(2));
                map.put("ciudad", cursor.getString(3));
                map.put("provincia", cursor.getString(4));
                map.put("descripcion", cursor.getString(5));
                // map.put("fechacr", cursor.getString(6));
                //  map.put("fechack", cursor.getString(7));

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
      //  query = "CREATE TABLE dispositivos ( id_dispositivo INTEGER PRIMARY KEY, codigoscan TEXT, nombre TEXT, descripcion TEXT, latitud TEXT, longitud TEXT, horasca TEXT)";
        String selectQuery = "SELECT  codigoscan,nombre,descripcion,latitud,longitud,horasca,fkidauxpedido FROM dispositivos where fkidauxpedido = '"+idped+"'";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("codigoscan", cursor.getString(0));
                map.put("nombre", cursor.getString(1));
                map.put("descripcion", cursor.getString(2));
                map.put("latitud", cursor.getString(3));
                map.put("longitud", cursor.getString(4));
                map.put("horasca", cursor.getString(5));
                map.put("fkidauxpedido", cursor.getString(6));

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
        //values.put("idauxpedido", queryValues.get("idauxpedido"));
        //  query = "CREATE TABLE dispositivos ( id_dispositivo INTEGER PRIMARY KEY, codigoscan TEXT, nombre TEXT, descripcion TEXT, latitud TEXT, longitud TEXT, horasca TEXT)";

        values.put("codigoscan", queryValues.get("codigo"));
        values.put("nombre", queryValues.get("nombre"));
        values.put("descripcion", queryValues.get("descripcion"));
        values.put("latitud", queryValues.get("latitud"));
        values.put("longitud", queryValues.get("longitud"));
        values.put("horasca", queryValues.get("tiempo"));
        values.put("fkidauxpedido", queryValues.get("fkidpedido"));



        //values.put("udpateStatus", "no");
        database.insert("dispositivos", null, values);
        database.close();
    }




    ////////////////*********************ELIMINA DISPOSITIVO**************///////////////

    public void dipsup (String iddisp) {


        SQLiteDatabase database = this.getWritableDatabase();

        database.delete("dispositivos", "codigoscan='"+iddisp+"'", null);

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
        //  query = "CREATE TABLE dispositivos ( id_dispositivo INTEGER PRIMARY KEY, codigoscan TEXT, nombre TEXT, descripcion TEXT, latitud TEXT, longitud TEXT, horasca TEXT)";

        String selectQuery = "SELECT  codigoscan,nombre,descripcion FROM dispositivos where codigoscan ='"+cod+"'";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("codigoscan", cursor.getString(0));
                map.put("nombre", cursor.getString(1));
                map.put("descripcion", cursor.getString(2));


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
        //values.put("idauxpedido", queryValues.get("idauxpedido"));
        values.put("codigoscan", queryValues.get("codigo"));
        values.put("nombre", queryValues.get("nombre"));
        values.put("descripcion", queryValues.get("descripcion"));

        String id =  "'"+queryValues.get("codigo")+"'";

        database.update("dispositivos", values ,"codigoscan"+"="+id, null);
        database.close();
    }


////////////////*****************INSERTAR REMITO***********////////////////

    public void upfoto(HashMap<String, String> queryValues) {
        {
            SQLiteDatabase database = this.getWritableDatabase();


            ContentValues values = new ContentValues();
            values.put("fkidauxpedido", queryValues.get("idpedido"));
            values.put("observaciones", queryValues.get("observaciones"));
            values.put("aclaracion", queryValues.get("aclaracion"));
            values.put("firma", queryValues.get("firma"));
            values.put("horafinalizado", queryValues.get("horafinal"));
            values.put("email", queryValues.get("email"));

            database.insert("remito", null,values );
            database.close();

        }

    }



    //////////////////////***********************ACTUALIZAR ESTADO DEL PEDIDO*********/////////////////

    /**
     * Inserts User into SQLite DB
     * //@param queryValues
     */
    public void upload_aux(String idped) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put("idauxpedido", queryValues.get("idauxpedido"));

        values.put("finalizado", "1" );

        database.update("aux_pedido", values ,"idauxpedido='"+idped+"'", null);
        database.close();
    }





    ////////////////*********************ELIMINA AUX-PEDIDO**************///////////////

    public void elim_aux (String idped) {


        SQLiteDatabase database = this.getWritableDatabase();

        database.delete("aux_pedido", "idauxpedido='"+idped+"'", null);

    }

////////////////*********************CONSULTA REMITO POR PEDIDO**************///////////////


    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<HashMap<String, String>> getremito(String idped) {
        ArrayList<HashMap<String, String>> wordList;
        //crea lista
        wordList = new ArrayList<HashMap<String, String>>();

        ///////QUERY DE DISPOSITIVOS
        //  query = "CREATE TABLE dispositivos ( id_dispositivo INTEGER PRIMARY KEY, codigoscan TEXT, nombre TEXT, descripcion TEXT, latitud TEXT, longitud TEXT, horasca TEXT)";

        String selectQuery = "SELECT  observaciones,firma,aclaracion,horafinalizado,fkidauxpedido,email FROM remito where fkidauxpedido ='"+idped+"'";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("observaciones", cursor.getString(0));
                map.put("firma", cursor.getString(1));
                map.put("aclaracion", cursor.getString(2));
                map.put("horafinalizado", cursor.getString(3));
                map.put("fkidauxpedido", cursor.getString(4));
                map.put("email", cursor.getString(5));

                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }

    ///////////////////////**************CONSULTA REMITOS***************///////////////////


    public ArrayList<HashMap<String, String>> consulrem() {
        ArrayList<HashMap<String, String>> wordList;
        //crea lista
        wordList = new ArrayList<HashMap<String, String>>();

        ///////QUERY DE DISPOSITIVOS
        //  query = "CREATE TABLE dispositivos ( id_dispositivo INTEGER PRIMARY KEY, codigoscan TEXT, nombre TEXT, descripcion TEXT, latitud TEXT, longitud TEXT, horasca TEXT)";

        String selectQuery = "SELECT fkidauxpedido FROM remito";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("fkidauxpedido", cursor.getString(0));

                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
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
        values.put("longitud", queryValues.get("longitud"));
        values.put("latitud", queryValues.get("latitud"));
        database.insert("GPSlogs", null,values );
        database.close();
    }


    public ArrayList<HashMap<String, String>> getgps()
    {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT longitud, latitud FROM GPSlogs";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {

            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("longitud", cursor.getString(0));
                map.put("latitud", cursor.getString(1));
                //map.put("id_gps", cursor.getString(2));

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
    public void updGPS(HashMap<String, String> queryValues) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put("idauxpedido", queryValues.get("idauxpedido"));
        values.put("latitud", queryValues.get("latitud"));
        values.put("longitud", queryValues.get("longitud"));
        database.update("GPSlogs", values ,"id_gps"+"="+1, null);
        database.close();



    }


}
