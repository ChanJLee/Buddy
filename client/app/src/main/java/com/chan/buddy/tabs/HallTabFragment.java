package com.chan.buddy.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chan.buddy.R;

/**
 * Created by chan on 15-8-21.
 * 大厅主界面
 */
public class HallTabFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater layoutInflater,ViewGroup viewGroup,Bundle bundle){
        View contentView = layoutInflater.inflate(R.layout.hall_tag,viewGroup,false);
        return contentView;
    }
}
