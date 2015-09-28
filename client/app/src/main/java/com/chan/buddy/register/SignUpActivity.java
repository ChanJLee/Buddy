package com.chan.buddy.register;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.chan.buddy.R;
import com.chan.buddy.service.SignUpServer;
import com.chan.buddy.surprise.SelectImagesActivity;
import com.chan.buddy.utility.StorageUtility;

import java.io.File;

/**
 * Created by chan on 15-9-9.
 */
public class SignUpActivity extends Activity implements View.OnClickListener{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String EXTRA_USERNAME = "username";
    public static final String EXTRA_PASSWORD = "password";
    private static final int REQUEST_IMAGE = 0x0521;
    private static final int REQUEST_CROP = 0x0525;
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private EditText m_nameEditText;
    private EditText m_userNameEditText;
    private EditText m_passWordEditText;
    private ImageView m_avatarImageView;
    private Button m_registerButton;
    private SignUpServer.SignUpBinder m_signUpBinder;
    private String m_avatarFileName;
    private boolean m_hasAvatar = false;
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Handler m_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what != SignUpServer.MESSAGE_SIGN_UP) return;

            if(msg.arg1 == SignUpServer.SIGN_UP_SUCCESS){
                signUpSuccess();
            }else{
                signUpFailed();
            }
        }
    };
    private ServiceConnection m_serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            m_signUpBinder = (SignUpServer.SignUpBinder) service;
            m_signUpBinder.setOnSignUpResponse(new SignUpServer.SignUpResponse(m_handler));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            m_signUpBinder = null;
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        init();
    }

    private void signUpSuccess(){
        final String userName = m_userNameEditText.getText().toString();
        final String passWord = m_passWordEditText.getText().toString();

        Intent intent = new Intent();
        intent.putExtra(EXTRA_USERNAME,userName);
        intent.putExtra(EXTRA_PASSWORD,passWord);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void signUpFailed() {
        Toast.makeText(this, "改用户名已经被注册", Toast.LENGTH_SHORT).show();
        m_userNameEditText.setText("");
    }

    private void init() {
        m_nameEditText = (EditText) findViewById(R.id.id_name_edit);
        m_userNameEditText = (EditText) findViewById(R.id.id_username_edit);
        m_passWordEditText = (EditText) findViewById(R.id.id_password_edit);
        m_avatarImageView = (ImageView) findViewById(R.id.id_avatar);
        m_registerButton = (Button) findViewById(R.id.id_sign_up);

        m_nameEditText.setOnFocusChangeListener(
                new EditTextFocusChangeListener(findViewById(R.id.id_item1))
        );
        m_userNameEditText.setOnFocusChangeListener(
                new EditTextFocusChangeListener(findViewById(R.id.id_item2))
        );
        m_passWordEditText.setOnFocusChangeListener(
                new EditTextFocusChangeListener(findViewById(R.id.id_item3))
        );

        m_avatarImageView.setOnClickListener(this);
        m_registerButton.setOnClickListener(this);
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, SignUpActivity.class);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id){
            case R.id.id_sign_up:
                onSignUpClick();
                break;
            case R.id.id_avatar:
                onAvatarClick();
                break;
            default:break;
        }
    }

    private void onSignUpClick() {
        final String name = m_nameEditText.getText().toString();
        final String userName = m_userNameEditText.getText().toString();
        final String passWord = m_passWordEditText.getText().toString();

        String message = null;
        if (TextUtils.isEmpty(name))
            message = "昵称不能为空";
        else if (TextUtils.isEmpty(userName))
            message = "用户名不能为空";
        else if (TextUtils.isEmpty(passWord))
            message = "密码不能为空";

        if (message != null) {
            Toast.makeText(
                    this, message, Toast.LENGTH_SHORT).show();
            return;
        }

        if(m_hasAvatar){
            m_avatarImageView.setDrawingCacheEnabled(true);
            Bitmap bitmap = m_avatarImageView.getDrawingCache();
            m_avatarFileName = StorageUtility.recordAvatar(userName, bitmap);
            m_avatarImageView.setDrawingCacheEnabled(false);
        }else {
            m_avatarFileName = StorageUtility.defaultAvatar(userName);
        }

        if (m_signUpBinder != null)
            m_signUpBinder.signUp(name, userName, passWord, m_avatarFileName);
    }

    private void onAvatarClick() {
        Intent intent = SelectImagesActivity.getIntent(this);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            onResponseSelectImage(data);
        }else if(requestCode == REQUEST_CROP && resultCode == RESULT_OK){
            onResponseCrop(data);
        }
    }

    private void onResponseCrop(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            m_avatarImageView.setImageBitmap(photo);
            m_hasAvatar = true;
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

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, SignUpServer.class);
        bindService(intent, m_serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(m_serviceConnection);
    }

    private void onResponseSelectImage(Intent data){
        String fileName = data.getStringExtra(SelectImagesActivity.EXTRA_DATA);
        Uri uri = Uri.fromFile(new File(fileName));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        //intent.setType("image/*");
        final int width = m_avatarImageView.getWidth();

        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", width);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_CROP);
    }
}
