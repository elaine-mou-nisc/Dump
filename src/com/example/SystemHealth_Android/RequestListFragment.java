package com.example.SystemHealth_Android;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

/**
 * Created by emou on 6/26/14.
 */
public class RequestListFragment extends Fragment implements AdapterView.OnItemClickListener{

    ArrayList<Request> requestArrayList = new ArrayList<Request>();
    View myFragmentView;
    long timeOfLastRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.requestlist_fragment,container,false);
        setHasOptionsMenu(true);

        if(savedInstanceState==null) {
            initList();
            refreshList();
            TextView textView = (TextView) myFragmentView.findViewById(R.id.date_display);
            textView.setText("Start: " + getActivity().getSharedPreferences("CTDatePreferences",Context.MODE_PRIVATE).getString("startDate","----") + "\n" +
                    "End: " + getActivity().getSharedPreferences("CTDatePreferences", Context.MODE_PRIVATE).getString("endDate","----"));
            timeOfLastRefresh = System.currentTimeMillis();
        }

        ListView listView = (ListView) myFragmentView.findViewById(R.id.request_list);
        if(listView!=null){
            listView.setOnItemClickListener(this);
        }
        return myFragmentView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent==myFragmentView.findViewById(R.id.request_list)){
            if(getActivity().findViewById(R.id.middle_fragment)!=null){
                ContactListFragment contactListFragment = new ContactListFragment();
                Bundle args = new Bundle();
                args.putInt("requestCode",requestArrayList.get(position).mCode);
                args.putString("requestDescription",requestArrayList.get(position).mDescription);
                contactListFragment.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.middle_fragment,contactListFragment)
                        .commit();
            }else {
                Intent intent = new Intent(getActivity(), ContactListActivity.class);
                intent.putExtra("requestCode",requestArrayList.get(position).mCode);
                intent.putExtra("requestDescription",requestArrayList.get(position).mDescription);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        if((System.currentTimeMillis() - timeOfLastRefresh) > 5000) {
            initList();
            refreshList();
            timeOfLastRefresh = System.currentTimeMillis();

            TextView textView = (TextView) myFragmentView.findViewById(R.id.date_display);
            textView.setText("Start: " + getActivity().getSharedPreferences("CTDatePreferences",Context.MODE_PRIVATE).getString("startDate","----") + "\n" +
                    "End: " + getActivity().getSharedPreferences("CTDatePreferences", Context.MODE_PRIVATE).getString("endDate","----"));
        }
    }

    private void initList(){

        JSONObject jsonObject = null;
        try {
            jsonObject = new DateOptions.BackgroundJsonTask(getActivity()).execute(R.raw.contact_detail).get();
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

    private void refreshList(){
        RequestDataSource requestDataSource = new RequestDataSource(getActivity());
        String sortBy = getActivity().getSharedPreferences("CTRequestPreferences", Context.MODE_PRIVATE).getString("sortField","description");
        String sortOrder = getActivity().getSharedPreferences("CTRequestPreferences", Context.MODE_PRIVATE).getString("sortOrder", "ASC");

        requestDataSource.open();
        requestArrayList = requestDataSource.getRequests(sortBy, sortOrder);
        requestDataSource.close();

        if(myFragmentView.findViewById(R.id.request_expandable)!=null) {//single pane, using expandable list
            String contactSortBy = getActivity().getSharedPreferences("CTContactPreferences", Context.MODE_PRIVATE).getString("sortField", "dateCreated");
            String contactSortOrder = getActivity().getSharedPreferences("CTContactPreferences", Context.MODE_PRIVATE).getString("sortOrder", "DESC");

            HashMap<String, ArrayList<Contact>> contactMap = new HashMap<String, ArrayList<Contact>>();

            ContactDataSource contactDataSource = new ContactDataSource(getActivity());
            contactDataSource.open();
            for (int i = 0; i < requestArrayList.size(); i++) {
                int code = requestArrayList.get(i).mCode;
                ArrayList<Contact> contactArrayList = contactDataSource.getContactsByRequestCode(code, contactSortBy, contactSortOrder);
                contactMap.put(requestArrayList.get(i).mDescription, contactArrayList);
            }
            contactDataSource.close();

            MyExpandableListAdapter adapter = new MyExpandableListAdapter(requestArrayList, contactMap);
            ExpandableListView expandableView = (ExpandableListView) myFragmentView.findViewById(R.id.request_expandable);
            expandableView.setAdapter(adapter);
        }else if(myFragmentView.findViewById(R.id.request_list)!=null){
            ListView listView = (ListView) myFragmentView.findViewById(R.id.request_list);
            listView.setAdapter(new RequestAdapter(getActivity(),R.layout.twotext_listitem,requestArrayList));
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

            RequestDataSource requestDataSource = new RequestDataSource(getActivity());
            requestDataSource.open();
            requestDataSource.clearRequestsDB();
            requestDataSource.addToRequestList(requestList);
            requestDataSource.close();

            ContactDataSource contactDataSource = new ContactDataSource(getActivity());
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
        if(getActivity().findViewById(R.id.right_fragment)==null){
            inflater.inflate(R.menu.ct_date_actions,menu);
        }else {
            inflater.inflate(R.menu.ct_requests_actions, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_edit_date:
                timeOfLastRefresh -= 5000;

                Intent intent = new Intent(getActivity(),DatePickerActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                timeOfLastRefresh -= 5000;//allows page to refresh upon changing settings/order

                intent = new Intent(getActivity(),RequestSettings.class);
                startActivity(intent);
                return true;
            case R.id.action_refresh:
                long currentTime = System.currentTimeMillis();
                if((currentTime - timeOfLastRefresh) > 5000) {
                    initList();
                    refreshList();
                    timeOfLastRefresh = currentTime;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class RequestAdapter extends ArrayAdapter<Request> {

        ArrayList<Request> requests;

        public RequestAdapter(Context context, int resource, ArrayList<Request> objects) {
            super(context, resource, objects);
            requests = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.twotext_listitem, parent, false);
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

    private class MyExpandableListAdapter extends BaseExpandableListAdapter{
        ArrayList<Request> requestsList;
        HashMap<String,ArrayList<Contact>> contactList;
        public MyExpandableListAdapter(ArrayList<Request> requests,HashMap<String,ArrayList<Contact>> contacts){
            super();
            requestsList = requests;
            contactList = contacts;
        }

        @Override
        public int getGroupCount() {
            return requestsList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return contactList.get(requestsList.get(groupPosition).mDescription).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return contactList.get(requestsList.get(groupPosition).mDescription);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return contactList.get(requestsList.get(groupPosition).mDescription).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView==null){
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.twotext_listitem,parent,false);
            }

            if(requestsList!=null){
                Request request = requestsList.get(groupPosition);

                TextView textView = (TextView) convertView.findViewById(R.id.text1);
                textView.setText(request.mDescription);
                textView = (TextView) convertView.findViewById(R.id.text2);
                textView.setText("" + request.mCount);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if(convertView==null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.contact_listitem, parent, false);
            }

            if(contactList !=null) {
                Contact contact = contactList.get(requestsList.get(groupPosition).mDescription).get(childPosition);

                TextView textView = (TextView) convertView.findViewById(R.id.text1);
                textView.setText(contact.mName);
                String text=null;
                if(contact.mStatus.equalsIgnoreCase("O")){
                    text = "Open";
                }else if(contact.mStatus.equalsIgnoreCase("C")){
                    text = "Closed";
                }
                textView = (TextView) convertView.findViewById(R.id.text2);
                textView.setText(text);
                Date date = new Date(contact.mDateCreated*1000);
                DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                format.setTimeZone(TimeZone.getTimeZone("US/Central"));
                text = format.format(date);
                textView = (TextView) convertView.findViewById(R.id.text3);
                textView.setText("Created: " + text);
                textView = (TextView) convertView.findViewById(R.id.text4);
                textView.setText("ID: " + contact.mContactId);
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
