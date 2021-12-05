package com.example.musify.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.musify.model.UriStore;
import com.example.musify.params.Params;

import java.util.ArrayList;
import java.util.List;

public class MyDbHandler extends SQLiteOpenHelper {

    public MyDbHandler(Context context)
    {
        super(context, Params.DB_NAME,null,Params.DB_VERSION);
    }

    String create =
            " CREATE TABLE IF NOT EXISTS " + Params.TABLE_URI  + " (" +
                    Params.KEY_ID + " TEXT, " +
                    Params.KEY_NAME + " TEXT, " +
                    Params.FAV_STATUS + " TEXT, " +
                    Params.KEY_URI + " TEXT)";

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String create = "CREATE TABLE "+Params.TABLE_URI +"("+Params.KEY_ID+" INTEGER PRIMARY KEY," +
//                Params.KEY_NAME + " TEXT, " + Params.KEY_URI + " TEXT, "  + Params.FAV_STATUS + " TEXT" + ")";

        Log.d("DB_Gourav","Query ---->"+create);
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        // enter your value
        for (int x = 1; x < 100; x++) {
            cv.put(Params.KEY_NAME,"");
            cv.put(Params.KEY_ID,x);
            cv.put(Params.KEY_URI,"");
            cv.put(Params.FAV_STATUS, "0");
            db.insert(Params.TABLE_URI,null, cv);
        }
        db.close();
    }

    public void addUri(UriStore uriStore)
    {
        SQLiteDatabase dbs = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String name = uriStore.getMname();
        name = name.replaceAll(" ","");
        name = name.replaceAll("[^a-zA-Z0-9]", "");
        name = name.toLowerCase();
        values.put(Params.KEY_NAME,name);
        values.put(Params.KEY_URI,uriStore.getUrifile());
        values.put(Params.FAV_STATUS,"1");
        dbs.insert(Params.TABLE_URI,null,values);
        Log.d("DB_Gourav", "Succesfully inserted");
        dbs.close();
    }


    public void removeUri(String name)
    {
        SQLiteDatabase dbs = this.getWritableDatabase();
        name = name.replaceAll(" ","");
        name = name.replaceAll("[^a-zA-Z0-9]", "");
        name = name.toLowerCase();
        String select = "UPDATE " + Params.TABLE_URI +" SET " + Params.FAV_STATUS + "='0' " + " WHERE "+ Params.KEY_NAME + "=" + "'" + name+"'";
        dbs.execSQL(select);
        Log.d("DB_Gourav", name);
        dbs.close();
    }

    public Cursor getAllUri(String name)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        name = name.replaceAll(" ","");
        name = name.replaceAll("[^a-zA-Z0-9]", "");
        name = name.toLowerCase();
        String select = "SELECT * FROM " + Params.TABLE_URI + " WHERE "+ Params.KEY_NAME + "=" + "'" + name+"'";
        return db.rawQuery(select,null,null);
    }

    public List<UriStore> select_all_fav_list()
    {
        List<UriStore> favList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT * FROM " + Params.TABLE_URI + " WHERE " + Params.FAV_STATUS + "=" + "'1'";
        Cursor cursor = db.rawQuery(select,null,null);

        if (cursor.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                favList.add(new UriStore(cursor.getString(cursor.getColumnIndex(Params.KEY_NAME)),cursor.getString(cursor.getColumnIndex(Params.KEY_URI))));
            } while (cursor.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursor.close();
        return favList;
    }

}
