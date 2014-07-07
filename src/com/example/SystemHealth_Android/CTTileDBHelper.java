package com.example.SystemHealth_Android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by emou on 6/27/14.
 */
public class CTTileDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "contactTracking.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE_CONTACT = "CREATE TABLE tileRequests (_id integer primary key autoincrement, " +
            "description text not null, count integer);";

    public CTTileDBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CONTACT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tileRequests");
        onCreate(db);
    }
}
