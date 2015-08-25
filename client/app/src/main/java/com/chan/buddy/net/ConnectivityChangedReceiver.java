package com.chan.buddy.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

/**
 * Created by chan on 15-8-21.
 * 检测网络变化的接收器
 */
public class ConnectivityChangedReceiver extends BroadcastReceiver {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String NOTICE_NO_CONNECTIVITY  = "网络已断开";
    private static final String NOTICE_HAS_CONNECTIVITY = "网络已连接";
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false))
            Toast.makeText(context, NOTICE_NO_CONNECTIVITY, Toast.LENGTH_SHORT).show();
        else Toast.makeText(context, NOTICE_HAS_CONNECTIVITY, Toast.LENGTH_SHORT).show();
    }
}
