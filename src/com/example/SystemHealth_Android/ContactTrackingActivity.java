package com.example.SystemHealth_Android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by emou on 6/26/14.
 */
public class ContactTrackingActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_tracking);

        ContactListFragment contactListFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();

        contactListFragment = (ContactListFragment) fragmentManager.findFragmentById(R.id.contact_list_frag);
        if(contactListFragment==null){
            contactListFragment = new ContactListFragment();
            fragmentManager.beginTransaction().replace(R.id.contact_list_frag,contactListFragment).commit();
        }
    }
}
