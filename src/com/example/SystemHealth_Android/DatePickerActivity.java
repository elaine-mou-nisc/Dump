package com.example.SystemHealth_Android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by emou on 7/2/14.
 */
public class DatePickerActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_wrapper);

        DatePickerFragment contactDatePickFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();

        contactDatePickFragment = (DatePickerFragment) fragmentManager.findFragmentById(R.id.wrapped_fragment);
        if (contactDatePickFragment == null) {
            contactDatePickFragment = new DatePickerFragment();
        }

        fragmentManager.beginTransaction().replace(R.id.wrapped_fragment, contactDatePickFragment).commit();
    }
}
