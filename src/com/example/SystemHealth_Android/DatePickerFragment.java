package com.example.SystemHealth_Android;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by emou on 7/2/14.
 */
public class DatePickerFragment extends Fragment implements View.OnClickListener, TextWatcher, AdapterView.OnItemSelectedListener {

    View myFragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myFragmentView = inflater.inflate(R.layout.datepicker_fragment,container,false);
        Button button = (Button) myFragmentView.findViewById(R.id.search_button);
        button.setOnClickListener(this);
        if(savedInstanceState!=null){
            button.setEnabled(savedInstanceState.getBoolean("searchEnabled"));
        }
        button = (Button) myFragmentView.findViewById(R.id.start_date_button);
        button.setOnClickListener(this);
        button.setEnabled(false);
        if(savedInstanceState!=null){
            button.setEnabled(savedInstanceState.getBoolean("setStartEnabled"));
        }
        button = (Button) myFragmentView.findViewById(R.id.end_date_button);
        button.setOnClickListener(this);
        button.setEnabled(false);
        if(savedInstanceState!=null){
            button.setEnabled(savedInstanceState.getBoolean("setEndEnabled"));
        }

        EditText editText = (EditText) myFragmentView.findViewById(R.id.start_date_edit);
        editText.setEnabled(false);
        if(savedInstanceState!=null){
            editText.setText(savedInstanceState.getString("startDate"));
        }
        editText.addTextChangedListener(this);
        editText = (EditText) myFragmentView.findViewById(R.id.end_date_edit);
        editText.setEnabled(false);
        if(savedInstanceState!=null){
            editText.setText(savedInstanceState.getString("endDate"));
        }
        editText.addTextChangedListener(this);

        DateOptions[] dateOptions = DateOptions.values();
        ArrayList<String> strings = new ArrayList<String>(dateOptions.length);
        for(int i=0;i<dateOptions.length;i++){
            strings.add(dateOptions[i].toString());
        }

