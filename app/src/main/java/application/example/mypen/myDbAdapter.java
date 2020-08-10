package application.example.mypen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class myDbAdapter {
    myDbHelper myhelper;

    private static final String TAG = "MY_DB_ADAPTER";        // For log messages

    public myDbAdapter(Context context)
    {
        myhelper = new myDbHelper(context);
    }

    public long insertData(String font_name, String lowercase, String uppercase, String numbers, String symbols)
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.FONTNAME, font_name);
        contentValues.put(myDbHelper.LOWERCASE, lowercase);
        contentValues.put(myDbHelper.UPPERCASE, uppercase);
        contentValues.put(myDbHelper.NUMBERS, numbers);
        contentValues.put(myDbHelper.SYMBOLS, symbols);
        long id = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
        return id;
    }

    public String[] getData(String font_name)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {"_id", "FontName", "Lowercase", "Uppercase", "Numbers", "Symbols"};
        String str_selection = "FontName = ?";

        String[] selectionArgs = {font_name};

        Cursor cursor = db.query(myDbHelper.TABLE_NAME,columns, str_selection, selectionArgs,null,null,null);

        String[] result = new String[6];

        while (cursor.moveToNext())
        {
            int uid =cursor.getInt(cursor.getColumnIndex(myDbHelper.UID));
            result[0] = String.valueOf(uid);
            String fontname =cursor.getString(cursor.getColumnIndex(myDbHelper.FONTNAME));
            result[1] = fontname;
            String lowercase =cursor.getString(cursor.getColumnIndex(myDbHelper.LOWERCASE));
            result[2] = lowercase;
            String uppercase =cursor.getString(cursor.getColumnIndex(myDbHelper.UPPERCASE));
            result[3] = uppercase;
            String numbers =cursor.getString(cursor.getColumnIndex(myDbHelper.NUMBERS));
            result[4] = numbers;
            String symbols =cursor.getString(cursor.getColumnIndex(myDbHelper.SYMBOLS));
            result[5] = symbols;

        }

        return result;
    }

    public int deleteData(int uid)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={String.valueOf(uid)};

        int count = db.delete(myDbHelper.TABLE_NAME ,myDbHelper.UID+" = ?",whereArgs);
        return count;
    }

//    public int updateData(int uid, String datetime, String description)
//    {
//        SQLiteDatabase db = myhelper.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(myDbHelper.DATETIME,datetime);
//        contentValues.put(myDbHelper.DESCRIPTION,description);
//        String[] whereArgs= {String.valueOf(uid)};
//        int count =db.update(myDbHelper.TABLE_NAME,contentValues, myDbHelper.UID+" = ?",whereArgs );
//        return count;
//    }

    static class myDbHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "myDatabase";
        private static final String TABLE_NAME = "font_details";
        private static final int DATABASE_Version = 1;
        private static final String UID="_id";
        private static final String FONTNAME = "FontName";
        private static final String LOWERCASE = "Lowercase";
        private static final String UPPERCASE = "Uppercase";
        private static final String NUMBERS = "Numbers";
        private static final String SYMBOLS = "Symbols";
        private static final String CREATE_TABLE = "CREATE TABLE "+ TABLE_NAME +
                " ("+ UID +" INTEGER PRIMARY KEY AUTOINCREMENT, " + FONTNAME +" VARCHAR(30), " + LOWERCASE + " VARCHAR(50), " + UPPERCASE + " VARCHAR(50), " + NUMBERS + " VARCHAR(50), " + SYMBOLS + " VARCHAR(50));";
        private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+ TABLE_NAME;
        private Context context;

        public myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context=context;
        }

        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_TABLE);
            } catch (Exception e) {
                Log.d(TAG, "" + e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(DROP_TABLE);
                onCreate(db);
            }catch (Exception e) {
                Log.d(TAG, "" + e);
            }
        }
    }
}