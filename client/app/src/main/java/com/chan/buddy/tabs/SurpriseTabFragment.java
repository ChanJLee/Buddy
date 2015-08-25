package com.chan.buddy.tabs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chan.buddy.R;
import com.chan.buddy.surprise.SendMessageActivity;

/**
 * Created by chan on 15-8-21.
 * 发现主界面
 */
public class SurpriseTabFragment extends Fragment
    implements View.OnClickListener{
    @Override
    public View onCreateView(LayoutInflater layoutInflater,ViewGroup viewGroup,Bundle bundle) {
        View contentView = layoutInflater.inflate(R.layout.surprise_tag, viewGroup, false);
        initView(contentView);
        return contentView;
    }

    /**
     * 初始化视图
     * @param view 显示在fragment中的视图
     */
    private void initView(@NonNull View view){

        view.findViewById(R.id.id_surprise_item_send_message).setOnClickListener(this);
        view.findViewById(R.id.id_surprise_item_scan).setOnClickListener(this);
        view.findViewById(R.id.id_surprise_item_shake).setOnClickListener(this);
        view.findViewById(R.id.id_surprise_item_nearby).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        final int id = v.getId();

        switch (id) {
            case R.id.id_surprise_item_send_message:
                onClickSendMessage();
                break;
            case R.id.id_surprise_item_scan:
                onClickScan();
                break;
            case R.id.id_surprise_item_shake:
                onClickShake();
                break;
            case R.id.id_surprise_item_nearby:
                onClickNearby();
                break;
            default:
                break;
        }
    }

    /**
     * 用于响应点击发表说说的按钮
     */
    private void onClickSendMessage() {
        startActivity(SendMessageActivity.getIntent(getContext()));
    }

    /**
     * 用于响应点击扫一扫按钮
     */
    private void onClickScan(){

    }

    /**
     * 用于响应点击摇一摇
     */
    private void onClickShake(){

    }

    /**
     * 用于响应点击附近的人
     */
    private void onClickNearby(){

    }
}
