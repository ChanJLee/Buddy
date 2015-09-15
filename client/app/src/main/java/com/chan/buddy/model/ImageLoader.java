package com.chan.buddy.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.LruCache;
import android.widget.ImageView;

import com.chan.buddy.R;
import com.chan.buddy.bean.ImageFolder;
import com.chan.buddy.utility.ImageUtility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.File;
import java.net.URI;

/**
 * Created by chan on 15-9-3.
 * 图片加载器
 */
public class ImageLoader {

    private DisplayImageOptions m_displayImageOptions;

    public ImageLoader() {

        m_displayImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_image)
                .showImageForEmptyUri(R.drawable.default_image)
                .showImageOnFail(R.drawable.default_image)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public void displayImage(String fileName,ImageView imageView) {
        com.nostra13.universalimageloader.core.ImageLoader imageLoader =
                com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        Uri uri = Uri.fromFile(new File(fileName));
        imageLoader.displayImage(uri.toString(), imageView, m_displayImageOptions);
    }
}
