package com.chan.buddy.surprise.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chan.buddy.R;
import com.chan.buddy.adapter.UniversalImageLoadAdapter;
import com.chan.buddy.bean.ImageFolder;

import java.util.List;

/**
 * Created by chan on 15-9-2.
 */
public class PopupListViewAdapter extends UniversalImageLoadAdapter{
    private List<ImageFolder> m_imageFolders;

    public PopupListViewAdapter(Context context, List<ImageFolder> imageFolders) {
        super(context);
        m_imageFolders = imageFolders;
    }

    @Override
    public int getCount() {
        return m_imageFolders.size();
    }

    @Override
    public Object getItem(int position) {
        return m_imageFolders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageFolder imageFolder = m_imageFolders.get(position);
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = createView(R.layout.select_more_item, parent,false);
            viewHolder = new ViewHolder();
            viewHolder.m_preImageView = (ImageView) convertView.findViewById(R.id.id_preview);
            viewHolder.m_countTextView = (TextView) convertView.findViewById(R.id.id_file_count);
            viewHolder.m_titleTextView = (TextView) convertView.findViewById(R.id.id_dir_name);
            viewHolder.m_tagView = convertView.findViewById(R.id.id_accept_tag);
            viewHolder.m_dir = imageFolder.getDir();
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //viewHolder.m_preImageView.setImageBitmap(getBitmap(imageFolder.getFirstImage()));

        displayBitmap(imageFolder.getFirstImage(),viewHolder.m_preImageView);
        String chunks[] = imageFolder.getDir().split("/");
        viewHolder.m_titleTextView.setText(chunks[chunks.length - 1]);
        viewHolder.m_countTextView.setText("共" + imageFolder.getCount() + "张");
        viewHolder.m_tagView.setVisibility(position == 0 ? View.VISIBLE : View.INVISIBLE);

        return convertView;
    }

    static public class ViewHolder {
        public ImageView m_preImageView;
        public TextView m_titleTextView;
        public TextView m_countTextView;
        public View m_tagView;
        public String m_dir;
    }
}
