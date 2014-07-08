package com.example.SystemHealth_Android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by emou on 7/8/14.
 */
public class CTContactListFragment extends Fragment {

    ArrayList<Contact> contactArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.contact_list_frag,container,false);
        setHasOptionsMenu(true);

        TextView textView = (TextView) myFragmentView.findViewById(R.id.date_display);
        textView.setText("Request: " + getArguments().getString("requestDescription"));
        return myFragmentView;
    }

    @Override
    public void onResume(){
        super.onResume();

        String sortBy = getActivity().getSharedPreferences("CTContactPreferences", Context.MODE_PRIVATE).getString("sortField","name");
        String sortOrder = getActivity().getSharedPreferences("CTContactPreferences", Context.MODE_PRIVATE).getString("sortOrder", "ASC");

        CTContactDataSource contactDataSource = new CTContactDataSource(getActivity());
        contactDataSource.open();
        contactArrayList = contactDataSource.getContactsByRequestCode(getArguments().getInt("requestCode"),sortBy, sortOrder);
        contactDataSource.close();

        ListView listView = (ListView) getView().findViewById(R.id.request_list);
        listView.setAdapter(new ContactAdapter(getActivity(), R.layout.contact_listitem, contactArrayList));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.ct_contacts_actions,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_order_settings:
                Intent intent = new Intent(getActivity(),CTContactSettings.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                convertView = inflater.inflate(R.layout.contact_listitem, parent, false);
            }

            if(contacts !=null) {
                TextView textView = (TextView) convertView.findViewById(R.id.text1);
                textView.setText(contacts.get(position).mName);
                String text=null;
                if(contacts.get(position).mStatus.equalsIgnoreCase("O")){
                    text = "Open";
                }else if(contacts.get(position).mStatus.equalsIgnoreCase("C")){
                    text = "Closed";
                }
                textView = (TextView) convertView.findViewById(R.id.text2);
                textView.setText(text);
                Date date = new Date(contacts.get(position).mDateCreated*1000);
                DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                format.setTimeZone(TimeZone.getTimeZone("US/Central"));
                text = format.format(date);
                textView = (TextView) convertView.findViewById(R.id.text3);
                textView.setText("Created: " + text);
                textView = (TextView) convertView.findViewById(R.id.text4);
                textView.setText("ID: " + contacts.get(position).mContactId);
            }

            return convertView;
        }

        @Override
        public int getCount(){
            return contacts.size();
        }
    }
}
