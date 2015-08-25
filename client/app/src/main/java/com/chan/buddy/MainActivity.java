package com.chan.buddy;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.chan.buddy.main.MainInterfaceActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.m_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MainInterfaceActivity.getIntent(MainActivity.this));
            }
        });
    }
}
