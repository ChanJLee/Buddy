package com.chan.buddy.utility;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by chan on 15-8-23.
 * 用于管理软键盘
 */
public class InputMethodUtility {

    /**
     * 确认软键盘是否显示
     *
     * @param context 上下文
     * @return true 代表软键盘显示 否则隐藏
     */
    public static boolean isKeyboardShow(@NonNull Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return inputMethodManager.isActive();
    }

    /**
     * 隐藏软键盘
     *
     * @param context 上下文
     * @param target  接受输入的目标视图
     */
    public static void hideKeyboard(@NonNull Context context, @NonNull View target) {
        InputMethodManager inputMethodManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                target.getWindowToken(),
                0
        );
    }

    /**
     * 隐藏软键盘
     *
     * @param context 上下文
     * @param target  接受输入的目标视图
     */
    public static void showKeyboard(@NonNull Context context,@NonNull View target) {
        InputMethodManager inputMethodManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(target, InputMethodManager.SHOW_FORCED);
    }
}