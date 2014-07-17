package com.example.SystemHealth_Android.setaside;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.SystemHealth_Android.MyActivity;
import com.example.SystemHealth_Android.R;


/**
 * Created by emou on 6/16/14.
 */
public class SystemHealthFragment extends Fragment implements View.OnClickListener {

    public boolean systemHealthy;

    String statusText=null;
    String text1=null;
    String text2=null;
    String text3=null;

    public SystemHealthFragment(){
        super();
        systemHealthy=true;
    }

    SystemHealthFragment(boolean healthy){
        super();
        systemHealthy = healthy;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(savedInstanceState!=null){
            systemHealthy = savedInstanceState.getBoolean("systemHealthy");
        }

        View myFragmentView = null;
        Resources res = getResources();
        Drawable drawable = null;
        TextView textView = null;
        ImageView imageView = null;

        if(savedInstanceState!=null && !systemHealthy){
            String savedText1 = savedInstanceState.getString("text1");
            String savedText2 = savedInstanceState.getString("text2");
            String savedText3 = savedInstanceState.getString("text3");

            if(savedText1!=null){
                text1 = savedText1;
            }
            if(savedText2!=null){
                text2 = savedText2;
            }
            if(savedText3!=null){
                text3 = savedText3;
            }
        }

        if(systemHealthy) {
            myFragmentView = inflater.inflate(R.layout.green_fragment_2, container, false);
            imageView = (ImageView) myFragmentView.findViewById(R.id.health_image);
            drawable = res.getDrawable(R.drawable.green_checkmark);
        }else{
            myFragmentView = inflater.inflate(R.layout.red_health_fragment,container,false);
            imageView = (ImageView) myFragmentView.findViewById(R.id.health_image);

            if(savedInstanceState!=null) {
                statusText = savedInstanceState.getString("statusText");
            }
            if(statusText==null){

                int i=0;
                if(text1!=null){
                    i++;
                }
                if(text2!=null){
                    i++;
                }
                if(text3!=null){
                    i++;
                }

                statusText = i +" Problems";
            }

            textView = (TextView) myFragmentView.findViewById(R.id.status);
            textView.setText(statusText);
            textView = (TextView) myFragmentView.findViewById(R.id.first_line);
            textView.setText(text1);
            textView = (TextView) myFragmentView.findViewById(R.id.second_line);
            textView.setText(text2);
            textView = (TextView) myFragmentView.findViewById(R.id.third_line);
            textView.setText(text3);
            drawable = res.getDrawable(R.drawable.red_exclamation_point);
        }

        textView = (TextView) myFragmentView.findViewById(R.id.timestamp);
        textView.setText(MyActivity.updateTime);

        imageView.setImageDrawable(drawable);

        LinearLayout linearLayout = (LinearLayout) myFragmentView.findViewById(R.id.fragment_block);
        linearLayout.setOnClickListener(this);

        return myFragmentView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){

        savedInstanceState.putBoolean("systemHealthy",systemHealthy);

        savedInstanceState.putString("statusText",statusText);

        savedInstanceState.putString("text1", text1);
        savedInstanceState.putString("text2", text2);
        savedInstanceState.putString("text3", text3);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(),PrintActivity.class);
        String message = systemHealthy?"Your system is healthy.":"Your system has some problems.";

        intent.putExtra("message",message);
        startActivity(intent);
    }
}
