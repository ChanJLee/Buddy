package com.chan.buddy.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chan.buddy.R;
import com.chan.buddy.profile.ProfileActivity;
import com.chan.buddy.profile.SettingActivity;

/**
 * Created by chan on 15-8-21.
 * 我 主界面
 */
public class ProfileTabFragment extends Fragment implements View.OnClickListener{

    @Override
    public View onCreateView(LayoutInflater layoutInflater,ViewGroup viewGroup,Bundle bundle){
        View contentView = layoutInflater.inflate(
                R.layout.profile_tag,
                viewGroup,
                false
        );

        init(contentView);
        return contentView;
    }

    /**
     * 初始化视图
     * @param contentView
     */
    private void init(View contentView){
        contentView.findViewById(R.id.id_profile_item_avatar)
                .setOnClickListener(this);
        contentView.findViewById(R.id.id_set)
                .setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id){
            case R.id.id_profile_item_avatar:
                onAvatarClick();
                break;
            case R.id.id_set:
                onSetClick();
                break;
            default: break;
        }
    }

    private void onSetClick() {
        startActivity(SettingActivity.getIntent(getContext()));
    }

    /**
     * 当设置资料的时候触发
     */
    private void onAvatarClick(){
        Intent intent = ProfileActivity.getIntent(getContext());
        startActivity(intent);
    }
}
