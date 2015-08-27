package com.chan.buddy.main;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.chan.buddy.R;
import com.chan.buddy.tabs.FollowTabFragment;
import com.chan.buddy.tabs.HallTabFragment;
import com.chan.buddy.tabs.ProfileTabFragment;
import com.chan.buddy.tabs.SurpriseTabFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chan on 15-8-21.
 * 软件主界面
 */
public class MainInterfaceActivity
        extends FragmentActivity
        implements View.OnClickListener {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final short CELL_COUNT       = 4;
    private static final short CELL_HALL        = 0;
    private static final short CELL_FOLLOW      = 1;
    private static final short CELL_SURPRISE    = 2;
    private static final short CELL_PROFILE     = 3;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private ViewPager m_viewPager;
    private FragmentPagerAdapter m_fragmentPagerAdapter;
    private List<Fragment> m_fragments;
    private ImageView m_imageView;
    private int m_imageViewWidth;
    private View m_cells[] = new View[CELL_COUNT];
    private PopupWindow m_popupWindow;
    private View m_moreView;
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main_interface);

        init();
    }

    /**
     * 做一些初始化工作
     */
    private void init(){
        calculateImageViewWidth();
        initView();
    }

    /**
     * 计算蓝色线的宽度
     */
    private void calculateImageViewWidth(){

        //获得屏幕宽度的 1 / 4
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        m_imageViewWidth = displayMetrics.widthPixels / 4;
    }

    /**
     * 初始化最下方的按钮
     */
    private void initCells(){
        m_cells[CELL_HALL]      = findViewById(R.id.id_hall_layout);
        m_cells[CELL_FOLLOW]    = findViewById(R.id.id_follow_layout);
        m_cells[CELL_SURPRISE]  = findViewById(R.id.id_surprise_layout);
        m_cells[CELL_PROFILE]   = findViewById(R.id.id_profile_layout);

        for(View view : m_cells)
            view.setOnClickListener(this);
    }

    /**
     * 初始化蓝线
     */
    private void initTabLineImageView(){
        m_imageView             = (ImageView) findViewById(R.id.id_tab_line);

        //设置蓝线的宽度
        ViewGroup.LayoutParams layoutParams = m_imageView.getLayoutParams();
        layoutParams.width = m_imageViewWidth;
        m_imageView.setLayoutParams(layoutParams);
    }

    /**
     * 初始化中间的view pager
     */
    private void initViewPager(){
        m_viewPager             = (ViewPager) findViewById(R.id.id_viewPager);

        //生成各个主界面
        m_fragments = new ArrayList<Fragment>();

        m_fragments.add(new HallTabFragment());
        m_fragments.add(new FollowTabFragment());
        m_fragments.add(new SurpriseTabFragment());
        m_fragments.add(new ProfileTabFragment());

        //创建适配器
        m_fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return m_fragments.get(position);
            }

            @Override
            public int getCount() {
                return m_fragments.size();
            }
        };

        //添加监听器
        m_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * 记录之前的偏移
             * 当滑动出下一个页面时 positionOffset 是单调递增的
             * 滑动之前的页面时 是单调递减的
             * 所以该值用来判断滑动方向
             */
            private float m_prevPositionOffset = 0f;
            /**
             * 由于view pager 的特点
             * 只能在positionOffset超出这个阈值后设置下一个图标不透明 当前图标透明
             */
            private static final float MAX_OPAQUE = 0.9f;
            /**
             * 同理
             */
            private static final float MIN_OPAQUE = 0.1f;
            /**
             * 透明时候的alpha值
             */
            private static final float TRANSPARENT = 0f;
            /**
             * 不透明时候的alpha值
             */
            private static final float OPAQUE = 1f;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                //设置蓝线的位置
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) m_imageView.getLayoutParams();
                layoutParams.leftMargin = (int) ((position + positionOffset) * m_imageViewWidth);
                m_imageView.setLayoutParams(layoutParams);

                //先把没有要选中的 都变为透明 因为如果没有这行代码 在快速切换的过程中 会使得有些图标有一点淡绿色
                for (int i = 0; i < m_cells.length; ++i)
                    if (i != position) m_cells[i].setAlpha(TRANSPARENT);

                //当小于0 的时候忽略
                if (positionOffset <= 0) return;

                //获得下一个页面
                if (m_prevPositionOffset < positionOffset) {

                    if (positionOffset <= MAX_OPAQUE) {
                        m_cells[position].setAlpha(1 - positionOffset);
                        m_cells[position + 1].setAlpha(positionOffset);
                    } else {
                        m_cells[position].setAlpha(TRANSPARENT);
                        m_cells[position + 1].setAlpha(OPAQUE);
                    }
                }

                //获得之前的页面
                else {
                    if (positionOffset > MIN_OPAQUE) {
                        m_cells[position + 1].setAlpha(positionOffset);
                        m_cells[position].setAlpha(1 - positionOffset);
                    } else {
                        m_cells[position].setAlpha(OPAQUE);
                        m_cells[position + 1].setAlpha(TRANSPARENT);
                    }
                }

                m_prevPositionOffset = positionOffset;
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        m_viewPager.setAdapter(m_fragmentPagerAdapter);
    }

    /**
     * 初始化界面
     */
    private void initView(){
        initCells();
        initTabLineImageView();
        initViewPager();
        initMoreView();
    }

    private void initMoreView(){

        final float xOffset = getResources().getDimension(R.dimen.popup_window_x_offset);
        final float yOffset = getResources().getDimension(R.dimen.popup_window_y_offset);

        m_moreView = findViewById(R.id.id_top_more);
        m_moreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(m_popupWindow == null) initPopupWindow();

                if (m_popupWindow.isShowing())
                    m_popupWindow.dismiss();
                else m_popupWindow.showAsDropDown(m_moreView, (int) xOffset, (int) yOffset);
            }
        });
    }

    /**
     * 获得启动activity所需的intent
     * @param context 上下文
     * @return 启动activity所需的intent
     */
    public static Intent getIntent(@NonNull Context context) {
        return new Intent(context, MainInterfaceActivity.class);
    }

    @Override
    public void onClick(View v) {

        final int id = v.getId();

        switch (id){
            case R.id.id_hall_layout:
                m_viewPager.setCurrentItem(CELL_HALL);
                break;
            case R.id.id_follow_layout:
                m_viewPager.setCurrentItem(CELL_FOLLOW);
                break;
            case R.id.id_surprise_layout:
                m_viewPager.setCurrentItem(CELL_SURPRISE);
                break;
            case R.id.id_profile_layout:
                m_viewPager.setCurrentItem(CELL_PROFILE);
                break;
            default: break;
        }
    }

    /**
     * 初始化popup window
     */
    private void initPopupWindow(){

        final float width = getResources().getDimension(R.dimen.popup_window_width);

        // 创建PopupWindow对象
        m_popupWindow = new PopupWindow(
                initPopUpWindowView(),
                (int)width,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                false
        );

        //设置点击窗口外边窗口消失
        m_popupWindow.setOutsideTouchable(true);
    }

    /**
     * @return 弹出菜单中的视图
     */
    private View initPopUpWindowView(){
        LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.popup_window_layout, null);
        return view;
    }
}
