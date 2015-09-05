package com.chan.buddy.surprise.views;

import android.content.Context;
import android.view.View;
import android.widget.PopupWindow;

import com.chan.buddy.R;
import com.chan.buddy.bean.ImageFolder;
import com.chan.buddy.surprise.adapter.PopupListViewAdapter;

import java.util.List;

/**
 * Created by chan on 15-9-3.
 */
public class SelectDirPopupWindow extends PopupWindow {
    private Context m_context;

    public SelectDirPopupWindow(Context context
            ,int width, int height, boolean focusable,
                                List<ImageFolder> imageFolders) {
        super(width, height);
        setFocusable(focusable);
        init(context, imageFolders);
    }

    private void init(Context context,List<ImageFolder> imageFolders){
        initContext(context);
        initContentView(imageFolders);
    }

    private void initContext(Context context){
        m_context = context;
    }

    private void initContentView(List<ImageFolder> imageFolders) {
        View root = View.inflate(m_context, R.layout.select_more_pop_window, null);
        SelectDirListView listView = (SelectDirListView) root.findViewById(R.id.id_list_view);
        listView.setAdapter(new PopupListViewAdapter(m_context,imageFolders));
        listView.setOnDirSelected(new SelectDirListView.OnDirSelected() {
            @Override
            public void onDirSelected(String dirName) {
                if ((m_onDirectionSelected != null))
                    m_onDirectionSelected.onDirectionSelected(dirName);
                dismiss();
            }
        });
        setContentView(root);
    }

    public interface OnDirectionSelected{
        void onDirectionSelected(String dirName);
    }

    public void setOnDirectionSelected(OnDirectionSelected onDirectionSelected) {
        m_onDirectionSelected = onDirectionSelected;
    }

    private OnDirectionSelected m_onDirectionSelected;
}
