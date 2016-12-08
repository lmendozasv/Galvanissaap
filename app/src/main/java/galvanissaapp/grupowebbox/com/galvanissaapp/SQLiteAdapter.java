package galvanissaapp.grupowebbox.com.galvanissaapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SQLiteAdapter   {

    public static final String DATABASE_NAME = "Galvanissadb";
    public static final String LIST_TABLE = "tbl_listas";
    public static final String SUBORDINATES_TABLE = "tbl_subordinados";
    public static final String GEOPOINTS_TABLE = "tbl_geopoints";
    public static final String MYGEOPOINTS_TABLE = "my_ruote_geopoints";


    public static final int MYDATABASE_VERSION = 2;

    public static final String KEY_ID = "_id";
    public static final String KEY_TITULO = "titulo";
    public static final String KEY_CONTENIDO = "contenido";
    public static final String KEY_FECHA = "fecha";
    public static final String KEY_HORA = "hora";

    /*geopoints*/
    public static final String KEY_GEO_ID = "geo_id";
    public static final String KEY_GEO_NAME = "geo_name";
    public static final String KEY_GEO_LAT = "geo_lat";
    public static final String KEY_GEO_LNG = "geo_lng";
    public static final String KEY_GEO_MTS = "geo_mts";
    public static final String KEY_GEO_STATUS = "geo_status";
    public static final String KEY_GEO_CATID = "cat_id";
    public static final String KEY_GEO_POSITIONS = "geo_positions";
    public static final String KEY_GEO_CATNAME = "cat_name";
    public static final String KEY_GEO_CATDESCRIPTION = "cat_description";
    public static final String KEY_GEO_ICON = "cat_icon";
    public static final String KEY_GEO_ZOOMLEVEL = "geo_zoomlevel";


    /*geopoints my route*/

    public static final String KEY_MYGEO_LAT = "geo_lat";
    public static final String KEY_MYGEO_LNG = "geo_lng";
    public static final String KEY_MYGEO_ISCHECK = "is_check";
    public static final String KEY_MYGEO_GEODATE = "geo_date";


    public static final String KEY_USRID = "usrid";
    public static final String KEY_NAME = "nombre";


    String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + SUBORDINATES_TABLE + "("
            + KEY_USRID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT)";


    private static final String SCRIPT_CREATE_DATABASE =
            "create table " + LIST_TABLE + " ("
                    + KEY_ID + " integer primary key autoincrement, "
                    + KEY_TITULO + " text not null, "
                    + KEY_CONTENIDO + " text not null, "
                    + KEY_HORA + " text not null, "
                    + KEY_FECHA + " text not null); "
                    ;
    private static final String SCRIPT_CREATE_TABLE_SUBORDINATES="CREATE TABLE " + SUBORDINATES_TABLE + "("
            + KEY_USRID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT);";

    private static final String SCRIPT_CREATE_GEOPOINTS =
            "create table " + GEOPOINTS_TABLE + " ("
                    + KEY_ID + " integer primary key autoincrement, "
                    + KEY_GEO_ID + " text not null, "
                    + KEY_GEO_NAME + " text not null, "
                    + KEY_GEO_LAT + " text not null, "
                    + KEY_GEO_LNG + " text not null, "
                    + KEY_GEO_MTS + " text not null, "
                    + KEY_GEO_STATUS + " text not null, "
                    + KEY_GEO_CATID + " text not null, "
                    + KEY_GEO_POSITIONS + " text not null, "
                    + KEY_GEO_ZOOMLEVEL + " text not null, "
                    + KEY_GEO_CATNAME + " text not null, "
                    + KEY_GEO_CATDESCRIPTION + " text not null, "
                    + KEY_GEO_ICON + " text not null); ";


    private static final String SCRIPT_CREATE_MYGEOPOINTS =
            "create table " + MYGEOPOINTS_TABLE + " ("
                    + KEY_ID + " integer primary key autoincrement, "
                    + KEY_MYGEO_LAT + " text not null, "
                    + KEY_MYGEO_LNG + " text not null, "
                    +KEY_MYGEO_GEODATE+ " text not null, "
                    + KEY_MYGEO_ISCHECK + " text not null); ";


    private SQLiteHelper sqLiteHelper;
    private SQLiteDatabase sqLiteDatabase;

    private Context context;

    public SQLiteAdapter(Context c){
        context = c;
    }

    public SQLiteAdapter openToRead() throws android.database.SQLException {
        sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, MYDATABASE_VERSION);
        sqLiteDatabase = sqLiteHelper.getReadableDatabase();
        return this;
    }

    public SQLiteAdapter openToWrite() throws android.database.SQLException {
        sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, MYDATABASE_VERSION);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        sqLiteHelper.close();
    }

    public long insert(String titulo,String contenido, String fecha,String hora){

        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_CONTENIDO, contenido);
        contentValues.put(KEY_TITULO, titulo);
        contentValues.put(KEY_FECHA, fecha);
        contentValues.put(KEY_HORA, hora);

        return sqLiteDatabase.insert(LIST_TABLE, null, contentValues);
    }

    public int deleteAll(){
        return sqLiteDatabase.delete(LIST_TABLE, null, null);
    }

    public Cursor queueAll(){
        String[] columns = new String[]{KEY_ID, KEY_CONTENIDO,KEY_TITULO,KEY_FECHA,KEY_HORA};
        Cursor cursor = sqLiteDatabase.query(LIST_TABLE, columns,
                null, null, null, null, null);

        return cursor;
    }

    public class SQLiteHelper extends SQLiteOpenHelper {

        public SQLiteHelper(Context context, String name,
                            CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL(SCRIPT_CREATE_TABLE_SUBORDINATES);

            db.execSQL(SCRIPT_CREATE_DATABASE);

            db.execSQL(SCRIPT_CREATE_GEOPOINTS);

            db.execSQL(SCRIPT_CREATE_MYGEOPOINTS);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub

        }

    }


    public void insertMyGeopoint(
            String geo_lat,
            String geo_lng,
            String geo_ischeck,
            String geo_date
    ){
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MYGEO_LAT, geo_lat);
        values.put(KEY_MYGEO_LNG, geo_lng);
        values.put(KEY_MYGEO_GEODATE, geo_date);
        values.put(KEY_MYGEO_ISCHECK, geo_ischeck);

        // Inserting Row
        try{
            Log.d("Insert GEOPOINT",values.toString());
            db.insert(MYGEOPOINTS_TABLE, null, values);
            db.close(); // Closing database connection

        }
        catch(Exception s){
            Log.d("Error",s.toString());
        }
    }


    public void insertGeopoint(
           String geo_id,
           String geo_name,
           String geo_lat,
           String geo_lng,
           String geo_mts,
           String geo_status,
           String cat_id,
           String geo_positions,
           String geo_zoomlevel,
           String cat_name,
           String cat_description,
           String cat_icon
    ){
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_GEO_ID, geo_id);
        values.put(KEY_GEO_NAME, geo_name);
        values.put(KEY_GEO_LAT, geo_lat);
        values.put(KEY_GEO_LNG, geo_lng);
        values.put(KEY_GEO_MTS, geo_mts);
        values.put(KEY_GEO_STATUS, geo_status);
        values.put(KEY_GEO_CATID, cat_id);
        values.put(KEY_GEO_POSITIONS, geo_positions);
        values.put(KEY_GEO_ZOOMLEVEL, geo_zoomlevel);
        values.put(KEY_GEO_CATNAME, cat_name);
        values.put(KEY_GEO_CATDESCRIPTION, cat_description);
        values.put(KEY_GEO_ICON, cat_icon);

        // Inserting Row
        try{
            Log.d("Insert GEOPOINT",values.toString());
            db.insert(GEOPOINTS_TABLE, null, values);
            db.close(); // Closing database connection

        }
        catch(Exception s){
            Log.d("Error",s.toString());
        }
    }

    public int deleteAllGeopoints(){
        return sqLiteDatabase.delete(GEOPOINTS_TABLE, null, null);
    }

    public void insertSubordinate(String name,String id){
        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_USRID, id);

        // Inserting Row
        try{
            Log.d("Insertando SQLITE",values.toString());
            db.insert(SUBORDINATES_TABLE, null, values);
            db.close(); // Closing database connection

        }
