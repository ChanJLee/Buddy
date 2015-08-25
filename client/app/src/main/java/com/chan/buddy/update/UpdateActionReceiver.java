package com.chan.buddy.update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by chan on 15-8-21.
 * 用于处理更新应用的广播接收器
 */
public class UpdateActionReceiver extends BroadcastReceiver {

    /**
     * 获得当前下载的reference
     */
    public static final String ACTION_DOWNLOAD_REFERENCE = "com.chan.action.DOWNLOAD_REFERENCE";

    /**
     *  获得下载的reference
     */
    public static final String EXTRA_REFERENCE = "download_reference";

    /**
     * 下载的reference
     */
    private long m_reference;

    @Override
    public void onReceive(Context context,Intent intent){

        if(ACTION_DOWNLOAD_REFERENCE.equals(intent.getAction())){
            m_reference = intent.getLongExtra(EXTRA_REFERENCE,-1);
        }else if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())){
            onDownloadComplete();
        }
    }

    private void onDownloadComplete(){

    }
}
