package com.example.SystemHealth_Android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Wrapper activity for ContactListFragment in case of small screens.
 * Fragment shows contact lists based off of request code, which is
 * passed from this activity's intent to the fragment's args.
 *
 * Created by emou on 7/8/14.
 */
public class ContactListActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_wrapper);

        ContactListFragment contactListFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();

        //lazy load fragment with intent's argument for requestCode
        contactListFragment = (ContactListFragment) fragmentManager.findFragmentById(R.id.contact_list_frag);
        if(contactListFragment==null){
            contactListFragment = new ContactListFragment();
            Bundle args = new Bundle();
            args.putInt("requestCode",getIntent().getIntExtra("requestCode",0));
            args.putString("requestDescription",getIntent().getStringExtra("requestDescription"));
            contactListFragment.setArguments(args);
            fragmentManager.beginTransaction().replace(R.id.contact_list_frag,contactListFragment).commit();
        }
    }
}
