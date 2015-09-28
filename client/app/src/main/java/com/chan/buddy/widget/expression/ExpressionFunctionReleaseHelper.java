package com.chan.buddy.widget.expression;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chan.buddy.R;
import com.chan.buddy.utility.ExpressionUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chan on 15-8-24.
 * 根据给定的view group 生成相应的 表情面板组件
 */
public class ExpressionFunctionReleaseHelper {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 表情对应的资源id
     */
    private static final int[] s_expressions = ExpressionUtility.getExpressions();
    /**
     * 每页包含的表情个数
     */
    private static short s_pageItemCount[] = { 21, 21, 21, 11 };
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 生成支持表情面板的功能
     * @param context 上下文
     * @param editText 输入框
     * @param viewPager 表情容器
     * @param indicatorContainer 指示当前多少页的点点容器
     */
    public static void releaseExpressionFunction(@NonNull final Context context,
                                                 @NonNull final EditText editText,
                                                 @NonNull ViewPager viewPager,
                                                 @NonNull final LinearLayout indicatorContainer) {
        releaseExpressionBoard(
                context,
                viewPager,
                new ExpressionFunctionReleaseHelper.OnItemClickListener() {
                    @Override
                    public void onItemClick(int resourceId) {
                        ExpressionUtility.onExpressionClick(context, resourceId, editText);
                    }
                });
        onPageChange(viewPager, indicatorContainer);
    }

    /**
     * 当表情换页发生时执行的操作
     * @param viewPager 表情容器
     * @param indicatorContainer 存放点点的容器
     */
    private static void onPageChange(@NonNull ViewPager viewPager ,
                                     @NonNull final LinearLayout indicatorContainer){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                final int size = indicatorContainer.getChildCount();
                for (int i = 0; i < size; ++i) {
                    ImageView imageView = (ImageView) indicatorContainer.getChildAt(i);
                    final int drawable = i == position ?
                            R.drawable.page_indicator_focused :
                            R.drawable.page_indicator_unfocused;
                    imageView.setImageResource(drawable);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    /**
     * 将表情存放到给定的view pager中
     * @param context   上下文
     * @param viewPager 用于放置表情面板的view pager
     * @param listener  当表情点击的时候触发的监听器
     */
    private static void releaseExpressionBoard(@NonNull Context context,
                                              @NonNull ViewPager viewPager,
                                              OnItemClickListener listener) {
        viewPager.setAdapter(
                new ExpressionPagerAdapter(
                        getPages(context, listener)
                )
        );
    }

    /**
     * 当表情点击的时候触发
     */
    public interface OnItemClickListener{
        /**
         * @param resourceId  表情所关联的 资源id 为 drawable类型
         */
        public void onItemClick(int resourceId);
    }

    /**
     * @param context 上下文
     * @return 所有的表情页面
     */
    public static List<View> getPages(
            Context context, final OnItemClickListener listener) {

        //存放所有的页面
        List<View> views;
        views = new ArrayList<View>();

        //一次生成界面
        int idx = 0;
        for (int i = 0; i < s_pageItemCount.length; ++i) {

            //获得每一页的表情个数
            final int count = s_pageItemCount[i];

            //存放每个表情的资源引用
            Integer[] expressions = new Integer[count];
            for (int j = 0; j < count; ++j)
                expressions[j] = s_expressions[idx++];

            //生成网格布局
            final GridView gridView = (GridView) View.inflate(context, R.layout.expression_gridview, null);

            //设置监听器
            gridView.setAdapter(new ExpressionGridViewAdapter(context, 1, expressions));

            //添加点击表情触发的事件
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int resourceId = (int) view.getTag();
                    if (listener != null) listener.onItemClick(resourceId);
                }
            });

            views.add(gridView);
        }

        return views;
    }

    /**
     * view pager的表情适配器
     */
    static class ExpressionPagerAdapter extends PagerAdapter{

        private List<View> m_pages;
        public ExpressionPagerAdapter(List<View> views){
            m_pages = views;
        }

        @Override
        public int getCount() {
            return m_pages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = m_pages.get(position);
            container.removeView(view);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View view = m_pages.get(position);
            container.addView(view);
            return view;
        }
    }

    /**
     * 网格布局的适配器
     */
    static class ExpressionGridViewAdapter extends ArrayAdapter<Integer> {
        public ExpressionGridViewAdapter(Context context, int resource, Integer[] objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = View.inflate(getContext(), R.layout.expression, null);
            }

            int resourceId = getItem(position);
            ImageView imageView = (ImageView) convertView;
            imageView.setImageResource(resourceId);
            imageView.setTag(resourceId);

            return convertView;
        }
    }
}
