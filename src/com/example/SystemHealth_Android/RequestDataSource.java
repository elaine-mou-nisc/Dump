package com.example.SystemHealth_Android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Data source for Requests in Contact Tracking
 *
 * Created by emou on 7/7/14.
 */
public class RequestDataSource {

    private SQLiteDatabase database;
    private RequestDBHelper dbHelper;

    public RequestDataSource(Context context){
        dbHelper = new RequestDBHelper(context);
    }

    public void open(){
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    //retrieves all requests with preferred sorting
    public ArrayList<Request> getRequests(String sortField, String sortOrder){
        ArrayList<Request> requests = new ArrayList<Request>();

        try {
            String query = "SELECT * FROM requests ORDER BY " + sortField + " " + sortOrder;
            Cursor cursor = database.rawQuery(query,null);

            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                Request newRequest = new Request(cursor.getString(1),cursor.getInt(2));
                newRequest.mCode = cursor.getInt(0);
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

    public void clearRequestsDB(){
        dbHelper.onUpgrade(database,0,1);
    }

    //adds given list of Requests to the SQLite database
    public int addToRequestList(ArrayList<Request> requests){
        int count=0;
        int numberOfRequests = requests.size();
        Request request;
        ContentValues values;

        for(int i=0;i<numberOfRequests;i++){
            request = requests.get(i);

            values = new ContentValues();
            values.put("code",request.mCode);
            values.put("description", request.mDescription);
            values.put("count", request.mCount);

            if(database.insert("requests",null,values)>0){
                count++;
            }
        }

        return count;
    }
}
