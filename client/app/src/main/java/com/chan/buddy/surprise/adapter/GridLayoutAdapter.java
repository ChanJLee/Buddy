package com.chan.buddy.surprise.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chan.buddy.R;
import com.chan.buddy.adapter.UniversalImageLoadAdapter;

import java.io.File;

/**
 * Created by chan on 15-9-1.
 */
public class GridLayoutAdapter extends UniversalImageLoadAdapter {

    private String m_dir;
    private String[] m_items;

    public GridLayoutAdapter(Context context,String dir, String[] items,int width) {
        super(context);
        m_dir = dir;
        m_items = items;
    }

    @Override
    public int getCount() {
        return m_items.length + 1;
    }

    @Override
    public Object getItem(int position) {
        return position == 0 ? "" : m_items[position - 1];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if(convertView == null) {
            convertView = createView(R.layout.select_image_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.m_content = (ImageView) convertView.findViewById(R.id.id_image_item);
            viewHolder.m_selectTag = (ImageView) convertView.findViewById(R.id.id_select_tag);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(position == 0){
            viewHolder.m_fileName = "";
            viewHolder.m_content.setScaleType(ImageView.ScaleType.CENTER);
            viewHolder.m_content.setImageResource(R.drawable.ic_camera_alt_grey600_48dp);
            viewHolder.m_selectTag.setVisibility(View.GONE);
            return convertView;
        }

        viewHolder.m_fileName = m_dir + File.separator + m_items[position - 1];
        viewHolder.m_content.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder.m_selectTag.setVisibility(View.VISIBLE);
        displayBitmap(viewHolder.m_fileName,viewHolder.m_content);
        return convertView;
    }

    /**
     * 用于优化adapter
     */
    public static class ViewHolder{
        /**
         * 内容
         */
        public ImageView m_content;
        /**
         * 是否选择
         */
        public ImageView m_selectTag;
        /**
         * 关联的文件名
         */
        public String m_fileName;
    }
}