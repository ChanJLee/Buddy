package com.chan.buddy.widget.expression;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import com.chan.buddy.R;
import com.chan.buddy.utility.ExpressionUtility;

/**
 * Created by chan on 15-8-24.
 */
public class ExpressionEditText extends EditText {
    private int m_prevLength = 0;

    public ExpressionEditText(Context context) {
        super(context);
        init();
    }

    public ExpressionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExpressionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExpressionEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * 初始化 让其支持删除表情功能
     */
    private void init(){
        addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                //获得当前的长度
                final int length = s.length();

                //如果之前的长度小于当前的长度 那么就是添加了字符
                if(m_prevLength < length){
                    m_prevLength = length;
                    return;
                }

                final int end = getSelectionEnd();

                final int maybe = ExpressionUtility.
                        isBackIsExpressionAndReturnStart(
                                s.toString().subSequence(
                                        0,
                                        end
                                )
                        );

                if (maybe == -1) return;
                s.delete(maybe, end);

                m_prevLength = s.length();
            }
        });
    }

    @Override
    public boolean onTextContextMenuItem(int id) {

        //当是粘贴的时候要做特殊处理
        if(id == android.R.id.paste){
            Context context = getContext();
            ClipboardManager clipboardManager = (ClipboardManager)
                    context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = clipboardManager.getPrimaryClip();
            CharSequence string = clipData.getItemAt(0).getText();
            Spannable spannable = ExpressionUtility.charSequenceToSpannable(context, string);
            getText().insert(getSelectionStart(),spannable);
            return true;
        }

        return super.onTextContextMenuItem(id);
    }
}
