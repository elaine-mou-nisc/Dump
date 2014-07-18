package com.example.SystemHealth_Android;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Tile fragment to be clicked on to lead into ContactTracking
 * Displays top request types and associated pie chart on front.
 *
 * Created by emou on 6/20/14.
 */
public class ContactTrackingTile extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    ArrayList<PieChartSlice> sliceArrayList;
    ArrayList<Request> requestArrayList;
    int limit = 3;
    long timeOfLastDBCheck=0;
    int[] colors;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Resources res = getActivity().getResources();

        colors = new int[] {res.getColor(R.color.light_red),res.getColor(R.color.light_orange),
                res.getColor(R.color.dark_gold),res.getColor(R.color.dark_green),
                res.getColor(R.color.light_blue),res.getColor(R.color.dark_blue),
                res.getColor(R.color.light_purple),res.getColor(R.color.gray)};

        View myFragmentView = inflater.inflate(R.layout.pie_tile,container,false);

        PieChart pieChart = (PieChart) myFragmentView.findViewById(R.id.pie_chart);
        ListView listView = (ListView) myFragmentView.findViewById(R.id.type_list);

        if(savedInstanceState!=null) {
            sliceArrayList = savedInstanceState.getParcelableArrayList("sliceArrayList");
            requestArrayList = savedInstanceState.getParcelableArrayList("requestArrayList");
        }

        int size;

        //only make update if enough time has elapsed since last refresh, not on any rotation
        if(System.currentTimeMillis() - timeOfLastDBCheck > 3000) {

            TileDataSource requestDataSource = new TileDataSource(getActivity());

            JSONObject jsonObject=null;
            try {
                jsonObject = new BackgroundJsonTask(getActivity()).execute(R.raw.contact_tile).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if(jsonObject!=null && jsonObject.has("results")) {
                try {
                    JSONArray resultsArray = jsonObject.getJSONArray("results");
                    int resultsLength = resultsArray.length();
                    //reset database of Requests based off of JSON
                    requestDataSource.open();
                    requestDataSource.clearRequestsDB();
                    for(int i=0;i<resultsLength;i++){
                        jsonObject = resultsArray.getJSONObject(i);
                        Request request=null;
                        if(jsonObject.has("description")){
                            request = new Request(jsonObject.getString("description"));
                            if(jsonObject.has("count")){
                                request.mCount = jsonObject.getInt("count");
                            }
                        }
                        if(request!=null) {
                            requestDataSource.addRequest(request);
                        }
                    }
                    requestDataSource.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            //retrieve top requests and sum the amount of other requests
            int othersCount=0;
            try {
                requestDataSource.open();
                requestArrayList = requestDataSource.getTopRequests(limit);
                othersCount = requestDataSource.getOthersCount(limit);
                requestDataSource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            timeOfLastDBCheck = System.currentTimeMillis();
            requestArrayList.add(new Request("Other",othersCount));

            size = requestArrayList.size();

            sliceArrayList = new ArrayList<PieChartSlice>(size);
            //fill sliceArrayList with values of top requests and Other
            for(int i=0;i<size;i++){
                sliceArrayList.add(new PieChartSlice(requestArrayList.get(i).mCount,colors[i%8]));
                sliceArrayList.get(i).mSubject = requestArrayList.get(i).mDescription;
            }
        }
        //draw pieChart based on slices list
        pieChart.setSliceArrayList(sliceArrayList);
        //build array of text associated with slices
        ArrayList<String> textArray = new ArrayList<String>();
        for(int i=0;i<sliceArrayList.size();i++){
            textArray.add((int)sliceArrayList.get(i).mValue + " " + sliceArrayList.get(i).mSubject);
        }
        //display text with colors corresponding to pie chart
        ArrayAdapter adapter = new ColorTextAdapter(getActivity(),R.layout.small_list_textview,textArray,colors);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        TextView textView = (TextView) myFragmentView.findViewById(R.id.timestamp);
        textView.setText(MyActivity.updateTime);

        LinearLayout linearLayout = (LinearLayout) myFragmentView.findViewById(R.id.pie_fragment_layout);
        linearLayout.setOnClickListener(this);

        return myFragmentView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onClick(view);
    }
    //adapter displays colored text given strings and colors for each text item
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
            textView.setTextColor(colorsArray[position%8]);
            return convertView;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        //save current lists of data, slices
        savedInstanceState.putParcelableArrayList("sliceArrayList",sliceArrayList);
        savedInstanceState.putParcelableArrayList("requestArrayList", requestArrayList);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        //open Contact Tracking activity
        Intent intent = new Intent(getActivity(),TrackingActivity.class);
        startActivity(intent);
    }
}
