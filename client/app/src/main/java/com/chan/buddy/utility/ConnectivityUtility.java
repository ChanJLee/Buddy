package com.chan.buddy.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

/**
 * Created by chan on 15-8-20.
 * 连接工具 用于检测网络状态
 */
public class ConnectivityUtility {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     *  连接类型为wifi
     */
    public static final short TYPE_WIFI = 0x0521;
    /**
     *  连接类型为移动网
     */
    public static final short TYPE_MOBILE = 0x0525;
    /**
     *  连接类型为其它
     */
    public static final short TYPE_OTHER = 0x000f;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static ConnectivityManager s_connectivityManager = null;
    private static final String BASE_ADDRESS = "http://192.168.1.220:8080/";
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param context 上下文
     * @return 返回网络是否连接 true 代表连接  false 代表断开
     */
    public static boolean isConnected(@NonNull Context context){
        ConnectivityManager connectivityManager = getConnectivityManager(context);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo.isConnected();
    }

    /**
     * @param context 上下文
     * @return 网络的类型 参考
     * {@link ConnectivityUtility#TYPE_WIFI}
     * {@link ConnectivityUtility#TYPE_MOBILE}
     * {@link ConnectivityUtility#TYPE_OTHER}
     */
    public static short getConnectivityType(@NonNull Context context){
        ConnectivityManager connectivityManager = getConnectivityManager(context);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        final int type = networkInfo.getType();

        switch (type){
            case ConnectivityManager.TYPE_MOBILE:
                return TYPE_MOBILE;
            case ConnectivityManager.TYPE_WIFI:
                return TYPE_WIFI;
            default: return TYPE_OTHER;
        }
    }

    /**
     * @param context 上下文
     * @return 连接管理器
     */
    private static ConnectivityManager getConnectivityManager(@NonNull Context context){
        if(s_connectivityManager == null)
            s_connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return s_connectivityManager;
    }

    /**
     * @return 获得登录服务器的地址
     */
    public static String getSignInHost(){
        return BASE_ADDRESS + "signIn";
    }

    /**
     * @return 获得注册服务器的地址
     */
    public static String getSignUpHost(){
        return BASE_ADDRESS + "signUp";
    }
}
