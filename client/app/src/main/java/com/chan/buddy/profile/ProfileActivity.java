package com.chan.buddy.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.chan.buddy.R;

/**
 * Created by chan on 15-9-4.
 */
public class ProfileActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        init();
    }

    private void init(){
        findViewById(R.id.id_qr).setOnClickListener(this);
    }

    static public Intent getIntent(@NonNull Context context) {
        return new Intent(context, ProfileActivity.class);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id){
            case R.id.id_qr:
                onQRClick();
                break;
            default: break;
        }
    }

    private void onQRClick(){
        startActivity(QRShowActivity.getIntent(this));
    }
}
