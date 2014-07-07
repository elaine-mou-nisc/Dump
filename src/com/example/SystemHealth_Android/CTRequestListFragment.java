package com.example.SystemHealth_Android;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by emou on 6/26/14.
 */
public class CTRequestListFragment extends Fragment{

    ArrayList<Request> requestArrayList = new ArrayList<Request>();
    View myFragmentView;
    long timeOfLastRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.contact_list_frag,container,false);
        setHasOptionsMenu(true);

        if(savedInstanceState==null) {
            initList();

            UpdateDataSourceTask updateDataSourceTask = new UpdateDataSourceTask();
            updateDataSourceTask.execute();

            timeOfLastRefresh = System.currentTimeMillis();

            ListView listView = (ListView) myFragmentView.findViewById(R.id.contact_list);
            listView.setAdapter(new ContactAdapter(getActivity(), R.layout.twoitem_list, requestArrayList));
        }

        TextView textView = (TextView) myFragmentView.findViewById(R.id.date_display);
        textView.setText("Start: " + getArguments().getString("date1") + "\n" +
                         "End: " + getArguments().getString("date2"));
        return myFragmentView;
    }

    public class UpdateDataSourceTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            CTRequestDataSource requestDataSource = new CTRequestDataSource(getActivity());

            requestDataSource.open();
            requestDataSource.clearRequestsDB();
            requestDataSource.addToRequestList(requestArrayList);
            requestDataSource.close();

            return null;
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        if(requestArrayList.isEmpty() && (System.currentTimeMillis() - timeOfLastRefresh) > 5000) {
            initList();

            UpdateDataSourceTask updateDataSourceTask = new UpdateDataSourceTask();
            updateDataSourceTask.execute();

            timeOfLastRefresh = System.currentTimeMillis();
        }

        String sortBy = getActivity().getSharedPreferences("ContactListPreferences", Context.MODE_PRIVATE).getString("sortField","description");
        String sortOrder = getActivity().getSharedPreferences("ContactListPreferences", Context.MODE_PRIVATE).getString("sortOrder", "ASC");
        CTRequestDataSource requestDataSource = new CTRequestDataSource(getActivity());

        requestDataSource.open();
        requestArrayList = requestDataSource.getContacts(sortBy,sortOrder);
        requestDataSource.close();

        ListView listView = (ListView) myFragmentView.findViewById(R.id.contact_list);
        listView.setAdapter(new ContactAdapter(getActivity(), R.layout.twoitem_list, requestArrayList));
    }

    private void initList(){

        JSONObject jsonObject = null;
        try {
            jsonObject = new BackgroundJsonTask(getActivity()).execute(R.raw.contact_detail).get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }

        if(jsonObject!=null) {
            try {
                requestArrayList = new SetContactsFromJsonTask().execute(jsonObject).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

    public class SetContactsFromJsonTask extends AsyncTask<JSONObject,Void,ArrayList<Request>>{

        @Override
        protected ArrayList<Request> doInBackground(JSONObject... params) {

            int resultsCount;
            JSONArray jsonArray;
            JSONObject jsonObject = params[0];
            ArrayList<Request> requestList = null;

            try {
                if(jsonObject.has("results")) {
                    jsonArray = jsonObject.getJSONArray("results");
                    requestList = new ArrayList<Request>();
                    resultsCount = jsonArray.length();

                    for (int i = 0; i < resultsCount; i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        Request request = new Request(jsonObject.getString("description"), jsonObject.getInt("count"));
                        requestList.add(request);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return requestList;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.contact_list_frag_actions,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_settings:
                timeOfLastRefresh -= 5000;//allows page to refresh upon changing settings/order

                Intent intent = new Intent(getActivity(),CTSettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_refresh:
                long currentTime = System.currentTimeMillis();
                if((currentTime - timeOfLastRefresh) > 5000) {

                    initList();

                    CTRequestDataSource requestDataSource = new CTRequestDataSource(getActivity());
                    String sortBy = getActivity().getSharedPreferences("ContactListPreferences", Context.MODE_PRIVATE).getString("sortField","description");
                    String sortOrder = getActivity().getSharedPreferences("ContactListPreferences", Context.MODE_PRIVATE).getString("sortOrder", "ASC");

                    requestDataSource.open();
                    requestDataSource.clearRequestsDB();
                    requestDataSource.addToRequestList(requestArrayList);

                    requestArrayList = requestDataSource.getContacts(sortBy,sortOrder);
                    requestDataSource.close();

                    ListView listView = (ListView) myFragmentView.findViewById(R.id.contact_list);
                    listView.setAdapter(new ContactAdapter(getActivity(), R.layout.twoitem_list, requestArrayList));

                    timeOfLastRefresh = currentTime;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class ContactAdapter extends ArrayAdapter<Request> {

        ArrayList<Request> requests;

        public ContactAdapter(Context context, int resource, ArrayList<Request> objects) {
            super(context, resource, objects);
            requests = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.twoitem_list, parent, false);
            }

            if(requests !=null) {
                TextView textView = (TextView) convertView.findViewById(R.id.text1);
                textView.setText(requests.get(position).mDescription);
                textView = (TextView) convertView.findViewById(R.id.text2);
                textView.setText("" + requests.get(position).mCount);
            }

            return convertView;
        }

        @Override
        public int getCount(){
            return requests.size();
        }
    }

}
