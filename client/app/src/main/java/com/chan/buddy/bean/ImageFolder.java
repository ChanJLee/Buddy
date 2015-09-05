package com.chan.buddy.bean;

/**
 * Created by chan on 15-9-1.
 */
public class ImageFolder {
    private String m_dir;
    private String m_firstImage;
    private int m_count = 1;

    public ImageFolder(String dir, String firstImage) {
        m_dir = dir;
        m_firstImage = firstImage;
    }

    public String getDir() {
        return m_dir;
    }

    public String getFirstImage() {
        return m_firstImage;
    }

    public void setDir(String dir) {
        m_dir = dir;
    }

    public void setFirstImage(String firstImage) {
        m_firstImage = firstImage;
    }

    public int getCount() {
        return m_count;
    }

    public void setCount(int count) {
        m_count = count;
    }
}
