package com.example.SystemHealth_Android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by emou on 7/8/14.
 */
public class CTContactListActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_tracking);

        CTContactListFragment contactListFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();

        contactListFragment = (CTContactListFragment) fragmentManager.findFragmentById(R.id.contact_list_frag);
        if(contactListFragment==null){
            contactListFragment = new CTContactListFragment();
            Bundle args = new Bundle();
            args.putInt("requestCode",getIntent().getIntExtra("requestCode",0));
            args.putString("requestDescription",getIntent().getStringExtra("requestDescription"));
            contactListFragment.setArguments(args);
            fragmentManager.beginTransaction().replace(R.id.contact_list_frag,contactListFragment).commit();
        }
    }
}
