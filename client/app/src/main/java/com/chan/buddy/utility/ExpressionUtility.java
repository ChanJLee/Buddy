package com.chan.buddy.utility;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.SparseArray;
import android.widget.EditText;

import com.chan.buddy.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chan on 15-8-21.
 * 用于提供表情的spannable
 */
public class ExpressionUtility {
    private static int s_drawables[] = null;
    private static SparseArray<String> s_sparseArray;
    private static Pattern s_pattern;
    private static final short MAX_VALUE_LENGTH = 9;
    private static Pattern s_fragmentPattern;

    static {
            s_drawables = new int[]{
                    R.drawable.image0, R.drawable.image1, R.drawable.image2, R.drawable.image3,
                    R.drawable.image4, R.drawable.image5, R.drawable.image6, R.drawable.image7,
                    R.drawable.image8, R.drawable.image9, R.drawable.image10, R.drawable.image11,
                    R.drawable.image12, R.drawable.image13, R.drawable.image14, R.drawable.image15,
                    R.drawable.image16, R.drawable.image17, R.drawable.image18, R.drawable.image19,
                    R.drawable.image70,
                    R.drawable.image20, R.drawable.image21, R.drawable.image22, R.drawable.image23,
                    R.drawable.image24, R.drawable.image25, R.drawable.image26, R.drawable.image27,
                    R.drawable.image28, R.drawable.image29, R.drawable.image30, R.drawable.image31,
                    R.drawable.image32, R.drawable.image33, R.drawable.image34, R.drawable.image35,
                    R.drawable.image36, R.drawable.image37, R.drawable.image38, R.drawable.image39,
                    R.drawable.image70,
                    R.drawable.image40, R.drawable.image41, R.drawable.image42, R.drawable.image43,
                    R.drawable.image44, R.drawable.image45, R.drawable.image46, R.drawable.image47,
                    R.drawable.image48, R.drawable.image49, R.drawable.image50, R.drawable.image51,
                    R.drawable.image52, R.drawable.image53, R.drawable.image54, R.drawable.image55,
                    R.drawable.image56, R.drawable.image57, R.drawable.image58, R.drawable.image59,
                    R.drawable.image70,
                    R.drawable.image60, R.drawable.image61, R.drawable.image62, R.drawable.image63,
                    R.drawable.image64, R.drawable.image65, R.drawable.image66, R.drawable.image67,
                    R.drawable.image68, R.drawable.image69, R.drawable.image70};
        s_pattern = Pattern.compile("\\[(image[1-6]?[0-9])\\]");
        s_fragmentPattern = Pattern.compile("\\[(image[1-6]?[0-9])");
    }

    /**
     * @return 返回所有的表情资源
     */
    public static int[] getExpressions(){
        return s_drawables;
    }

    /**
     * 响应resourceId 对应表情的事件
     * 当是resourceId对应的是一个删除表情时  如果光标对应的是一个字符 那么这个字符将被删除
     * 如果是一个表情 那么这个表情将被删除
     * 如果resourceId对应的是一个表情时
     * 会在光标对应的位置插入一个表情
     * @param context  上下文
     * @param resourceId 资源引用值 为drawable类型
     * @param editText 对应的输入框
     */
    public static void onExpressionClick(Context context, int resourceId,EditText editText) {
        if (resourceId == R.drawable.image70)
            onDeleteExpressionClick(editText);
        else onNonDeleteExpressionClick(context, resourceId, editText);
    }

    /**
     * 当删除表情按下时触发
     * @param editText
     */
    private static void onDeleteExpressionClick(@NonNull EditText editText) {

        //如果是空文本 那么不做任何事情
        if (TextUtils.isEmpty(editText.getText())) return;

        //如果选中了一段文字 那么删除
        final int start = editText.getSelectionStart();
        final int end = editText.getSelectionEnd();

        Editable editable = editText.getText();
        if (start != end) {
            editable.delete(start, end);
            return;
        }

        //如果要删除一个字符或者表情
        //那么我们往前查找 替换字符最长的长度
        int first = end - MAX_VALUE_LENGTH;

        //如果是前面根本不够找到一个可能的字符串 那么就一定是删除一个字符
        //但是这个条件是光标不在第一个位置处
        if (first < 0 && end != 0 && editable.charAt(0) != '[') {
            editable.delete(end - 1, end);
            return;
        }

        //如果小于0 则为0
        if(first < 0) first = 0;

        //如果我们够找到一个可能的字符串 那么就试着查看一下它的值是否匹配
        String content = editable.toString();
        String maybe = content.substring(first, end);

        Matcher matcher = s_pattern.matcher(maybe);

        //如果找到 那么就是删除一个表情
        if (matcher.find()) {

            int length = matcher.end() - matcher.start();
            editable.delete(end - length, end);
            return;
        }

        //否则还是删除一个字符 不过注意是不是在第一个位置
        if (end != 0)
            editable.delete(end - 1, end);
    }

