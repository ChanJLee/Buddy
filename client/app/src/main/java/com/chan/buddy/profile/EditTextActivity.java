package com.chan.buddy.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chan.buddy.R;

/**
 * Created by chan on 15-9-18.
 */
public class EditTextActivity extends Activity implements View.OnClickListener{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public final static String EXTRA_DATA = "i believe y";
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private Button m_acceptButton;
    private TextView m_titleTextView;
    private ImageView m_backImageView;
    private EditText m_editText;
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_text_layout);

        init();
    }

    private void init(){
        m_acceptButton = (Button) findViewById(R.id.id_accept_button);
        m_backImageView = (ImageView) findViewById(R.id.id_back_image_view);
        m_titleTextView = (TextView) findViewById(R.id.id_title_text_view);
        m_editText = (EditText) findViewById(R.id.id_edit_text);

        m_titleTextView.setText("修改");

        m_backImageView.setOnClickListener(this);
        m_acceptButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id){
            case R.id.id_back_image_view:
                onBackClick();
                break;
            case R.id.id_accept_button:
                onAcceptClick();
                break;
            default:break;
        }
    }

    private void onBackClick(){
        setResult(RESULT_CANCELED);
        finish();
    }

    private void onAcceptClick() {
        final String content = m_editText.getText().toString();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATA, content);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static Intent getIntent(@NonNull Context context) {
        return new Intent(context, EditTextActivity.class);
    }
}
