package com.chan.buddy.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chan.buddy.R;
import com.chan.buddy.loginInfo.LoginInfoManager;
import com.chan.buddy.surprise.SelectImagesActivity;
import com.chan.buddy.utility.StorageUtility;

import java.io.File;

/**
 * Created by chan on 15-9-4.
 */
public class ProfileActivity extends Activity
        implements View.OnClickListener{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int REQUEST_IMAGE = 0x0525;
    private static final int REQUEST_CROP = 0x0526;
    private static final int REQUEST_NICKNAME = 0x0521;
    private static final int REQUEST_SIGNATURE = 0x0522;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private ImageView m_avatarImageView;
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        init();
    }

    private void init() {
        m_avatarImageView = (ImageView) findViewById(R.id.id_avatar_image_view);
        findViewById(R.id.id_qr).setOnClickListener(this);
        findViewById(R.id.id_avatar).setOnClickListener(this);
        findViewById(R.id.id_back_image_view).setOnClickListener(this);
        findViewById(R.id.id_nick_name).setOnClickListener(this);
        findViewById(R.id.id_signature).setOnClickListener(this);
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
            case R.id.id_back_image_view:
                onBackClick();
                break;
            case R.id.id_avatar:
                onAvatarClick();
                break;
            case R.id.id_nick_name:
                onNickNameClick();
                break;
            case R.id.id_signature:
                onSignatureClick();
                break;
            default: break;
        }
    }

    private void onSignatureClick() {
        Intent intent = EditTextActivity.getIntent(this);
        startActivityForResult(intent, REQUEST_SIGNATURE);
    }

    private void onNickNameClick() {
        Intent intent = EditTextActivity.getIntent(this);
        startActivityForResult(intent, REQUEST_NICKNAME);
    }

    private void onQRClick(){ startActivity(QRShowActivity.getIntent(this)); }

    private void onBackClick(){ finish(); }

    private void onAvatarClick() {
        Intent intent = SelectImagesActivity.getIntent(this);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) return;

        if(requestCode == REQUEST_IMAGE){
            onResponseSelectImage(data);
        }

        else if(requestCode == REQUEST_CROP){
            onResponseCrop(data);
        }

        else if(requestCode == REQUEST_NICKNAME){
            onResponseNickName(data);
        }

        else if(requestCode == REQUEST_SIGNATURE){
            onResponseSignature(data);
        }
    }

    private void onResponseNickName(Intent data) {
        final String content = data.getStringExtra(EditTextActivity.EXTRA_DATA);
        TextView textView = (TextView) findViewById(R.id.id_name);
        textView.setText(content);
    }

    private void onResponseSignature(Intent data) {
        final String content = data.getStringExtra(EditTextActivity.EXTRA_DATA);
        TextView textView = (TextView) findViewById(R.id.id_sign);
        textView.setText(content);
    }

    private void onResponseCrop(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            m_avatarImageView.setImageBitmap(photo);
            StorageUtility.recordAvatar(
                    LoginInfoManager.getLastedUserName(this),
                    photo
            );
        }
    }

    private void onResponseSelectImage(Intent data){

        String fileName = data.getStringExtra(SelectImagesActivity.EXTRA_DATA);
        Uri uri = Uri.fromFile(new File(fileName));

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
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
