package com.chan.buddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chan.buddy.main.MainInterfaceActivity;

public class MainActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.id_sign_in).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = MainInterfaceActivity.getIntent(this);
        startActivity(intent);
    }
}
