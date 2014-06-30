package com.example.SystemHealth_Android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.w3c.dom.Text;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by emou on 6/20/14.
 */
public class PieFragment extends Fragment implements View.OnClickListener {

    ArrayList<PieChartSlice> sliceArrayList;
    ArrayList<Contact> contactArrayList;
    int limit = 4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myFragmentView = inflater.inflate(R.layout.pie_fragment,container,false);

        PieChart pieChart = (PieChart) myFragmentView.findViewById(R.id.pie_chart);
        ListView listView = (ListView) myFragmentView.findViewById(R.id.type_list);

        if(savedInstanceState!=null) {
            sliceArrayList = savedInstanceState.getParcelableArrayList("sliceArrayList");
            contactArrayList = savedInstanceState.getParcelableArrayList("contactArrayList");
        }

        ContactDataSource contactDataSource = new ContactDataSource(getActivity());
        try {
            contactDataSource.open();
            contactArrayList = contactDataSource.getTopContacts(limit);
            contactDataSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int size = contactArrayList.size();

        if(sliceArrayList==null) {
            sliceArrayList = new ArrayList<PieChartSlice>();

            for(int i=0;i<size;i++){
                sliceArrayList.add(new PieChartSlice(1));
            }
        }

        size = sliceArrayList.size();

        for(int i=0;i<size;i++){
            sliceArrayList.get(i).mValue = contactArrayList.get(i).mAmount;
            sliceArrayList.get(i).mSubject = contactArrayList.get(i).mName;
        }

        pieChart.setSliceArrayList(sliceArrayList);

        ArrayList<String> textArray = new ArrayList<String>();

        for(int i=0;i<sliceArrayList.size();i++){
            textArray.add((int)sliceArrayList.get(i).mValue + " " + sliceArrayList.get(i).mSubject);
        }

        int[] colors = new int[sliceArrayList.size()];
        for(int i=0;i<colors.length;i++){
            colors[i] = sliceArrayList.get(i).mColor;
        }

        ArrayAdapter adapter = new ColorTextAdapter(getActivity(),R.layout.small_list_textview,textArray,colors);
        listView.setAdapter(adapter);

        TextView textView = (TextView) myFragmentView.findViewById(R.id.timestamp);
        textView.setText(MyActivity.updateTime);

        LinearLayout linearLayout = (LinearLayout) myFragmentView.findViewById(R.id.pie_fragment_layout);
        linearLayout.setOnClickListener(this);

        return myFragmentView;
    }

    private class ColorTextAdapter extends ArrayAdapter {

        int[] colorsArray;
        ArrayList<String> stringArrayList;
        String[] stringArray;

        public ColorTextAdapter(Context context, int resource, ArrayList<String> objects) {
            super(context, resource, objects);
            stringArrayList = objects;
        }
        public ColorTextAdapter(Context context, int resource, String[] objects){
            super(context,resource,objects);
            stringArray = objects;
        }
        public ColorTextAdapter(Context context, int resource, ArrayList<String> objects,int[] colors) {
            super(context, resource, objects);
            stringArrayList = objects;
            colorsArray = colors;
        }
        public ColorTextAdapter(Context context, int resource, String[] objects, int[] colors){
            super(context,resource,objects);
            stringArray = objects;
            colorsArray = colors;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.small_list_textview, parent, false);
            TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
            if(stringArrayList!=null) {
                textView.setText(stringArrayList.get(position));
            }else if(stringArray!=null){
                textView.setText(stringArray[position]);
            }
            textView.setTextColor(colorsArray[position]);
            return convertView;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putParcelableArrayList("sliceArrayList",sliceArrayList);

        savedInstanceState.putParcelableArrayList("contactArrayList",contactArrayList);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(getActivity(),ContactTrackingActivity.class);
        startActivity(intent);
    }
}
