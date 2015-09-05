package com.chan.buddy.surprise;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chan.buddy.R;
import com.chan.buddy.utility.DialogReleaseUtility;
import com.chan.buddy.utility.InputMethodUtility;
import com.chan.buddy.widget.expression.ExpressionFunctionReleaseHelper;

/**
 * Created by chan on 15-8-23.
 */
public class SendMessageActivity
        extends Activity implements View.OnClickListener{

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 表情栏显示
     */
    private static final short STATE_SMILE_SHOW = 0x0521;
    /**
     * 软键盘显示
     */
    private static final short STATE_KEYBOARD_SHOW = 0x0525;
    /**
     * 正常
     */
    private static final short STATE_NORMAL = 0x0520;
    /**
     * 记录当前状态
     */
    private short m_stateCurrent = STATE_NORMAL;

    /**
     * 表情键盘转换按钮
     */
    private ImageView m_turnImageView;
    /**
     * 内容输入框
     */
    private EditText m_editText;
    /**
     * 表情栏的父布局
     */
    private View m_expressionParent;
    /**
     * 存放表情
     */
    private ViewPager m_viewPager;
    /**
     * 存放点
     */
    private LinearLayout m_indicatorContainer;
    /**
     * 预览要发送的图片
     */
    private ImageView m_imageContainer;
    /**
     * 是否由多媒体内容
     */
    private boolean m_hasMultiMedia = false;

    private short m_currentMediaType = TYPE_TEXT;
    private String m_mediaData;
    private static final short TYPE_TEXT = 0x0521;
    private static final short TYPE_IMAGE = 0x0522;
    private static final short TYPE_LOCATION = 0x0523;
    private static final short TYPE_AUDIO = 0x0524;
    private static final short TYPE_VIDEO = 0x0525;
    private TextView m_mediaPrevTextView;
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.surprise_send_message);

        init();
    }

    /**
     * 做一些初始化工作
     */
    private void init(){
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        m_editText = (EditText) findViewById(R.id.id_send_message_edit_text);
        m_turnImageView = (ImageView) findViewById(R.id.id_surprise_item_turn);
        m_expressionParent = findViewById(R.id.id_expression_parent);
        m_viewPager = (ViewPager) findViewById(R.id.id_expression_view_pager);
        m_indicatorContainer = (LinearLayout) findViewById(R.id.id_page_indicator_container);
        m_mediaPrevTextView = (TextView) findViewById(R.id.id_send_message_prev);
        findViewById(R.id.id_send_message_add_video).setOnClickListener(this);
        findViewById(R.id.id_send_message_add_audio).setOnClickListener(this);
        findViewById(R.id.id_send_message_add_location).setOnClickListener(this);
        findViewById(R.id.id_back_image_view).setOnClickListener(this);
        m_imageContainer = (ImageView) findViewById(R.id.id_send_message_add_image);
        m_imageContainer.setOnClickListener(this);

        m_turnImageView.setOnClickListener(this);
        m_editText.setOnClickListener(this);

        ExpressionFunctionReleaseHelper.releaseExpressionFunction(
                this,
                m_editText,
                m_viewPager,
                m_indicatorContainer
        );
    }

    /**
     * 获取发送消息界面的intent
     * @param context
     * @return 启动发送消息的activity 的 intent
     */
    public static Intent getIntent(@NonNull Context context) {
        return new Intent(context, SendMessageActivity.class);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.id_surprise_item_turn:
                onClickTurn();
                break;
            case R.id.id_send_message_edit_text:
                onClickEditText();
                break;
            case R.id.id_back_image_view:
                onClickBack();
                break;
            case R.id.id_send_message_add_location:
                onClickAddLocation();
                break;
            case R.id.id_send_message_add_audio:
                onClickAddAudio();
                break;
            case R.id.id_send_message_add_video:
                onClickAddVideo();
                break;
            case R.id.id_send_message_add_image:
                onClickAddImage();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        onClickBack();
    }

    /**
     * 当返回键按下时
     */
    private void onClickBack(){

        //如果文本没有 多媒体文件页没有 那么就直接返回
        if(m_editText.getText().length() == 0 && !m_hasMultiMedia){
            finish();
            return;
        }

        DialogReleaseUtility.ButtonInfoHolder accept = new DialogReleaseUtility.
                ButtonInfoHolder("确定",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SendMessageActivity.this.finish();
            }
        });

        DialogReleaseUtility.ButtonInfoHolder cancel = new DialogReleaseUtility.
                ButtonInfoHolder("取消",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog dialog = DialogReleaseUtility.releaseDialog(
                this,
                null,
                "提示",
                "您有未保存的内容,确定退出吗?",
                false,
                accept,
                cancel
        );
        dialog.show();
    }

    /**
     * 当对应的 添加 图片 定位 声音 视频按钮按下是调用的
     */

    private final short REQUEST_IMAGE = 0x0521;
    private final short REQUEST_LOCATION = 0x0522;
    private final short REQUEST_VIDEO = 0x0523;
    private final short REQUEST_AUDIO = 0x0524;

    private void onClickAddImage() {
        Intent intent = SelectImagesActivity.getIntent(this);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void onClickAddLocation(){

    }

    private void onClickAddVideo() {
        Intent intent = RecordVideoActivity.getIntent(this);
        startActivityForResult(intent, REQUEST_VIDEO);
    }

    private void onClickAddAudio(){
        Intent intent = RecordAudioActivity.getIntent(this);
        startActivityForResult(intent, REQUEST_AUDIO);
    }

    /**
     * 当点击编辑的时候触发
     */
    private void onClickEditText(){
        if(m_stateCurrent != STATE_KEYBOARD_SHOW){
            m_stateCurrent = STATE_KEYBOARD_SHOW;
            showKeyboard();
            hideSmileBoard();
        }
    }

    /**
     * 响应点击表情按钮事件
     */
    private void onClickTurn(){

        if(m_stateCurrent == STATE_SMILE_SHOW ||
                m_stateCurrent == STATE_NORMAL){
            m_stateCurrent = STATE_KEYBOARD_SHOW;
            hideKeyboard();
            showSmileBoard();
            m_turnImageView.setImageResource(R.drawable.surprise_turn_keyboard);
        }
        else if(m_stateCurrent == STATE_KEYBOARD_SHOW){
            m_stateCurrent = STATE_SMILE_SHOW;
            showKeyboard();
            hideSmileBoard();
            m_turnImageView.setImageResource(R.drawable.surprise_add_smile);
        }
    }

    /**
     * 显示软键盘
     */
    private void showKeyboard() {
        InputMethodUtility.showKeyboard(this,m_editText);
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (InputMethodUtility.isKeyboardShow(this)) {
            InputMethodUtility.hideKeyboard(this, m_editText);
        }
    }

    /**
     * 显示表情面板
     */
    private void showSmileBoard(){
        m_expressionParent.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏表情面板
     */
    private void hideSmileBoard(){
        m_expressionParent.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_VIDEO && resultCode == RESULT_OK){
            onVideoResult(data);
            return;
        }

        if(requestCode == REQUEST_AUDIO && resultCode == RESULT_OK){
            onAudioResult(data);
            return;
        }

        if(requestCode == REQUEST_IMAGE && resultCode == RESULT_OK){
            onImageResult(data);
            return;
        }

        if(requestCode == REQUEST_LOCATION && resultCode == RESULT_OK){
            onLocationResult(data);
            return;
        }
    }

    private void onImageResult(Intent data){
        String fileName = data.getStringExtra(SelectImagesActivity.EXTRA_DATA);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = m_imageContainer.getWidth();
        options.outHeight = m_imageContainer.getHeight();
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(fileName,options);
        m_imageContainer.setImageBitmap(bitmap);
        setMediaType(TYPE_IMAGE);
    }

    //没有保存路径//******************************************************************
    private void onAudioResult(Intent data){
        addMediaPrev(R.drawable.ic_keyboard_voice_grey600_48dp);
        setMediaType(TYPE_AUDIO);
    }

    private void onLocationResult(Intent data){
        addMediaPrev(R.drawable.ic_place_grey600_48dp);
        setMediaType(TYPE_LOCATION);
    }

    /** 当录制视频返回后
     * @param data
     */
    private void onVideoResult(Intent data) {
        m_mediaData = data.getStringExtra(RecordVideoActivity.EXTRA_FILE_NAME);

        Bitmap thumbnail =  ThumbnailUtils.createVideoThumbnail(
                m_mediaData,
                MediaStore.Images.Thumbnails.MINI_KIND
        );

        if(thumbnail != null)
            addMediaPrev(thumbnail);
        else addMediaPrev(R.drawable.ic_videocam_grey600_48dp);

        setMediaType(TYPE_VIDEO);
    }

    /**
     * @param bitmap 用于预览的多媒体图片
     */
    private void addMediaPrev(Bitmap bitmap) {
        ImageSpan imageSpan = new ImageSpan(this, bitmap);
        addMediaPrev(imageSpan);
    }

    private void addMediaPrev(int resourceId) {
        ImageSpan imageSpan = new ImageSpan(this, resourceId);
        addMediaPrev(imageSpan);
    }

    private void addMediaPrev(ImageSpan imageSpan) {
        if(m_mediaPrevTextView.getVisibility() != View.VISIBLE)
            m_mediaPrevTextView.setVisibility(View.VISIBLE);

        SpannableString spannableString = new SpannableString(" ");
        spannableString.setSpan(
                imageSpan,
                0,
                spannableString.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        );
        m_mediaPrevTextView.setText(spannableString);
    }

    /** 设置多媒体类型
     * @param type {@link SendMessageActivity#TYPE_AUDIO}
     * {@link SendMessageActivity#TYPE_IMAGE} etc
     */
    private void setMediaType(short type){
        m_currentMediaType |= type;
    }
}
