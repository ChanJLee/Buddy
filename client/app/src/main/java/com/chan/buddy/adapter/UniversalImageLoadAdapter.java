package com.chan.buddy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.chan.buddy.model.ImageLoader;

/**
 * Created by chan on 15-9-3.
 * 通用图片加载适配器 对需要加载大量图片的控件提供优化
 *
 * 他不再和谁谈论相逢的孤岛 因为心里早已荒无人烟
 * 他的心里再也装不下一个家 做一个只对自己说谎的哑巴
 * 他说你任何为人称道的美丽 不及他第一次遇见你
 * 时光苟延残喘无可奈何
 * 如果所有土地连在一起 走上一生只为拥抱你
 * 喝醉了 他的梦 晚安
 */
abstract public class UniversalImageLoadAdapter extends BaseAdapter{

    private ImageLoader m_imageLoader;
    private LayoutInflater m_layoutInflater;
    private Context m_context;
    /**
     * @param context 上下文
     */
    public UniversalImageLoadAdapter(Context context){
        m_context = context;
        m_imageLoader = new ImageLoader();
        m_layoutInflater = LayoutInflater.from(context);
    }

    protected View createView(int layoutId,ViewGroup parent,boolean attachToRoot) {
        return m_layoutInflater.inflate(layoutId, parent, attachToRoot);
    }

    protected void displayBitmap(String fileName,ImageView imageView) {
        m_imageLoader.displayImage(fileName, imageView);
    }

    protected Context getContext() {
        return m_context;
    }
}
