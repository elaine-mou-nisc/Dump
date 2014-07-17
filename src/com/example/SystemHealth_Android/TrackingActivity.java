package com.example.SystemHealth_Android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;

/**
 * Created by emou on 6/26/14.
 */
public class TrackingActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_activity);

        RequestListFragment contactListFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.left_fragment);

        if(frameLayout!=null){//three-pane layout
            contactListFragment = (RequestListFragment) fragmentManager.findFragmentById(R.id.left_fragment);

            if(contactListFragment==null) {
                contactListFragment = new RequestListFragment();
                Bundle args = new Bundle();
                args.putString("date1", getIntent().getStringExtra("date1"));
                args.putString("date2", getIntent().getStringExtra("date2"));

                contactListFragment.setArguments(args);
                fragmentManager.beginTransaction().replace(R.id.left_fragment, contactListFragment).commit();
            }

            if(findViewById(R.id.right_fragment)!=null){
                DatePickerFragment dateFragment = (DatePickerFragment) fragmentManager.findFragmentById(R.id.right_fragment);

                if(dateFragment==null){
                    dateFragment = new DatePickerFragment();
                    fragmentManager.beginTransaction().replace(R.id.right_fragment,dateFragment).commit();
                }
            }

        }else if(findViewById(R.id.contact_list_frag)!=null) {//single pane layout

            contactListFragment = (RequestListFragment) fragmentManager.findFragmentById(R.id.contact_list_frag);

            if(contactListFragment==null) {
                contactListFragment = new RequestListFragment();
                Bundle args = new Bundle();
                args.putString("date1", getIntent().getStringExtra("date1"));
                args.putString("date2", getIntent().getStringExtra("date2"));

                contactListFragment.setArguments(args);
                fragmentManager.beginTransaction().replace(R.id.contact_list_frag, contactListFragment).commit();
            }
        }
    }
}
