package com.chan.buddy.service;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyInfo;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.chan.buddy.app.BuddyApplication;
import com.chan.buddy.loginInfo.LoginInfoManager;

import java.util.List;

/**
 * Created by chan on 15-9-20.
 */
public class RadarEngine implements RadarSearchListener{
    ////////////////////////////////////////////////////////////////////////////////////////////
    private RadarSearchManager m_radarSearchManager;
    private OnNearbyResult m_onNearbyResult;
    private String m_userName;
    private String m_nickName;
    private Context m_context;
    /// /////////////////////////////////////////////////////////////////////////////////////////

    public RadarEngine(@NonNull Context context){
        m_context = context;

        //获得用户Id 并设置
        m_userName = LoginInfoManager.getLastedUserName(
                BuddyApplication.getBuddyApplicationContext()
        );
        m_nickName = LoginInfoManager.getLastedNickName(
                BuddyApplication.getBuddyApplicationContext(),
                m_userName
        );

        m_radarSearchManager = RadarSearchManager.getInstance();
        m_radarSearchManager.addNearbyInfoListener(this);
        m_radarSearchManager.setUserID(m_userName);
    }

    public void setOnNearbyResult(OnNearbyResult onNearbyResult) {
        m_onNearbyResult = onNearbyResult;
    }

    public void getNearPeople(LatLng point){

        //上传自己的位置信息
        RadarUploadInfo info = new RadarUploadInfo();
        info.comments = m_nickName;
        info.pt = point;
        m_radarSearchManager.uploadInfoRequest(info);

        //构造请求参数，其中centerPt是自己的位置坐标
        m_radarNearbySearchOption = new RadarNearbySearchOption()
                .centerPt(point).pageNum(0).radius(2000).pageCapacity(10);
    }

    private RadarNearbySearchOption m_radarNearbySearchOption;

    @Override
    public void onGetNearbyInfoList(RadarNearbyResult radarNearbyResult,
                                    RadarSearchError radarSearchError) {
        final Context context = getContext();

        if(radarSearchError == RadarSearchError.RADAR_NO_ERROR){
            Toast.makeText(
                    context,
                    "获取周边成功",
                    Toast.LENGTH_SHORT).show();
            handleNearbyResult(radarNearbyResult);
        }else{
            Toast.makeText(
                    context,
                    "获取周边失败",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void handleNearbyResult(RadarNearbyResult radarNearbyResult) {
        if (m_onNearbyResult != null)
            m_onNearbyResult.onNearbyResult(radarNearbyResult.infoList);
    }

    public interface OnNearbyResult{
        void onNearbyResult(List<RadarNearbyInfo> radarNearbyInfo);
    }

    @Override
    public void onGetUploadState(RadarSearchError radarSearchError) {
        final Context context = getContext();

        // TODO Auto-generated method stub
        if (radarSearchError == RadarSearchError.RADAR_NO_ERROR) {
            Toast.makeText(context, "单次上传位置成功", Toast.LENGTH_SHORT)
                    .show();
            m_radarSearchManager.nearbyInfoRequest(m_radarNearbySearchOption);
        } else {
            Toast.makeText(context, "单次上传位置失败", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onGetClearInfoState(RadarSearchError radarSearchError) {
        final Context context = getContext();
        // TODO Auto-generated method stub
        if (radarSearchError == RadarSearchError.RADAR_NO_ERROR) {
            Toast.makeText(context, "清除位置失败", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(context, "清除位置失败", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public Context getContext(){
        return m_context;
    }

    /**
     * 清除我的位置信息
     */
    private void clearUserInfo() {
        if (m_radarSearchManager != null)
            m_radarSearchManager.clearUserInfo();
    }

    public void destroy() {
        if (m_radarSearchManager != null) {
            clearUserInfo();
            m_radarSearchManager.removeNearbyInfoListener(this);
            m_radarSearchManager.destroy();
        }
    }
}
