package com.example.SystemHealth_Android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Sets sharedPreferences for CTRequestPreferences for sortField and sortOrder fields on save.
 *
 * Created by emou on 6/26/14.
 */
public class RequestSettings extends Activity implements View.OnClickListener{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_settings);
        Button button = (Button) findViewById(R.id.save_button);
        button.setOnClickListener(this);

        //check appropriate radio button if loading from previous instance
        if(savedInstanceState!=null){
            int checkedId = savedInstanceState.getInt("checkedId");
            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
            radioGroup.check(checkedId);
        }else {
            //otherwise set button to match existing set preferences
            String sortBy = getSharedPreferences("CTRequestPreferences", Context.MODE_PRIVATE).getString("sortField", "count");

            RadioButton rbType = (RadioButton) findViewById(R.id.radioType);
            RadioButton rbAmount = (RadioButton) findViewById(R.id.radioAmount);
            if (sortBy.equalsIgnoreCase("description")) {
                rbType.setChecked(true);
            } else {
                rbAmount.setChecked(true);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        //save currently checked button's id for reference upon loading
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        int checkedId = radioGroup.getCheckedRadioButtonId();
        savedInstanceState.putInt("checkedId",checkedId);
    }

    @Override
    public void onClick(View v) {
        //save new settings to sharedPreferences and exit activity
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        int checkedId = radioGroup.getCheckedRadioButtonId();
        switch(checkedId){
            case R.id.radioType://set to alphabetical sort
                getSharedPreferences("CTRequestPreferences",Context.MODE_PRIVATE).edit().putString("sortField","description").commit();
                getSharedPreferences("CTRequestPreferences",Context.MODE_PRIVATE).edit().putString("sortOrder","ASC").commit();
                break;
            case R.id.radioAmount://sort by number (decreasing from top)
                getSharedPreferences("CTRequestPreferences",Context.MODE_PRIVATE).edit().putString("sortField","count").commit();
                getSharedPreferences("CTRequestPreferences",Context.MODE_PRIVATE).edit().putString("sortOrder","DESC").commit();
                break;
        }
        this.finish();
    }
}
