package com.chan.buddy.app;

import android.app.Application;
import android.content.Context;

import com.chan.buddy.utility.StorageUtility;

/**
 * Created by chan on 15-8-20.
 * 用于初始化应用
 */
public class BuddyApplication extends Application {
    private static Context s_context;

    @Override
    public void onCreate(){
        super.onCreate();
        s_context = this;
        initStorage();
    }

    /**
     * 创建应用要用的文件夹
     */
    private void initStorage(){
        if(StorageUtility.hasSDCard()){
            StorageUtility.initStorageUtility();
        }
    }

    /**
     * @return 获得Buddy这个应用的全局上下文
     */
    public static Context getBuddyApplicationContext(){
        return s_context;
    }
}
