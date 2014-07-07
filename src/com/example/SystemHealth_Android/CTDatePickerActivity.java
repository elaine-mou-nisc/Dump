package com.example.SystemHealth_Android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;

/**
 * Created by emou on 7/2/14.
 */
public class CTDatePickerActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_date_act);

        CTDatePickerFragment contactDatePickFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.left_fragment);

        if(frameLayout!=null){//three-pane layout
            contactDatePickFragment = (CTDatePickerFragment) fragmentManager.findFragmentById(R.id.left_fragment);

            if(contactDatePickFragment==null){
                contactDatePickFragment = new CTDatePickerFragment();
                fragmentManager.beginTransaction().replace(R.id.left_fragment, contactDatePickFragment).commit();
            }
        }else {//single pane layout

            contactDatePickFragment = (CTDatePickerFragment) fragmentManager.findFragmentById(R.id.contact_date_frag);
            if (contactDatePickFragment == null) {
                contactDatePickFragment = new CTDatePickerFragment();
                fragmentManager.beginTransaction().replace(R.id.contact_date_frag, contactDatePickFragment).commit();
            }
        }
    }

}
