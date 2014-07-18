package com.example.SystemHealth_Android;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
/*
 * First screen; displays tile fragments to click and lead into activities
 *
 */
public class MyActivity extends FragmentActivity {

    public static String updateTime = "11:46am June 20, 2014";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //selects layout based on orientation
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.main_landscape);
        }
        else {
            setContentView(R.layout.main_portrait);
        }

/*        String status = null;
        JSONArray jsonArray = null;
        BackgroundJsonTask jsonTask = new BackgroundJsonTask(this);
        try {
            JSONObject jsonObject = jsonTask.execute(R.raw.test2).get();

            if(jsonObject.has("status")){
                status = jsonObject.getString("status");
            }
            if(jsonObject.has("services")){
                jsonArray = (JSONArray) jsonObject.get("services");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        boolean healthy = false;
        if(status.equals("OK")){
            healthy = true;
        }else{
            healthy = false;
        }*/

        FragmentManager fragmentManager = getSupportFragmentManager();

        /*SystemHealthFragment greenFragment = (SystemHealthFragment) fragmentManager.findFragmentById(R.id.green_fragment);
        ProgressFragment progressFragment = (ProgressFragment) fragmentManager.findFragmentById(R.id.progress_fragment);*/
        ContactTrackingTile contactTrackingTile = (ContactTrackingTile) fragmentManager.findFragmentById(R.id.pie_fragment);
        //lazy loads fragments
        if(/*greenFragment==null || progressFragment==null || */contactTrackingTile ==null) {
            /*greenFragment = new SystemHealthFragment(healthy);
            progressFragment = new ProgressFragment();*/
            contactTrackingTile = new ContactTrackingTile();
/*            fragmentManager.beginTransaction().replace(R.id.green_fragment,greenFragment).commit();
            fragmentManager.beginTransaction().replace(R.id.progress_fragment,progressFragment).commit();*/
            fragmentManager.beginTransaction().replace(R.id.pie_fragment, contactTrackingTile).commit();
        }


/*        if(status.equals("Bad")){
            ArrayList<String> stringArrayList = new ArrayList<String>(3);
            for(int i=0;i<3 && i<jsonArray.length() ;i++){
                try {
                    stringArrayList.add(jsonArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(!stringArrayList.isEmpty()){
                greenFragment.text1=stringArrayList.get(0);
            }
            if(stringArrayList.size()>1){
                greenFragment.text2=stringArrayList.get(1);
            }
            if(stringArrayList.size()>2){
                greenFragment.text3=stringArrayList.get(2);
            }
        }*/
    }
}