        Spinner spinner = (Spinner) myFragmentView.findViewById(R.id.date_spinner);
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,
                strings);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
        if(savedInstanceState!=null){
            spinner.setSelection(savedInstanceState.getInt("spinnerPosition"));
        }

        return myFragmentView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString("startDate",((EditText)getView().findViewById(R.id.start_date_edit)).getText().toString());
        savedInstanceState.putString("endDate",((EditText)getView().findViewById(R.id.end_date_edit)).getText().toString());
        savedInstanceState.putInt("spinnerPosition",((Spinner)getView().findViewById(R.id.date_spinner)).getSelectedItemPosition());
        savedInstanceState.putBoolean("setStartEnabled",getView().findViewById(R.id.start_date_edit).isEnabled());
        savedInstanceState.putBoolean("setEndEnabled",getView().findViewById(R.id.end_date_edit).isEnabled());
        savedInstanceState.putBoolean("searchEnabled",getView().findViewById(R.id.search_button).isEnabled());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id){
            case R.id.start_date_button:
                Calendar c1 = Calendar.getInstance();
                int year = c1.get(Calendar.YEAR);
                int month = c1.get(Calendar.MONTH);
                int day = c1.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd1 = new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        EditText editText = (EditText) getView().findViewById(R.id.start_date_edit);
                        editText.setText((monthOfYear+1) + "/" + dayOfMonth + "/" + year);
                    }
                },year,month,day);
                dpd1.getDatePicker().setSpinnersShown(false);
                dpd1.getDatePicker().setCalendarViewShown(true);
                dpd1.show();
                break;
            case R.id.end_date_button:
                Calendar c2 = Calendar.getInstance();
                int y = c2.get(Calendar.YEAR);
                int m = c2.get(Calendar.MONTH);
                int d = c2.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd2 = new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        EditText editText = (EditText) getView().findViewById(R.id.end_date_edit);
                        editText.setText((monthOfYear+1) + "/" + dayOfMonth + "/" + year);
                    }
                },y,m,d);
                dpd2.getDatePicker().setSpinnersShown(false);
                dpd2.getDatePicker().setCalendarViewShown(true);
                dpd2.show();
                break;
            case R.id.search_button:
                String date1 = null;
                String date2 = null;

                String choice = (String) ((Spinner) getView().findViewById(R.id.date_spinner)).getSelectedItem();

                if(choice.equals(DateOptions.CUSTOM.getDescription())){
                    EditText editText = (EditText) getView().findViewById(R.id.start_date_edit);
                    date1 = editText.getText().toString();
                    editText = (EditText) getView().findViewById(R.id.end_date_edit);
                    date2 = editText.getText().toString();
                }else{
                    Time now = new Time(Time.getCurrentTimezone());
                    now.setToNow();
                    date2 = (now.month+1) + "/" + now.monthDay + "/" + now.year;

                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(System.currentTimeMillis());
                    if(choice.equals(DateOptions.DAY.getDescription())){
                        c.add(Calendar.DATE,-1);
                    }else if(choice.equals(DateOptions.WEEK.getDescription())){
                        c.add(Calendar.DATE,-7);
                    }else if(choice.equals(DateOptions.THIRTY_DAYS.getDescription())){
                        c.add(Calendar.DATE,-30);
                    }else if(choice.equals(DateOptions.SIXTY_DAYS.getDescription())){
                        c.add(Calendar.DATE,-60);
                    }else if(choice.equals(DateOptions.NINETY_DAYS.getDescription())){
                        c.add(Calendar.DATE,-90);
                    }else if(choice.equals(DateOptions.YEAR.getDescription())){
                        c.add(Calendar.DATE,-365);
                    }

                    date1 = (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH) + "/"
                            + c.get(Calendar.YEAR);
                }

                FrameLayout frameLayout = (FrameLayout) getActivity().findViewById(R.id.left_fragment);
                getActivity().getSharedPreferences("CTDatePreferences", Context.MODE_PRIVATE).edit().
                        putString("startDate", date1).commit();
                getActivity().getSharedPreferences("CTDatePreferences",Context.MODE_PRIVATE).edit().
                        putString("endDate", date2).commit();
                if(frameLayout!=null) {//in 3-pane view
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    RequestListFragment requestFragment = (RequestListFragment) fragmentManager.
                            findFragmentById(R.id.left_fragment);
                    requestFragment.timeOfLastRefresh -=5000;
                    requestFragment.onResume();
                    Fragment fragment = fragmentManager.findFragmentById(R.id.middle_fragment);
                    fragmentManager.beginTransaction().remove(fragment).commit();
                }else{//single pane layout
                    getActivity().finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
    @Override
    public void afterTextChanged(Editable s) {
        EditText editText1 = (EditText) getView().findViewById(R.id.start_date_edit);
        EditText editText2 = (EditText) getView().findViewById(R.id.end_date_edit);

        boolean startIsBeforeEnd = false;
        if(editText1.getText().toString().length() > 0 && editText2.getText().toString().length()>0) {
            String[] date1 = editText1.getText().toString().split("/");
            String[] date2 = editText2.getText().toString().split("/");
            if (Integer.parseInt(date1[2]) > Integer.parseInt(date2[2])) {
                startIsBeforeEnd = false;
            } else if (Integer.parseInt(date1[2]) < Integer.parseInt(date2[2])) {
                startIsBeforeEnd = true;
            } else if (Integer.parseInt(date1[0]) > Integer.parseInt(date2[0])) {
                startIsBeforeEnd = false;
            } else if (Integer.parseInt(date1[0]) < Integer.parseInt(date2[0])) {
                startIsBeforeEnd = true;
            } else if (Integer.parseInt(date1[1]) > Integer.parseInt(date2[1])) {
                startIsBeforeEnd = false;
            } else {
                startIsBeforeEnd = true;
            }
        }

        Button button = (Button) getView().findViewById(R.id.search_button);
        if(!startIsBeforeEnd) {
            button.setEnabled(false);
        }else{
            button.setEnabled(true);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent==getView().findViewById(R.id.date_spinner)){
            if(parent.getItemAtPosition(position).equals(DateOptions.CUSTOM.getDescription())){
                Button button = (Button) getView().findViewById(R.id.start_date_button);
                button.setEnabled(true);
                button = (Button) getView().findViewById(R.id.end_date_button);
                button.setEnabled(true);

                EditText editText1 = (EditText) getView().findViewById(R.id.start_date_edit);
                EditText editText2 = (EditText) getView().findViewById(R.id.end_date_edit);

                boolean startIsBeforeEnd = false;

                if(editText1.getText().toString().length() > 0 && editText2.getText().toString().length()>0) {
                    String[] date1 = editText1.getText().toString().split("/");
                    String[] date2 = editText2.getText().toString().split("/");
                    if (Integer.parseInt(date1[2]) > Integer.parseInt(date2[2])) {
                        startIsBeforeEnd = false;
                    } else if (Integer.parseInt(date1[2]) < Integer.parseInt(date2[2])) {
                        startIsBeforeEnd = true;
                    } else if (Integer.parseInt(date1[0]) > Integer.parseInt(date2[0])) {
                        startIsBeforeEnd = false;
                    } else if (Integer.parseInt(date1[0]) < Integer.parseInt(date2[0])) {
                        startIsBeforeEnd = true;
                    } else if (Integer.parseInt(date1[1]) > Integer.parseInt(date2[1])) {
                        startIsBeforeEnd = false;
                    } else {
                        startIsBeforeEnd = true;
                    }
                }

                if(!startIsBeforeEnd) {
                    button = (Button) getView().findViewById(R.id.search_button);
                    button.setEnabled(false);
                }
            }else{
                Button button = (Button) getView().findViewById(R.id.start_date_button);
                button.setEnabled(false);
                button = (Button) getView().findViewById(R.id.end_date_button);
                button.setEnabled(false);
                button = (Button) getView().findViewById(R.id.search_button);
                button.setEnabled(true);
            }
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