    /**
     * 当非删除表情按下时触发
     * @param context  上下文
     * @param resourceId 资源引用值 为drawable类型
     * @param editText 对应的输入框
     */
    private static void onNonDeleteExpressionClick(Context context,int resourceId, EditText editText) {
        ImageSpan imageSpan = getImageSpan(context, resourceId);
        SparseArray<String> stringSparseArray = getSparseArray();

        SpannableString spannableString = SpannableString.valueOf(stringSparseArray.get(resourceId));
        spannableString.setSpan(imageSpan,0,spannableString.length(),Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        editText.getText().insert(editText.getSelectionStart(),spannableString);
    }

    /**
     * 将指定的字符转化为对应的带表情SpannableString对象
     * @param context  上下文
     * @param charSequence 内容
     * @return 对应的spannable string
     */
    public static SpannableString charSequenceToSpannable(@NonNull Context context,@NonNull CharSequence charSequence){

        SpannableString spannable = SpannableString.valueOf(charSequence);

        Matcher matcher = s_pattern.matcher(spannable);

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();

            final String value = matcher.group(0);

            ImageSpan imageSpan = getImageSpan(context,value);
            if(imageSpan == null) continue;
            spannable.setSpan(
                    imageSpan,
                    start,
                    end,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            );
        }
        return spannable;
    }

    /**
     * @return 资源值 与 文件名对应的 sparse array
     */
    private static SparseArray<String> getSparseArray(){
        if(s_sparseArray != null) return s_sparseArray;

        s_sparseArray = new SparseArray<String>();

        //把各个表情对应的字符串
        int idx = 0;
        for(int id : s_drawables){
            if(id != R.drawable.image70)
                s_sparseArray.put(id,"[image" + idx + "]");
            ++idx;
        }

        return s_sparseArray;
    }

    /**
     * @param context  上下文
     * @param value 字符串
     * @return 对应的image span
     */
    private static ImageSpan getImageSpan(@NonNull Context context,@NonNull String value) {
        SparseArray<String> stringSparseArray = getSparseArray();

        //因为indexOfValue默认使用 == 所以还是要深比较
        //int idx = stringSparseArray.indexOfValue(value);
        int idx = 0;
        final int size = stringSparseArray.size();
        for (; idx < size; ++idx) {
            if(stringSparseArray.valueAt(idx).equals(value))
                break;
        }
        if (idx == size) return null;
        return getImageSpan(context, stringSparseArray.keyAt(idx));
    }

    /**
     * @param context 上下文
     * @param key 键值
     * @return 对应的image span
     */
    private static ImageSpan getImageSpan(@NonNull Context context ,int key) {
        return new ImageSpan(context, key);
    }

    /**
     * 检查末尾是不是一个表情 如果是 则返回它的开始位置
     * 如果不是 则返回 -1
     * @param charSequence 匹配字符
     * @return 返回最后一个表情的开始位置
     */
    public static int isBackIsExpressionAndReturnStart(CharSequence charSequence) {

        //如果是空的 那么不合法
        if (TextUtils.isEmpty(charSequence)) return -1;

        //如果最后一个字符不是数字那么不合法
        final int length = charSequence.length();
        final char back = charSequence.charAt(length - 1);
        if(!Character.isDigit(back)) return -1;

        //如果剩下的长度连一个表情最基本的长度都不到 也不合法
        int start = length - MAX_VALUE_LENGTH;
        if(start < 0) start = 0;

        //如果没有找到也不合法
        Matcher matcher = s_fragmentPattern.matcher(charSequence.subSequence(start,length));
        if(!matcher.find()) return -1;

        //找到了
        return length - (matcher.end() - matcher.start());
    }
}
