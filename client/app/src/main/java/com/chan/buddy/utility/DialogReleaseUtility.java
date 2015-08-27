package com.chan.buddy.utility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

/**
 * Created by chan on 15-8-26.
 */
public class DialogReleaseUtility {

    /**
     * 存放对话框按钮上的 文字 和 点击监听器
     */
    public static class ButtonInfoHolder{
        private String m_text;
        private DialogInterface.OnClickListener m_clickListener;

        public ButtonInfoHolder() {
        }

        public ButtonInfoHolder(
                String text,
                DialogInterface.OnClickListener clickListener) {
            m_text = text;
            m_clickListener = clickListener;
        }

        public String getText() {
            return m_text;
        }

        public void setText(String text) {
            m_text = text;
        }

        public DialogInterface.OnClickListener getClickListener() {
            return m_clickListener;
        }

        public void setClickListener(DialogInterface.OnClickListener clickListener) {
            m_clickListener = clickListener;
        }
    }

    /**
     * 生成对话框
     * @param context 上下文
     * @param icon 图标
     * @param title 标题
     * @param message 内容
     * @param cancelAble 可取消吗
     * @param positiveButtonHolder 参考
     * {@link com.chan.buddy.utility.DialogReleaseUtility.ButtonInfoHolder}
     * @param negativeButtonHolder
     * @return 对话框
     */
    public static Dialog releaseDialog(
            @NonNull Context context,
            Drawable icon,
            @NonNull String title,
            @NonNull String message,
            boolean cancelAble,
            ButtonInfoHolder positiveButtonHolder,
            ButtonInfoHolder negativeButtonHolder){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(icon != null) builder.setIcon(icon);

        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(cancelAble);

        if(positiveButtonHolder != null)
            builder.setPositiveButton(
                    positiveButtonHolder.getText(),
                    positiveButtonHolder.getClickListener()
            );

        if(negativeButtonHolder != null)
            builder.setNegativeButton(
                    negativeButtonHolder.getText(),
                    negativeButtonHolder.getClickListener()
            );

        return builder.create();
    }
}
