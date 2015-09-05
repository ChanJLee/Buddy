package com.chan.buddy.utility;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by chan on 15-8-26.
 * 存储工具 管理文件系统
 */
public class StorageUtility {

    private static final String VIDEO_TEMP_DIR = "/temp/video/";
    private static final String AUDIO_TEMP_DIR = "/temp/audio/";
    private static final String IMAGE_TEMP_DIR = "/temp/image/";
    private static final String APP_DIR = "/chan/Buddy";
    private static File BASE_DIR;
    private static File BASE_TEMP_DIR;
    private static File BASE_AUDIO_TEMP_DIR;
    private static File BASE_IMAGE_TEMP_DIR;

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
        BASE_AUDIO_TEMP_DIR = new File(BASE_DIR, AUDIO_TEMP_DIR);
        if (!BASE_AUDIO_TEMP_DIR.exists()) BASE_AUDIO_TEMP_DIR.mkdirs();
        BASE_IMAGE_TEMP_DIR = new File(BASE_DIR, IMAGE_TEMP_DIR);
        if (!BASE_IMAGE_TEMP_DIR.exists()) BASE_IMAGE_TEMP_DIR.mkdirs();
    }

    /**
     * 回收临时文件
     */
    static public void recycleTempFiles() {
        File file = new File(BASE_DIR + "temp");
        file.delete();
    }

    /**
     * 是否有sd 卡
     *
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

    static public File getAudioTempFile() {
        return new File(BASE_AUDIO_TEMP_DIR, "audio.3gp");
    }

    static public File getImageTempFile() {
        //File file = new File(BASE_IMAGE_TEMP_DIR, "image.jpg");
        File file = new File(Environment.getExternalStorageDirectory(), "demo.jpg");
        return file;
    }
}
