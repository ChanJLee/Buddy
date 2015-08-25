package com.chan.buddy.widget.expression;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.chan.buddy.utility.ExpressionUtility;

/**
 * Created by chan on 15-8-24.
 */
public class ExpressionEditText extends EditText {

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

                final int maybe = ExpressionUtility.isBackIsExpressionAndReturnStart(s.toString());
                if (maybe == -1) return;
                s.delete(maybe, s.length());
            }
        });
    }
}