catch(Exception s){
Log.d("Error",s.toString());
}
    }

    public Cursor getMyGeopoints(){
        sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, MYDATABASE_VERSION);

        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();


        Cursor cu=null;


        cu= db.query(MYGEOPOINTS_TABLE, new String[] {
                        KEY_ID,
                        KEY_MYGEO_LAT,
                        KEY_MYGEO_LNG,
                        KEY_MYGEO_GEODATE,
                        KEY_MYGEO_ISCHECK
                } ,
                null,
                null,
                null,
                null,
                null
        );
        Log.d(" CURSORS MY",String.valueOf(cu.getCount()));
        return cu;
    }
    public Cursor getAllGeopoints(){
        sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, MYDATABASE_VERSION);

        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();


        Cursor cu=null;


        cu= db.query(GEOPOINTS_TABLE, new String[] {
                KEY_GEO_ID,
                KEY_GEO_NAME ,
                KEY_GEO_LAT,
                KEY_GEO_LNG,
                KEY_GEO_MTS,
                KEY_GEO_STATUS,
                KEY_GEO_CATID,
                KEY_GEO_POSITIONS,
                KEY_GEO_CATNAME,
                KEY_GEO_CATDESCRIPTION,
                KEY_GEO_ICON
        } ,
                null,
                null,
                null,
                null,
                null
        );
        Log.d(" CURSORS ",String.valueOf(cu.getCount()));
return cu;
    }


    public List<String> getSubordinates(){
        List<String> labels = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + SUBORDINATES_TABLE;


        sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, MYDATABASE_VERSION);

        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }


    public String EmployeeID(String name){
        String empid="";
        String selectQuery = "SELECT usrid from "+SUBORDINATES_TABLE+" where "+KEY_NAME+" = '" + name+"' ";

        sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, MYDATABASE_VERSION);

        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            empid = cursor.getString(0);
        }
        return empid;
    }


    public String EmployeeName(String name){
        String empid="";
        String selectQuery = "SELECT "+KEY_NAME+" from "+SUBORDINATES_TABLE+" where "+KEY_USRID+" = '" + name+"' ";
        Log.d("qry",selectQuery);
        sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, MYDATABASE_VERSION);

        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()){
            empid = cursor.getString(0);
        }
        return empid;
    }
}