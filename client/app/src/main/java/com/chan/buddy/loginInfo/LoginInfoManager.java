package com.chan.buddy.loginInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * Created by chan on 15-9-16.
 */
public class LoginInfoManager {
    /**
     * 存放在沙箱中的文件名
     */
    private static final String FILE_NAME = "buddy_info";
    /**
     * 最后一次成功登录的节点名
     */
    private static final String LAST_INFO = "success";

    /**
     * 获得最近一次成功登录的人
     * @param context
     * @return 最近一次登录人的账号
     */
    static public String getLastedUserName(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                FILE_NAME,
                Context.MODE_PRIVATE
        );
        return sharedPreferences.getString(LAST_INFO,"");
    }

    static public String getLastedNickName(@NonNull Context context,String userName){

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                FILE_NAME,
                Context.MODE_PRIVATE
        );

        String content = sharedPreferences.getString(userName, "");
        if (TextUtils.isEmpty(content)) return "";
        return content.split(SPLIT_STRING)[1];
    }

    /**
     * 根据已经登录成功的账号 获得上次它登陆时记录的密码
     * @param userName
     * @return
     */
    static public String getRecordedPassWord(@NonNull Context context, @NonNull String userName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                FILE_NAME,
                Context.MODE_PRIVATE
        );

        String content = sharedPreferences.getString(userName, "");
        if (TextUtils.isEmpty(content)) return "";
        return content.split(SPLIT_STRING)[0];
    }

    private static final String SPLIT_STRING = " ";

    /**
     * 记录最近成功登录的账号和密码
     * @param context 上下文
     * @param userName 账号
     * @param passWord 密码
     * @param nickName 昵称
     */
    static public void recordLastInfo(@NonNull Context context, String userName,String passWord,String nickName) {

        //获得编辑对象
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                FILE_NAME,
                Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(LAST_INFO, userName);
        //提交修改
        String content = passWord + SPLIT_STRING + nickName;
        editor.putString(userName, content);
        editor.commit();
    }
}
