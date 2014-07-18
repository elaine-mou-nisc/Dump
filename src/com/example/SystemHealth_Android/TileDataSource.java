package com.example.SystemHealth_Android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Data source for Contact Tracking tile and its pie chart and associated text
 *
 * Created by emou on 6/27/14.
 */
public class TileDataSource {
    private SQLiteDatabase database;
    private TileDBHelper dbHelper;

    public TileDataSource(Context context){
        dbHelper = new TileDBHelper(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    //get top <limit> Requests from the database
    public ArrayList<Request> getTopRequests(int limit){
        ArrayList<Request> requests = new ArrayList<Request>();

        try {
            String query = "SELECT * FROM tileRequests ORDER BY count DESC LIMIT " + limit;
            Cursor cursor = database.rawQuery(query,null);

            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                Request newRequest = new Request(cursor.getString(1),cursor.getInt(2));
                requests.add(newRequest);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            requests.clear();
            e.printStackTrace();
        }

        return requests;
    }

    //sum up how many requests there are that are not included in the top <topNumber> types of request
    public int getOthersCount(int topNumber){
        int count=0;

        String query = "SELECT * FROM tileRequests ORDER BY count DESC";
        Cursor cursor = database.rawQuery(query,null);

        cursor.moveToPosition(topNumber);
        while(!cursor.isAfterLast()){
            count += cursor.getInt(2);
            cursor.moveToNext();
        }
        cursor.close();

        return count;
    }

    public void clearRequestsDB(){
        dbHelper.onUpgrade(database,0,1);
    }

    //add a single request to the database
    public boolean addRequest (Request request){
        boolean success = false;

        ContentValues values = new ContentValues();
        values.put("description",request.mDescription);
        values.put("count",request.mCount);

        if(database.insert("tileRequests",null,values)>0){
            success = true;
        }
        return success;
    }
}
