package com.chan.buddy.surprise.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.chan.buddy.R;
import com.chan.buddy.surprise.adapter.GridLayoutAdapter;

/**
 * Created by chan on 15-9-3.
 */
public class ImageGridView extends GridView {

    private OnImageItemClick m_onImageItemClick;

    private OnCameraItemClick m_onCameraItemClick;
    private GridLayoutAdapter.ViewHolder m_prevViewHolder;

    public ImageGridView(Context context) {
        super(context);
        init();
    }

    public ImageGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        super.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GridLayoutAdapter.ViewHolder viewHolder =
                        (GridLayoutAdapter.ViewHolder) view.getTag();

                //如果是相机按下了
                if (position == 0) {
                    if (m_onCameraItemClick != null)
                        m_onCameraItemClick.onCameraItemClick();
                    return;
                }

                //剩下的都是点击图片
                //如果之前已经选过当前的项目
                boolean hasSelected = m_prevViewHolder == viewHolder;
                m_prevViewHolder = viewHolder;

                final int selectedId = R.drawable.has_select;
                final int notSelectedId = R.drawable.no_select;

                viewHolder.m_selectTag.setImageResource(
                        hasSelected ? notSelectedId : selectedId
                );

                //既然之前选过 那么这次就是取消选择 所以把标记也要滞空
                if(hasSelected) m_prevViewHolder = null;

                final int count = getChildCount();

                for (int i = 0; i < count; ++i) {
                    if (i == position) continue;
                    GridLayoutAdapter.ViewHolder holder =
                            (GridLayoutAdapter.ViewHolder) getChildAt(i).getTag();
                    holder.m_selectTag.setImageResource(notSelectedId);
                }

                if (m_onImageItemClick != null)
                    m_onImageItemClick.onImageItemClick(hasSelected ? null : viewHolder.m_fileName);
            }
        });
    }

    public void setOnImageItemClick(OnImageItemClick onImageItemClick) {
        m_onImageItemClick = onImageItemClick;
    }

    public void setOnCameraItemClick(OnCameraItemClick onCameraItemClick) {
        m_onCameraItemClick = onCameraItemClick;
    }

    public interface OnImageItemClick{
        /**
         * @param fileName 所选图片对应的文件名
         */
        void onImageItemClick(String fileName);
    }

    public interface OnCameraItemClick{
        void onCameraItemClick();
    }
}
