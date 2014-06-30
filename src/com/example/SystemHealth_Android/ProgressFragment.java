package com.example.SystemHealth_Android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by emou on 6/18/14.
 */
public class ProgressFragment extends Fragment implements View.OnClickListener {

    private TextView textView;
    private ProgressBar progressBar;
    private int progressStatus=0;
    private Handler handler = new Handler();
    private String progressText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View myFragmentView = inflater.inflate(R.layout.progress_fragment,container,false);

        textView = (TextView) myFragmentView.findViewById(R.id.progress_text);
        progressBar = (ProgressBar) myFragmentView.findViewById(R.id.progress_bar);

        if(savedInstanceState!=null){
            progressStatus = savedInstanceState.getInt("progressStatus");
            progressText = savedInstanceState.getString("progressText");
            textView.setText(progressText);
        }

        if(progressStatus<100) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    while (progressStatus < 100) {
                        progressStatus += 2;

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressText = progressStatus + "% Ready";
                                textView.setText(progressText);
                                progressBar.setProgress(progressStatus);
                            }
                        });

                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        LinearLayout linearLayout = (LinearLayout) myFragmentView.findViewById(R.id.progress_fragment_layout);
        linearLayout.setOnClickListener(this);

        return myFragmentView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        //save linked iTunes Object list (may be null)
        savedInstanceState.putInt("progressStatus",progressStatus);
        savedInstanceState.putString("progressText",progressText);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if(progressStatus>=100) {
            Intent intent = new Intent(getActivity(), PrintActivity.class);
            intent.putExtra("message", "Your MDMS is loaded!");
            startActivity(intent);
        }
    }
}
