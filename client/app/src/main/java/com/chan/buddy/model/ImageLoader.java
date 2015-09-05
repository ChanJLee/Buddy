package com.chan.buddy.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.chan.buddy.bean.ImageFolder;
import com.chan.buddy.utility.ImageUtility;

/**
 * Created by chan on 15-9-3.
 * 图片加载器
 */
public class ImageLoader {
    private LruCache<String,Bitmap> m_bitmapLruCache;
    private BitmapFactory.Options m_options;
    private int m_width;
    private int m_height;

    /**
     * @param width  用于展示图片部件的宽
     * @param height 高
     * @param maxMemorySize 允许使用的最大内存 -1 代表使用默认的配置
     */
    public ImageLoader(int width, int height, int maxMemorySize){

        if(maxMemorySize == -1){
            maxMemorySize = (int) (Runtime.getRuntime().maxMemory() / 8);
        }
        m_bitmapLruCache = new LruCache<String,Bitmap>(maxMemorySize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

        m_options = new BitmapFactory.Options();
        m_width = width;
        m_height = height;
    }

    public Bitmap getBitmap(String fileName){
        Bitmap result = m_bitmapLruCache.get(fileName);
        if(result == null){
            result = releaseBitmap(fileName);
        }
        return result;
    }

    public Bitmap releaseBitmap(String fileName){

        m_options.inJustDecodeBounds = true;
        m_options.inSampleSize = ImageUtility.calculateInSampleSize(m_options,m_width,m_height);

        m_options.inJustDecodeBounds = false;
        Bitmap result = BitmapFactory.decodeFile(fileName,m_options);
        return result;
    }
}
