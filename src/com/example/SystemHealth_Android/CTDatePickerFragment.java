package com.example.SystemHealth_Android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;

/**
 * Created by emou on 7/2/14.
 */
public class CTDatePickerFragment extends Fragment implements View.OnClickListener {

    View myFragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myFragmentView = inflater.inflate(R.layout.contact_date_frag,container,false);
        Button button = (Button) myFragmentView.findViewById(R.id.search_button);
        button.setOnClickListener(this);

        return myFragmentView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id){
            case R.id.search_button:
                DatePicker datePicker = (DatePicker) myFragmentView.findViewById(R.id.calendar1);
                String date1;
                int month = datePicker.getMonth() + 1;
                date1 = (String) (month>10 ? month:("0" + month));
                int day = datePicker.getDayOfMonth();
                date1 = date1 + "/" + (day>10? day:("0"+day));
                date1 = date1 + "/" + datePicker.getYear();

                datePicker = (DatePicker) myFragmentView.findViewById(R.id.calendar2);
                String date2;
                month = datePicker.getMonth() + 1;
                date2 = (String) (month>10 ? month:("0" + month));
                day = datePicker.getDayOfMonth();
                date2 = date2 + "/" + (day>10? day:("0"+day));
                date2 = date2 + "/" + datePicker.getYear();

                FrameLayout frameLayout = (FrameLayout) getActivity().findViewById(R.id.middle_fragment);

                if(frameLayout!=null) {
                    CTRequestListFragment contactListFragment = new CTRequestListFragment();
                    Bundle args = new Bundle();
                    args.putString("date1",date1);
                    args.putString("date2",date2);
                    contactListFragment.setArguments(args);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.middle_fragment,contactListFragment).commit();

                }else{//single pane layout
                    Intent intent = new Intent(getActivity(), CTRequestListActivity.class);
                    intent.putExtra("date1", date1);
                    intent.putExtra("date2",date2);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }
}
