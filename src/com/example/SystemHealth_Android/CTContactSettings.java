package com.example.SystemHealth_Android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by emou on 7/8/14.
 */
public class CTContactSettings extends Activity implements RadioGroup.OnCheckedChangeListener{
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_settings);

        initSettings();
    }

    private void initSettings(){
        String sortBy = getSharedPreferences("CTContactPreferences", Context.MODE_PRIVATE).getString("sortField","name");

        RadioButton rbDate = (RadioButton) findViewById(R.id.radioDate);
        RadioButton rbName = (RadioButton) findViewById(R.id.radioName);
        RadioButton rbStatus = (RadioButton) findViewById(R.id.radioStatus);

        if(sortBy.equalsIgnoreCase("dateCreated")){
            rbDate.setChecked(true);
        }else if(sortBy.equalsIgnoreCase("name")){
            rbName.setChecked(true);
        }else{
            rbStatus.setChecked(true);
        }
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch(checkedId){
            case R.id.radioDate://sort by number (decreasing from top)
                getSharedPreferences("CTContactPreferences",Context.MODE_PRIVATE).edit().putString("sortField","dateCreated").commit();
                getSharedPreferences("CTContactPreferences",Context.MODE_PRIVATE).edit().putString("sortOrder","DESC").commit();
                break;
            case R.id.radioName://set to alphabetical sort
                getSharedPreferences("CTContactPreferences",Context.MODE_PRIVATE).edit().putString("sortField","name").commit();
                getSharedPreferences("CTContactPreferences",Context.MODE_PRIVATE).edit().putString("sortOrder","ASC").commit();
                break;
            case R.id.radioStatus:
                getSharedPreferences("CTContactPreferences",Context.MODE_PRIVATE).edit().putString("sortField","status").commit();
                getSharedPreferences("CTContactPreferences",Context.MODE_PRIVATE).edit().putString("sortOrder","DESC").commit();
        }

    }
}
