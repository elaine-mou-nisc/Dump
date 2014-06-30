package com.example.SystemHealth_Android;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
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
public class ContactListFragment extends Fragment implements AdapterView.OnItemClickListener{

    ArrayList<Contact> contactArrayList = new ArrayList<Contact>();
    View myFragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.contact_list_frag,container,false);
        setHasOptionsMenu(true);

        ContactDataSource contactDataSource = new ContactDataSource(getActivity());
        if(savedInstanceState==null) {
            initList();

            try {
                contactDataSource.open();
                contactDataSource.clearContactsDB();
                contactDataSource.addToContactList(contactArrayList);
                contactDataSource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            ListView listView = (ListView) myFragmentView.findViewById(R.id.contact_list);
            listView.setAdapter(new ContactAdapter(getActivity(), R.layout.twoitem_list, contactArrayList));
        }

        return myFragmentView;
    }

    @Override
    public void onResume(){
        super.onResume();
        ContactDataSource contactDataSource = new ContactDataSource(getActivity());

        String sortBy = getActivity().getSharedPreferences("ContactListPreferences", Context.MODE_PRIVATE).getString("sortField","contactType");
        String sortOrder = getActivity().getSharedPreferences("ContactListPreferences",Context.MODE_PRIVATE).getString("sortOrder","ASC");
        try {
            contactDataSource.open();
            contactArrayList = contactDataSource.getContacts(sortBy,sortOrder);
            contactDataSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(contactArrayList.isEmpty()) {
            initList();

            try {
                contactDataSource.open();
                contactDataSource.clearContactsDB();
                contactDataSource.addToContactList(contactArrayList);
                contactDataSource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        ListView listView = (ListView) myFragmentView.findViewById(R.id.contact_list);
        listView.setAdapter(new ContactAdapter(getActivity(), R.layout.twoitem_list, contactArrayList));
    }

    private void initList(){

        int resultsCount;
        JSONArray jsonArray;
        BackgroundJsonTask jsonTask = new BackgroundJsonTask(getActivity());
        try {
            JSONObject jsonObject = jsonTask.execute(R.raw.contacts).get();

            if( (resultsCount = jsonObject.getInt("resultsCount")) >0){
                jsonArray = jsonObject.getJSONArray("results");
                contactArrayList = new ArrayList<Contact>();

                for(int i=0;i<resultsCount;i++){
                    jsonObject = jsonArray.getJSONObject(i);
                    Contact contact = new Contact(jsonObject.getString("contactType"),jsonObject.getInt("amount"));
                    contactArrayList.add(contact);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
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
                Intent intent = new Intent(getActivity(),ContactSettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private class ContactAdapter extends ArrayAdapter<Contact> {

        ArrayList<Contact> contacts;

        public ContactAdapter(Context context, int resource, ArrayList<Contact> objects) {
            super(context, resource, objects);
            contacts = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.twoitem_list, parent, false);
            }

            if(contacts!=null) {
                TextView textView = (TextView) convertView.findViewById(R.id.text1);
                textView.setText(contacts.get(position).mName);
                textView = (TextView) convertView.findViewById(R.id.text2);
                textView.setText("" + contacts.get(position).mAmount);
            }

            return convertView;
        }
    }

}
