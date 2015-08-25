package com.chan.buddy.surprise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chan.buddy.R;
import com.chan.buddy.utility.InputMethodUtility;
import com.chan.buddy.utility.ExpressionUtility;
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
    private ViewPager m_viewPager;
    private LinearLayout m_indicatorContainer;
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
            default:
                break;
        }
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
