package com.chan.buddy.widget.expression;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.text.Spannable;
import android.util.AttributeSet;
import android.widget.EditText;

import com.chan.buddy.utility.ExpressionUtility;

/**
 * Created by chan on 15-8-24.
 */
public class ExpressionEditText extends EditText {
    public ExpressionEditText(Context context) {
        super(context);
    }

    public ExpressionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpressionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExpressionEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
