package com.chan.buddy.utility;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by chan on 15-8-26.
 * 存储工具 管理文件系统
 */
public class StorageUtility {

    private static final String VIDEO_TEMP_DIR = "/temp/video/";
    private static final String APP_DIR = "chan/Buddy";
    private static File BASE_DIR;
    private static File BASE_TEMP_DIR;

    /**
     * 初始化存储工具
     */
    static public void initStorageUtility() {

        File dir = hasSDCard() ? Environment.getExternalStorageDirectory()
                : Environment.getDataDirectory();

        BASE_DIR = new File(dir, APP_DIR);
        if (!BASE_DIR.exists()) BASE_DIR.mkdirs();
        BASE_TEMP_DIR = new File(BASE_DIR, VIDEO_TEMP_DIR);
        if (!BASE_TEMP_DIR.exists()) BASE_TEMP_DIR.mkdirs();
    }

    /**
     * 回收临时文件
     */
    static public void recycleTempFiles() {
        File file = new File(BASE_DIR + "temp");
        file.delete();
    }

    /** 是否有sd 卡
     * @return true 有 false 没有
     */
    static public boolean hasSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * @return 视频文件的临时文件
     */
    static public File getVideoTempFile() {
        return new File(BASE_TEMP_DIR, "video.3gp");
    }
}
