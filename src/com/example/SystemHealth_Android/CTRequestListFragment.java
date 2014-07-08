package com.example.SystemHealth_Android;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by emou on 6/26/14.
 */
public class CTRequestListFragment extends Fragment implements AdapterView.OnItemClickListener{

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
            timeOfLastRefresh = System.currentTimeMillis();
        }

        ListView listView = (ListView) myFragmentView.findViewById(R.id.request_list);
        listView.setOnItemClickListener(this);
        TextView textView = (TextView) myFragmentView.findViewById(R.id.date_display);
        textView.setText("Start: " + getArguments().getString("date1") + "\n" +
                         "End: " + getArguments().getString("date2"));
        return myFragmentView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.v("OnItemClick", "OnItemClick");
        if(parent==myFragmentView.findViewById(R.id.request_list)){
            Log.v("OnItemClick","parent = request_list");
            if(getActivity().findViewById(R.id.right_fragment)!=null){
                Log.v("OnItemClick","right_fragment != null");
                CTContactListFragment contactListFragment = new CTContactListFragment();
                Bundle args = new Bundle();
                args.putInt("requestCode",requestArrayList.get(position).mCode);
                args.putString("requestDescription",requestArrayList.get(position).mDescription);
                contactListFragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.right_fragment,contactListFragment)
                        .addToBackStack(null).commit();
            }else {
                Log.v("OnItemClick","right_fragment = null");
                Intent intent = new Intent(getActivity(), CTContactListActivity.class);
                intent.putExtra("requestCode",requestArrayList.get(position).mCode);
                intent.putExtra("requestDescription",requestArrayList.get(position).mDescription);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        if(requestArrayList.isEmpty() && (System.currentTimeMillis() - timeOfLastRefresh) > 5000) {
            initList();
            timeOfLastRefresh = System.currentTimeMillis();
        }

        CTRequestDataSource requestDataSource = new CTRequestDataSource(getActivity());
        String sortBy = getActivity().getSharedPreferences("CTRequestPreferences", Context.MODE_PRIVATE).getString("sortField","description");
        String sortOrder = getActivity().getSharedPreferences("CTRequestPreferences", Context.MODE_PRIVATE).getString("sortOrder", "ASC");

        requestDataSource.open();
        requestArrayList = requestDataSource.getRequests(sortBy, sortOrder);
        requestDataSource.close();

        ListView listView = (ListView) myFragmentView.findViewById(R.id.request_list);
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

            JSONObject jsonObject = params[0];
            ArrayList<Request> requestList = null;
            ArrayList<Contact> contactList = null;

            try {
                if(jsonObject.has("results")) {
                    JSONArray jsonRequestsArray = jsonObject.getJSONArray("results");
                    requestList = new ArrayList<Request>();
                    contactList = new ArrayList<Contact>();
                    int resultsCount = jsonRequestsArray.length();

                    for (int i = 0; i < resultsCount; i++) {
                        jsonObject = jsonRequestsArray.getJSONObject(i);
                        Request request = new Request(jsonObject.getString("description"), jsonObject.getInt("count"));
                        request.mCode = jsonObject.getInt("code");
                        requestList.add(request);

                        JSONArray jsonContactsArray = jsonObject.getJSONArray("contacts");
                        int contactsSize = jsonContactsArray.length();
                        for(int j=0;j<contactsSize;j++){
                            JSONObject jsonContact = jsonContactsArray.getJSONObject(j);
                            if(jsonContact.has("name")) {
                                Contact contact = new Contact(jsonContact.getString("name"));
                                contact.mContactId = jsonContact.getInt("contactId");
                                contact.mDateCreated = jsonContact.getLong("dateCreated");
                                contact.mStatus = jsonContact.getString("status");
                                contact.mRequestCode = request.mCode;
                                contactList.add(contact);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            CTContactDataSource contactDataSource = new CTContactDataSource(getActivity());
            contactDataSource.open();
            contactDataSource.clearRequestsDB();
            contactDataSource.addToContactList(contactList);
            contactDataSource.close();

            return requestList;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.ct_requests_actions,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_settings:
                timeOfLastRefresh -= 5000;//allows page to refresh upon changing settings/order

                Intent intent = new Intent(getActivity(),CTRequestSettings.class);
                startActivity(intent);
                return true;
            case R.id.action_refresh:
                long currentTime = System.currentTimeMillis();
                if((currentTime - timeOfLastRefresh) > 5000) {

                    initList();

                    String sortBy = getActivity().getSharedPreferences("CTRequestPreferences", Context.MODE_PRIVATE).getString("sortField","description");
                    String sortOrder = getActivity().getSharedPreferences("CTRequestPreferences", Context.MODE_PRIVATE).getString("sortOrder", "ASC");

                    CTRequestDataSource requestDataSource = new CTRequestDataSource(getActivity());
                    requestDataSource.open();
                    requestArrayList = requestDataSource.getRequests(sortBy,sortOrder);
                    requestDataSource.close();

                    ListView listView = (ListView) myFragmentView.findViewById(R.id.request_list);
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
