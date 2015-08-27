package com.chan.buddy.surprise;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
    private boolean m_hasImage = false;
    private boolean m_hasLocation = false;
    private boolean m_hasAudio = false;
    private boolean m_hasVideo = false;
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

    private void onClickAddImage(){

    }

    private void onClickAddLocation(){

    }

    private void onClickAddVideo() {
        Intent intent = RecordVideoActivity.getIntent(this);
        startActivityForResult(intent, REQUEST_VIDEO);
    }

    private void onClickAddAudio(){

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
}
