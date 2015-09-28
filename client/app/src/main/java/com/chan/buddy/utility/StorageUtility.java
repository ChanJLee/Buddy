package com.chan.buddy.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.chan.buddy.R;
import com.chan.buddy.app.BuddyApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

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
        File file = new File(BASE_IMAGE_TEMP_DIR, "image.jpg");
        return file;
    }

    /**
     * 记录某个账户的头像缓存
     *
     * @param id     用户名 且必须唯一
     * @param bitmap 头像
     * @return 头像存储的位置
     */
    static public String recordAvatar(String id, Bitmap bitmap) {
        File file = new File(BASE_IMAGE_TEMP_DIR, id + ".jpeg");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(os.toByteArray());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            return null;
        }
        return file.getAbsolutePath();
    }

    static public String defaultAvatar(String id) {
        File file = new File(BASE_DIR, id + ".jpeg");

        if(!file.exists()){
            Context context = BuddyApplication.getBuddyApplicationContext();
            InputStream is = context.getResources()
                    .openRawResource(R.raw.ic_account_circle_grey600_48dp);
            FileOutputStream os = null;
            try {
                os = new FileOutputStream(file);
                int count = -1;
                byte bytes[] = new byte[256];

                while ((count = is.read(bytes)) != -1) {
                    os.write(bytes, 0, count);
                }
            } catch (Exception e){
                return null;
            }finally {
                try {
                    if (os != null)
                        os.close();
                    if (is != null)
                        is.close();
                } catch (Exception e) {}
            }
        }
        return file.getAbsolutePath();
    }
}
