package com.example.SystemHealth_Android.setaside;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.example.SystemHealth_Android.R;
import org.w3c.dom.Text;

/**
 * Created by emou on 6/17/14.
 */
public class PrintActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_text);

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");

        TextView textView = (TextView) findViewById(R.id.display_text);

        textView.setText(message);
    }
}
