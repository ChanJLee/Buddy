package com.chan.buddy.app;

import android.app.Application;
import android.content.Context;

import com.chan.buddy.utility.StorageUtility;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

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
        init();
    }

    /**
     * 创建应用要用的文件夹
     */
    private void initStorage(){
        if(StorageUtility.hasSDCard()){
            StorageUtility.initStorageUtility();
        }
    }

    private void init(){
        initStorage();
        initImageLoader();
    }

    private void initImageLoader(){

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs();
        // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    /**
     * @return 获得Buddy这个应用的全局上下文
     */
    public static Context getBuddyApplicationContext(){
        return s_context;
    }
}
