package com.chan.buddy.surprise;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.chan.buddy.R;
import com.chan.buddy.bean.ImageFolder;
import com.chan.buddy.surprise.adapter.GridLayoutAdapter;
import com.chan.buddy.surprise.views.ImageGridView;
import com.chan.buddy.surprise.views.SelectDirPopupWindow;
import com.chan.buddy.utility.StorageUtility;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chan on 15-8-31.
 */
public class SelectImagesActivity extends Activity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    static private final short REQUEST_IMAGE = 0x0521;
    static public final String EXTRA_DATA = "chan_select_image";
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private List<ImageFolder> m_imageFolders;
    private String m_currentDir;
    private SelectDirPopupWindow m_popupWindow;
    private int m_screenWidth;
    private ImageGridView m_gridView;
    private TextView m_titleTextView;
    private int m_screenHeight;
    private String m_currentFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_images);

        init();
    }

    private void init(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)
                getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        m_screenWidth = displayMetrics.widthPixels;
        m_screenHeight = displayMetrics.heightPixels;
        initGridView();

        m_titleTextView = (TextView) findViewById(R.id.id_title_text_view);
        m_titleTextView.setText("选择图片");

        findViewById(R.id.id_accept_button).setOnClickListener(this);
        findViewById(R.id.id_back_image_view).setOnClickListener(this);
        findViewById(R.id.id_more_image).setOnClickListener(this);
    }

    private void initGridView() {
        m_gridView = (ImageGridView) findViewById(R.id.id_preview_grid_view);
        m_gridView.setOnImageItemClick(new ImageGridView.OnImageItemClick() {
            @Override
            public void onImageItemClick(String fileName) {
                m_currentFile = fileName;
            }
        });
        m_gridView.setOnCameraItemClick(new ImageGridView.OnCameraItemClick() {
            @Override
            public void onCameraItemClick() {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(StorageUtility.getImageTempFile()));
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //如果当前没有选定任何的目录 那么我们就从重新加载
        if(m_currentDir == null)
            initLoads();
        //如果已经选择了目录 那么重新加载那个目录下的文件
        else refreshImages();
    }

    private void refreshImages(){
        showProgressDialog();
        File dir = new File(m_currentDir);
        String fileNames[] = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpeg") ||
                        filename.endsWith(".png") ||
                        filename.endsWith(".jpg"))
                    return true;
                return false;
            }
        });

        GridLayoutAdapter gridLayoutAdapter = new GridLayoutAdapter(
                this,
                dir.getAbsolutePath(),
                fileNames,m_screenWidth / 3);

        m_gridView.setAdapter(gridLayoutAdapter);
        dismissProgressDialog();
    }

    private ProgressDialog m_progressDialog;
    private void initLoads() {
        showProgressDialog();
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(1, null, this);
    }

    private void showProgressDialog() {
        if(m_progressDialog == null){
            m_progressDialog = ProgressDialog.show(this, "加载图片", "加载中...");
        }
        m_progressDialog.show();
    }

    private void dismissProgressDialog(){
        m_progressDialog.dismiss();
    }

    private void getDirs(Cursor data){
        final int index = data.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        if(index == -1) return;

        m_imageFolders = new ArrayList<>();
        int i = 0;
        int size = 0;
        while (data.moveToNext()){
            String path = data.getString(index);
            String parent = new File(path).getParent();

            i = 0;
            size = m_imageFolders.size();
            for(;i < size;++i){
                if(m_imageFolders.get(i).getDir().equals(parent))
                    break;
            }

            //如果已经有了这个文件夹 那么增加它的文件个数
            if(i != size) {
                ImageFolder imageFolder = m_imageFolders.get(i);
                imageFolder.setCount(imageFolder.getCount() + 1);
                continue;
            }

            m_imageFolders.add(new ImageFolder(parent,path));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String mimeType = MediaStore.Images.Media.MIME_TYPE;
        String selection = mimeType + "=? or " + mimeType + "=? or " + mimeType + "=?";

        return new CursorLoader(
                this,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.ImageColumns.DATA},
                selection,
                new String[]{"image/png", "image/jpeg", "image/jpg"},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        getDirs(data);
        loadImages();
        initPopupWindow();
        dismissProgressDialog();
    }

    private void loadImages() {
        if(m_imageFolders.isEmpty()) return;

        File dir = new File(m_imageFolders.get(0).getDir());
        String fileNames[] = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpeg") ||
                        filename.endsWith(".png") ||
                        filename.endsWith(".jpg"))
                    return true;
                return false;
            }
        });

        //当前目录为第一个目录
        m_currentDir = dir.getAbsolutePath();
        GridLayoutAdapter gridLayoutAdapter = new GridLayoutAdapter(
                this,
                dir.getAbsolutePath(),
                fileNames,m_screenWidth / 3);
        m_gridView.setAdapter(gridLayoutAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    public static Intent getIntent(@NonNull Context context) {
        return new Intent(context, SelectImagesActivity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE) {

            //如果获取成功 那么当前的文件名 改为临时的文件名
            if (resultCode == RESULT_OK){
                m_currentFile = StorageUtility.getImageTempFile().getAbsolutePath();
                onAcceptClick();
            }else{
                onBackClick();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.id_back_image_view:
                onBackClick();
                break;
            case R.id.id_accept_button:
                onAcceptClick();
                break;
            case R.id.id_more_image:
                onMoreClick();
                break;
            default: break;
        }
    }

    private void onMoreClick(){
        if(m_popupWindow.isShowing())
            m_popupWindow.dismiss();
        else m_popupWindow.showAsDropDown(findViewById(R.id.id_bottom));
    }

    private void onBackClick(){
        finish();
    }

    private void onAcceptClick(){
        if (!TextUtils.isEmpty(m_currentFile)) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DATA, m_currentFile);
            setResult(RESULT_OK, intent);
        }else{
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    private void initPopupWindow() {

        m_popupWindow = new SelectDirPopupWindow(this,
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (m_screenHeight * 0.66), true,m_imageFolders);

        m_popupWindow.setOnDirectionSelected(new SelectDirPopupWindow.OnDirectionSelected() {
            @Override
            public void onDirectionSelected(String dirName) {
                m_currentDir = dirName;
                refreshImages();
            }
        });

        //设置点击窗口外边窗口消失
        m_popupWindow.setOutsideTouchable(true);
    }
}
