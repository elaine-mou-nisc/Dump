package com.example.SystemHealth_Android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by emou on 6/26/14.
 */
public class CTRequestListActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_tracking);

        CTRequestListFragment contactListFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();

        contactListFragment = (CTRequestListFragment) fragmentManager.findFragmentById(R.id.contact_list_frag);
        if(contactListFragment==null){
            contactListFragment = new CTRequestListFragment();
            Bundle args = new Bundle();
            args.putString("date1",getIntent().getStringExtra("date1"));
            args.putString("date2",getIntent().getStringExtra("date2"));

            contactListFragment.setArguments(args);
            fragmentManager.beginTransaction().replace(R.id.contact_list_frag,contactListFragment).commit();
        }
    }
}
