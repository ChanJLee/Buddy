package com.chan.buddy.surprise.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chan.buddy.surprise.adapter.PopupListViewAdapter;

/**
 * Created by chan on 15-9-3.
 */
public class SelectDirListView extends ListView {
    public SelectDirListView(Context context) {
        super(context);
        init();
    }

    public SelectDirListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectDirListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SelectDirListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        super.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PopupListViewAdapter.ViewHolder viewHolder =
                        (PopupListViewAdapter.ViewHolder) view.getTag();

                viewHolder.m_tagView.setVisibility(VISIBLE);
                final int count = getChildCount();

                for (int i = 0; i < count; ++i) {
                    if (i == position) continue;
                    PopupListViewAdapter.ViewHolder holder =
                            (PopupListViewAdapter.ViewHolder) getChildAt(i).getTag();
                    holder.m_tagView.setVisibility(GONE);
                }

                if(m_onDirSelected != null)
                    m_onDirSelected.onDirSelected(viewHolder.m_dir);
            }
        });
    }

    /**
     * 当选中一个目录的时候触发
     */
    public interface OnDirSelected{
        void onDirSelected(String dirName);
    }

    public void setOnDirSelected(OnDirSelected onDirSelected) {
        m_onDirSelected = onDirSelected;
    }

    private OnDirSelected m_onDirSelected;
}
