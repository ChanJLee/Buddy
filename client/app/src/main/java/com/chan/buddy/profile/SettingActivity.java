package com.chan.buddy.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chan.buddy.MainActivity;
import com.chan.buddy.R;

/**
 * Created by chan on 15-9-16.
 */
public class SettingActivity extends Activity implements View.OnClickListener{
    /**
     * 标题
     */
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private TextView m_titleTextView;
    /**
     * 返回按钮
     */
    private ImageView m_backImageView;
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_layout);

        init();
    }

    private void init(){
        m_titleTextView = (TextView) findViewById(R.id.id_title_text_view);
        m_backImageView = (ImageView) findViewById(R.id.id_back_image_view);

        m_titleTextView.setText("设置");
        m_backImageView.setOnClickListener(this);

        findViewById(R.id.id_exit).setOnClickListener(this);
        findViewById(R.id.id_about).setOnClickListener(this);
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, SettingActivity.class);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id){
            case R.id.id_back_image_view:
                onBackClick();
                break;
            case R.id.id_exit:
                onExitClick();
                break;
            case R.id.id_about:
                onAbout();
            default:break;
        }
    }

    private void onAbout() {
        startActivity(AboutActivity.getIntent(this));
    }

    private void onBackClick(){
        finish();
    }

    private void onExitClick() {
        startActivity(MainActivity.getIntent(this));
    }
}
