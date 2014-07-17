package com.example.SystemHealth_Android;

import android.app.Activity;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by emou on 7/14/14.
 */
public enum DateOptions {
    DAY("Last Day"), WEEK("Last Week"),THIRTY_DAYS("Last 30 Days"),SIXTY_DAYS("Last 60 Days"),NINETY_DAYS("Last 90 Days"),
    YEAR("Last Year"),CUSTOM("Custom Date Range");

    private String description;

    DateOptions(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    @Override
    public String toString(){
        return description;
    }

    /**
     * Created by emou on 6/24/14.
     */
    public static class BackgroundJsonTask extends AsyncTask<Integer,Void,JSONObject> {

        private Activity mActivity;

        public BackgroundJsonTask (Activity activity){
            mActivity = activity;
        }

        @Override
        protected JSONObject doInBackground(Integer... ids) {
            try{
                return findJson(ids[0]);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        private JSONObject findJson(Integer id) throws IOException, JSONException {

            InputStream is = mActivity.getResources().openRawResource(id);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            StringBuilder stringBuilder = new StringBuilder(1024);
            String line;

            while((line = bufferedReader.readLine())!=null){
                stringBuilder.append(line);
            }
            line = stringBuilder.toString();

            return new JSONObject(line);
        }
    }
}
