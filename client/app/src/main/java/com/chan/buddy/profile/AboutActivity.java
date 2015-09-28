package com.chan.buddy.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.chan.buddy.R;

/**
 * Created by chan on 15-9-16.
 */
public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        findViewById(R.id.id_back_image_view)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        TextView titleTextView = (TextView)
                findViewById(R.id.id_title_text_view);
        titleTextView.setText("关于我们");
    }

    static public Intent getIntent(Context context) {
        return new Intent(context, AboutActivity.class);
    }
}
