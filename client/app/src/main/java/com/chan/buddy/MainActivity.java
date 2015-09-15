package com.chan.buddy;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chan.buddy.main.MainInterfaceActivity;
import com.chan.buddy.register.SignUpActivity;
import com.chan.buddy.service.SignInServer;
import com.chan.buddy.utility.DialogReleaseUtility;

import java.util.HashSet;
import java.util.Set;
import static com.chan.buddy.service.SignInServer.MESSAGE_SIGN_IN;
import static com.chan.buddy.service.SignInServer.SIGN_IN_FAILED;
import static com.chan.buddy.service.SignInServer.SIGN_IN_SUCCESS;

/**
 *
 */
public class MainActivity extends Activity
        implements View.OnClickListener{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String LAST_INFO = "success";
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private EditText m_userNameEditText;
    private EditText m_passwordEditText;
    private Button m_signInButton;
    private TextView m_signUpTextView;
    private SignInServer.SignInBinder m_signInBinder;

    private ServiceConnection m_serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            m_signInBinder = (SignInServer.SignInBinder) service;
            m_signInBinder.setOnSignInResponse(
                    new SignInServer.SignInBinder.ForwardOnSignInResponse(m_handler));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            m_signInBinder = null;
        }
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        m_signInButton = (Button) findViewById(R.id.id_sign_in);
        m_userNameEditText = (EditText) findViewById(R.id.id_username);
        m_passwordEditText = (EditText) findViewById(R.id.id_password);
        m_signUpTextView = (TextView) findViewById(R.id.id_sign_up);

        m_signInButton.setOnClickListener(this);
        m_signUpTextView.setOnClickListener(this);

        m_userNameEditText.setOnFocusChangeListener(
                new EditTextFocusChangeListener(findViewById(R.id.id_item1))
        );
        m_passwordEditText.setOnFocusChangeListener(
                new EditTextFocusChangeListener(findViewById(R.id.id_item2))
        );
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id){
            case R.id.id_sign_in:
                onSignInClick();
                break;
            case R.id.id_sign_up:
                onSignUpClick();
                break;
            default:break;
        }
    }

    private static final int REQUEST_SIGN_UP = 0x0525;

    /**
     * 当注册按下时候触发
     */
    private void onSignUpClick() {
        Intent intent = SignUpActivity.getIntent(this);
        startActivityForResult(intent, REQUEST_SIGN_UP);
    }

    /**
     * 当登录按钮登录的时候触发
     */
    private void onSignInClick(){

        final String userName = m_userNameEditText.getText().toString();
        final String passWord = m_passwordEditText.getText().toString();

        if(TextUtils.isEmpty(userName) ||
                TextUtils.isEmpty(passWord)){
            Toast.makeText(this,"密码或者用户名不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(m_signInBinder != null) {
            m_signInBinder.signIn(userName, passWord);
        }
    }

    /**
     * 如果登录成功 那么就记录下用户的信息
     * 用来下次继续登录
     */
    private void recordUserInfo() {

        //获得编辑对象
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //获得用户名 和 密码
        final String userName = m_userNameEditText.getText().toString();
        final String password = m_passwordEditText.getText().toString();

        editor.putString(LAST_INFO, userName);

        //提交修改1
        editor.putString(userName, password);
        editor.commit();
    }

    private Handler m_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == MESSAGE_SIGN_IN){
                handleSignInMessage(msg);
            }
        }
    };

    /**
     * 用于处理登录服务返回的消息
     * @param message 服务器返回的消息
     */
    private void handleSignInMessage(Message message){
        if(message.arg1 == SIGN_IN_SUCCESS){
            onSignInSuccess();
        }else if(message.arg1 == SIGN_IN_FAILED){
            onSignInFailed();
        }
    }

    /**
     * 当登录失败的时候显示
     */
    private void onSignInFailed(){

        //获得编辑对象
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_INFO,"");
        editor.commit();

        DialogReleaseUtility.ButtonInfoHolder holder = new DialogReleaseUtility.ButtonInfoHolder(
                "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        Dialog dialog = DialogReleaseUtility.releaseDialog(
                this,
                null,
                "消息",
                "登录失败",
                false,
                holder,
                null
        );
        dialog.show();
    }

    /**
     * 登录成功的时候触发
     */
    private void onSignInSuccess(){
        recordUserInfo();
        Intent intent = MainInterfaceActivity.getIntent(this);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this,SignInServer.class);
        bindService(intent, m_serviceConnection, BIND_AUTO_CREATE);
        readLastedSignInInfo();
    }

    private void readLastedSignInInfo() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        final String userName = sharedPreferences.getString(LAST_INFO, "");

        if (TextUtils.isEmpty(userName)) return;
        m_userNameEditText.setText(userName);

        final String passWord = sharedPreferences.getString(userName, "");

        if (TextUtils.isEmpty(passWord)) return;
        m_passwordEditText.setText(passWord);

        if (m_signInBinder != null)
            m_signInBinder.signIn(userName, passWord);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(m_serviceConnection);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_SIGN_UP && resultCode == RESULT_OK) {
            final String userName = data.getStringExtra(SignUpActivity.EXTRA_USERNAME);
            final String passWord = data.getStringExtra(SignUpActivity.EXTRA_PASSWORD);

            m_userNameEditText.setText(userName);
            m_passwordEditText.setText(passWord);

            if (m_signInBinder != null)
                m_signInBinder.signIn(userName, passWord);
        }
    }

    static final private class EditTextFocusChangeListener
            implements View.OnFocusChangeListener{
        private View m_parent;

        public EditTextFocusChangeListener(View parent){
            m_parent = parent;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            final int drawable = hasFocus ?
                    R.drawable.edit_selected : R.drawable.edit_normal;
            m_parent.setBackgroundResource(drawable);
        }
    }
}
