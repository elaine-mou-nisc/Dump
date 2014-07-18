package com.example.SystemHealth_Android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Activity to set preferences for order for Contacts
 * Sets sharedPreferences for CTContactPreferences with sortField and sortOrder
 *
 * Created by emou on 7/8/14.
 */
public class ContactSettings extends Activity implements View.OnClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_settings);
        Button button = (Button) findViewById(R.id.save_button);
        button.setOnClickListener(this);

        if(savedInstanceState!=null){
            //check appropriate radio button if reloading view
            int checkedId = savedInstanceState.getInt("checkedId");
            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
            radioGroup.check(checkedId);
        }else {
            //if starting for the first time, display buttons according to set preferences
            String sortBy = getSharedPreferences("CTContactPreferences", Context.MODE_PRIVATE).getString("sortField", "name");

            RadioButton rbDate = (RadioButton) findViewById(R.id.radioDate);
            RadioButton rbName = (RadioButton) findViewById(R.id.radioName);
            RadioButton rbStatus = (RadioButton) findViewById(R.id.radioStatus);

            if (sortBy.equalsIgnoreCase("dateCreated")) {
                rbDate.setChecked(true);
            } else if (sortBy.equalsIgnoreCase("name")) {
                rbName.setChecked(true);
            } else {
                rbStatus.setChecked(true);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        //save currently checked button in case of rotation, etc.
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        int checkedId = radioGroup.getCheckedRadioButtonId();
        savedInstanceState.putInt("checkedId",checkedId);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.save_button) {
            //on save button click, set shared preferences and exit activity
            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
            int checkedId = radioGroup.getCheckedRadioButtonId();
            switch (checkedId) {
                case R.id.radioDate://sort by number (decreasing from top)
                    getSharedPreferences("CTContactPreferences", Context.MODE_PRIVATE).edit().putString("sortField", "dateCreated").commit();
                    getSharedPreferences("CTContactPreferences", Context.MODE_PRIVATE).edit().putString("sortOrder", "DESC").commit();
                    break;
                case R.id.radioName://set to alphabetical sort
                    getSharedPreferences("CTContactPreferences", Context.MODE_PRIVATE).edit().putString("sortField", "name").commit();
                    getSharedPreferences("CTContactPreferences", Context.MODE_PRIVATE).edit().putString("sortOrder", "ASC").commit();
                    break;
                case R.id.radioStatus:
                    getSharedPreferences("CTContactPreferences", Context.MODE_PRIVATE).edit().putString("sortField", "status").commit();
                    getSharedPreferences("CTContactPreferences", Context.MODE_PRIVATE).edit().putString("sortOrder", "DESC").commit();
            }
            this.finish();
        }
    }
}
