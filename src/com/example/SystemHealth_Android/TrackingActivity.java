package com.example.SystemHealth_Android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;

/**
 * Contact Tracking activity; displays three vertical columns in large devices for requests, contacts, and
 * date picking, from left to right. On small screens, loads a single fragment for each activity screen, with
 * requests first displayed.
 *
 * Created by emou on 6/26/14.
 */
public class TrackingActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_activity);

        RequestListFragment requestListFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.left_fragment);

        if(frameLayout!=null){//if in three-pane layout
            requestListFragment = (RequestListFragment) fragmentManager.findFragmentById(R.id.left_fragment);
            //lazy load RequestListFragment into leftmost third, passing it dates
            if(requestListFragment==null) {
                requestListFragment = new RequestListFragment();
                fragmentManager.beginTransaction().replace(R.id.left_fragment, requestListFragment).commit();
            }
            //lazy load DatePickerFragment into rightmost third
            if(findViewById(R.id.right_fragment)!=null){
                DatePickerFragment dateFragment = (DatePickerFragment) fragmentManager.findFragmentById(R.id.right_fragment);

                if(dateFragment==null){
                    dateFragment = new DatePickerFragment();
                    fragmentManager.beginTransaction().replace(R.id.right_fragment,dateFragment).commit();
                }
            }

        }else if(findViewById(R.id.contact_list_frag)!=null) {//if in single pane layout

            requestListFragment = (RequestListFragment) fragmentManager.findFragmentById(R.id.contact_list_frag);
            //lazy load RequestListFragment into only FrameLayout
            if(requestListFragment==null) {
                requestListFragment = new RequestListFragment();
                fragmentManager.beginTransaction().replace(R.id.contact_list_frag, requestListFragment).commit();
            }
        }
    }
}
