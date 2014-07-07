package com.example.SystemHealth_Android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by emou on 6/26/14.
 */
public class CTSettingsActivity extends Activity implements RadioGroup.OnCheckedChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_settings);

        initSettings();
    }

    private void initSettings(){
        String sortBy = getSharedPreferences("ContactListPreferences", Context.MODE_PRIVATE).getString("sortField","count");
        String sortOrder = getSharedPreferences("ContactListPreferences",Context.MODE_PRIVATE).getString("sortOrder","DESC");

        RadioButton rbType = (RadioButton) findViewById(R.id.radioType);
        RadioButton rbAmount = (RadioButton) findViewById(R.id.radioAmount);
        if(sortBy.equalsIgnoreCase("description")){
            rbType.setChecked(true);
        }else{
            rbAmount.setChecked(true);
        }

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        radioGroup.setOnCheckedChangeListener(this);

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch(checkedId){
            case R.id.radioType://set to alphabetical sort
                getSharedPreferences("ContactListPreferences",Context.MODE_PRIVATE).edit().putString("sortField","description").commit();
                getSharedPreferences("ContactListPreferences",Context.MODE_PRIVATE).edit().putString("sortOrder","ASC").commit();
                break;
            case R.id.radioAmount://sort by number (decreasing from top)
                getSharedPreferences("ContactListPreferences",Context.MODE_PRIVATE).edit().putString("sortField","count").commit();
                getSharedPreferences("ContactListPreferences",Context.MODE_PRIVATE).edit().putString("sortOrder","DESC").commit();
                break;
        }

    }
}
