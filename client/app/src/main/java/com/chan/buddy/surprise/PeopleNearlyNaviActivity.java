package com.chan.buddy.surprise;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chan.buddy.R;

/**
 * Created by chan on 15-9-19.
 */
public class PeopleNearlyNaviActivity extends Activity
        implements View.OnClickListener{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private final static String DIALOG_SETTING = "youAreTheAppleInMyEyes";
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_nearly);

        TextView titleTextView = (TextView) findViewById(R.id.id_title_text_view);
        titleTextView.setText("附近的人");

        findViewById(R.id.id_back_image_view).setOnClickListener(this);
        findViewById(R.id.id_start).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id){
            case R.id.id_back_image_view:
                onBackClick();
                break;
            case R.id.id_start:
                onStartClick();
                break;
            default:break;
        }
    }

    private void onBackClick(){
        finish();
    }

    private void onStartClick() {
        if(isShowDialog()){
            showDialog();
        }else {
            invokeNearlyActivity();
        }
    }

    private void invokeNearlyActivity(){
        Intent intent = PeopleNearlyActivity.getIntent(
                PeopleNearlyNaviActivity.this
        );
        startActivity(intent);

//        Intent intent = new Intent(this, RadarDemo.class);
//        startActivity(intent);
    }

    private boolean isShowDialog(){
        SharedPreferences sharedPreferences =
                getSharedPreferences(DIALOG_SETTING,MODE_PRIVATE);
        return TextUtils.isEmpty(sharedPreferences.getString(DIALOG_SETTING, ""));
    }

    private void showDialog(){

        final NearlyDialog dialog = new NearlyDialog(this, R.style.dialog);
        dialog.setOnAcceptClicked(new NearlyDialog.OnAcceptClicked() {
            @Override
            public void onAcceptClicked() {
                invokeNearlyActivity();
                dialog.dismiss();
            }
        });

        dialog.setOnCancelClicked(new NearlyDialog.OnCancelClicked() {
            @Override
            public void onCancelClicked() {
                dialog.dismiss();
            }
        });

        dialog.setOnRecordSetting(new NearlyDialog.OnRecordSetting() {
            @Override
            public void onRecordSetting(boolean isNoteNextTime) {
                SharedPreferences sharedPreferences = PeopleNearlyNaviActivity.this
                        .getSharedPreferences(DIALOG_SETTING, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(DIALOG_SETTING, isNoteNextTime ? "" : "no");
                editor.commit();
            }
        });

        dialog.show();
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, PeopleNearlyNaviActivity.class);
    }

    private final static class NearlyDialog extends Dialog
            implements View.OnClickListener{
        ////////////////////////////////////////////////////////////////////////////////////////////
        private OnAcceptClicked m_onAcceptClicked;
        private OnCancelClicked m_onCancelClicked;
        private OnRecordSetting m_onRecordSetting;
        private boolean m_isCheck = false;
        private ImageView m_checkBox;
        ////////////////////////////////////////////////////////////////////////////////////////////

        public void setOnAcceptClicked(OnAcceptClicked onAcceptClicked) {
            m_onAcceptClicked = onAcceptClicked;
        }

        public void setOnCancelClicked(OnCancelClicked onCancelClicked) {
            m_onCancelClicked = onCancelClicked;
        }

        public void setOnRecordSetting(OnRecordSetting onRecordSetting) {
            m_onRecordSetting = onRecordSetting;
        }

        public NearlyDialog(Context context, int themeResId) {
            super(context, themeResId);
            init();
        }

        private void init() {
            setContentView(releaseContentView());
            Resources resources = getContext().getResources();
            final int width = (int) resources.getDimension(R.dimen.dialog_w);
            final int height = (int) resources.getDimension(R.dimen.dialog_h);
            getWindow().setLayout(width, height);
        }

        @Override
        public void onClick(View v) {
            final int id = v.getId();
            switch (id){
                case R.id.id_accept:
                    onAcceptInvoke();
                    break;
                case R.id.id_cancel:
                    onCancelInvoke();
                    break;
                case R.id.id_check_box:
                    onCheckBoxInvoke();
                    break;
                default:break;
            }
        }

        private void onCheckBoxInvoke() {
            m_isCheck = !m_isCheck;
            final int background = m_isCheck ?
                    R.drawable.checked : R.drawable.no_checked;
            m_checkBox.setImageResource(background);
        }

        private void onAcceptInvoke() {

            if(m_onRecordSetting != null){
                m_onRecordSetting.onRecordSetting(!m_isCheck);
            }

            if (m_onAcceptClicked != null) {
                m_onAcceptClicked.onAcceptClicked();
            }
        }

        private void onCancelInvoke(){
            if(m_onCancelClicked != null){
                m_onCancelClicked.onCancelClicked();
            }
        }

        public interface OnAcceptClicked{
            void onAcceptClicked();
        }

        public interface OnCancelClicked{
            void onCancelClicked();
        }

        public interface OnRecordSetting{
            void onRecordSetting(boolean isNoteNextTime);
        }

        private View releaseContentView() {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.dialog_layout, null);
            m_checkBox = (ImageView) view.findViewById(R.id.id_check_box);
            m_checkBox.setOnClickListener(this);
            view.findViewById(R.id.id_cancel)
                    .setOnClickListener(this);
            view.findViewById(R.id.id_accept)
                    .setOnClickListener(this);
            return view;
        }
    }
}
