package com.example.SystemHealth_Android;

import android.content.Context;
import android.content.Intent;
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
 * Created by emou on 6/20/14.
 */
public class PieFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    ArrayList<PieChartSlice> sliceArrayList;
    ArrayList<Request> requestArrayList;
    int limit = 3;
    long timeOfLastDBCheck=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myFragmentView = inflater.inflate(R.layout.pie_fragment,container,false);

        PieChart pieChart = (PieChart) myFragmentView.findViewById(R.id.pie_chart);
        ListView listView = (ListView) myFragmentView.findViewById(R.id.type_list);

        if(savedInstanceState!=null) {
            sliceArrayList = savedInstanceState.getParcelableArrayList("sliceArrayList");
            requestArrayList = savedInstanceState.getParcelableArrayList("requestArrayList");
        }

        int size;

        if(System.currentTimeMillis() - timeOfLastDBCheck > 3000) {

            CTTileDataSource requestDataSource = new CTTileDataSource(getActivity());

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

            if(sliceArrayList==null) {
                sliceArrayList = new ArrayList<PieChartSlice>();
            }

            if(sliceArrayList.size()<size){
                while(sliceArrayList.size()<size){
                    sliceArrayList.add(new PieChartSlice(1));
                }
            }else if(sliceArrayList.size()>size){
                while(sliceArrayList.size()>size){
                    sliceArrayList.remove(sliceArrayList.size()-1);
                }
            }

            size = sliceArrayList.size();

            for(int i=0;i<size;i++){
                sliceArrayList.get(i).mValue = requestArrayList.get(i).mCount;
                sliceArrayList.get(i).mSubject = requestArrayList.get(i).mDescription;
            }
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

        savedInstanceState.putParcelableArrayList("requestArrayList", requestArrayList);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(getActivity(),CTDatePickerActivity.class);
        startActivity(intent);
    }
}
