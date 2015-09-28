package com.chan.buddy.surprise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyInfo;
import com.chan.buddy.R;
import com.chan.buddy.menu.ContextMenuDialogFragment;
import com.chan.buddy.menu.MenuObject;
import com.chan.buddy.menu.MenuParams;
import com.chan.buddy.menu.interfaces.OnMenuItemClickListener;
import com.chan.buddy.menu.interfaces.OnMenuItemLongClickListener;
import com.chan.buddy.service.RadarEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chan on 15-9-19.
 */
public class PeopleNearlyActivity extends FragmentActivity
        implements BDLocationListener,
        RadarEngine.OnNearbyResult,
        View.OnClickListener,BaiduMap.OnMarkerClickListener,
        OnMenuItemClickListener ,OnMenuItemLongClickListener{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final String TAG_CONTEXT_FRAGMENT = "ayu";
    private static final String EXTRA_USER_NAME = "youAreMySunshine";
    private static final int MENU_CLOSE = 0;
    private static final int MENU_SEND = 1;
    private static final int MENU_MK_FRIEND = 2;
    private static final int MENU_FOLLOW = 3;
    private static final int MENU_BLOCK = 4;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private BaiduMap m_baiduMap;
    private MapView m_mapView;
    private LocationClient m_locationClient;
    private boolean m_isFirstIn = true;
    private RadarEngine m_radarEngine;
    private BitmapDescriptor m_bitmapDescriptorB =
            BitmapDescriptorFactory.fromResource(R.drawable.icon_markb);
    private ContextMenuDialogFragment m_contextMenuDialogFragment;
    private List<MenuObject> m_menuObjects;
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.people_nearly_layout);
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        initRadar();
        initBaiDuMap();
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView(){
        findViewById(R.id.id_back_image_view)
                .setOnClickListener(this);
        TextView textView = (TextView) findViewById(R.id.id_title_text_view);
        textView.setText("附近的人");
    }

    /**
     * 初始化引擎
     */
    private void initRadar(){
        m_radarEngine = new RadarEngine(this);
        m_radarEngine.setOnNearbyResult(this);
    }

    /**
     * 初始化百度地图
     */
    private void initBaiDuMap(){
        m_mapView = (MapView) findViewById(R.id.id_bai_du_map);
        m_baiduMap = m_mapView.getMap();

        m_baiduMap.setOnMarkerClickListener(this);

        m_baiduMap.setMyLocationEnabled(true);
        m_locationClient = new LocationClient(this);
        m_locationClient.registerLocationListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_mapView.onResume();
        startLocation();
    }

    /**
     * 开始定位
     */
    private void startLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setScanSpan(0);
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        m_locationClient.setLocOption(option);
        m_locationClient.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_mapView.onDestroy();
        m_radarEngine.destroy();
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if(!m_isFirstIn) return;

        m_isFirstIn = false;
        final double latitude = bdLocation.getLatitude();
        final double longitude = bdLocation.getLongitude();

        //找到我当前的位置
        LatLng point = new LatLng(latitude, longitude);
        //查找附近的人
        getNearlyPeople(point);

        //定义地图状态
        MapStatus mapStatus = new MapStatus.Builder()
                .target(point)
                .zoom(18)
                .build();

        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        //改变地图状态
        m_baiduMap.setMapStatus(mapStatusUpdate);

        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(2000)// 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(latitude)
                .longitude(longitude).build();
        m_baiduMap.setMyLocationData(locData);
    }

    /**
     * @param context
     * @return
     */
    public static Intent getIntent(@NonNull Context context) {
        return new Intent(context, PeopleNearlyActivity.class);
    }

    /**
     * 获得附近的人
     * @param point 自己的坐标
     */
    private void getNearlyPeople(LatLng point) {
        m_radarEngine.getNearPeople(point);
    }

    /**
     * 附近的人回调接口
     * @param radarNearbyInfo 附近的人的信息
     */
    @Override
    public void onNearbyResult(List<RadarNearbyInfo> radarNearbyInfo) {
        final int size = radarNearbyInfo.size();
        for (int i = 0; i < size; ++i) {

            RadarNearbyInfo info = radarNearbyInfo.get(i);
            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_USER_NAME,info.userID);

            //图标用来显示我的位置
            OverlayOptions options = new MarkerOptions()
                    .icon(m_bitmapDescriptorB)
                    .position(info.pt)
                    .title(info.comments)
                    .extraInfo(bundle);

            m_baiduMap.addOverlay(options);
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id){
            case R.id.id_back_image_view:
                onBackClick();
                break;
            default:break;
        }
    }

    /**
     * 当返回按钮按下时的操作
     */
    private void onBackClick(){
        finish();
    }

    /**
     * @param marker 被点中的标记
     * @return true 代表消耗事件 false 与之相反
     */
    @Override
    public boolean onMarkerClick(Marker marker) {

        final Bundle bundle = marker.getExtraInfo();
        final String userName = bundle.getString(EXTRA_USER_NAME, "");
        if (TextUtils.isEmpty(userName))
            return false;
        showSelectMenu();
        return true;
    }

    /**
     * 显示菜单
     */
    private void showSelectMenu() {
        if (m_contextMenuDialogFragment == null)
            releaseContextMenu();

        FragmentManager fragmentManager = getSupportFragmentManager();
        m_contextMenuDialogFragment.show(fragmentManager, TAG_CONTEXT_FRAGMENT);
    }

    /**
     * 取消菜单
     */
    private void dismissSelectMenu(){
        if(m_contextMenuDialogFragment != null &&
                m_contextMenuDialogFragment.isAdded()){
            m_contextMenuDialogFragment.dismiss();
        }
    }

    /**
     * 生成菜单
     */
    private void releaseContextMenu(){
        m_menuObjects = releaseMenuObjects();
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(m_menuObjects);
        menuParams.setClosableOutside(false);
        m_contextMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
    }

    /**
     * 生成菜单项
     * @return
     */
    private List<MenuObject> releaseMenuObjects() {

        List<MenuObject> menuObjects = new ArrayList<MenuObject>();

        MenuObject close = new MenuObject("关闭");
        close.setResource(R.drawable.icn_close);

        MenuObject send = new MenuObject("打招呼");
        send.setResource(R.drawable.icn_1);

        MenuObject addFr = new MenuObject("加好友");
        addFr.setResource(R.drawable.icn_3);

        MenuObject addFav = new MenuObject("关注");
        addFav.setResource(R.drawable.icn_4);

        MenuObject block = new MenuObject("拉黑");
        block.setResource(R.drawable.icn_5);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(addFr);
        menuObjects.add(addFav);
        menuObjects.add(block);
        return menuObjects;
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {
        switch (position) {
            case MENU_CLOSE:
                break;
            case MENU_SEND:
                break;
            case MENU_FOLLOW:
                break;
            case MENU_MK_FRIEND:
                break;
            case MENU_BLOCK:
                break;
            default:
                break;
        }
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {}
}