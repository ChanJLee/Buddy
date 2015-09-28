package com.chan.buddy.surprise;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chan.buddy.R;
import com.chan.buddy.utility.CameraUtility;
import com.chan.buddy.utility.DialogReleaseUtility;
import com.chan.buddy.utility.StorageUtility;

/**
 * Created by chan on 15-8-26.
 */
public class RecordVideoActivity extends Activity
        implements View.OnClickListener,
        SurfaceHolder.Callback{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final short MAX_PROGRESS = 100;
    private static final short COUNT_TIME = 10000;
    private static final short INTERVAL = 1000;
    public static final String EXTRA_FILE_NAME = "record_video_activity";
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private PowerManager.WakeLock m_wakeLock;
    private ProgressBar m_progressBar;
    private Button m_acceptButton;
    /**
     * 转换摄像头
     */
    private ImageView m_switchCamera;
    /**
     * 开始录制按钮
     */
    private ImageView m_recordImageView;
    /**
     * 标题栏
     */
    private TextView m_titleTextView;
    /**
     * 是否录制了视频
     */
    private boolean m_hasRecorded = false;
    private SurfaceView m_surfaceView;
    private Camera m_camera;
    private boolean m_isBackCamera = true;
    private MediaRecorder m_mediaRecorder;
    private CountDownTimer m_countDownTimer = new CountDownTimer(COUNT_TIME,INTERVAL) {

        @Override
        public void onTick(long millisUntilFinished) {

            m_progressBar.setProgress((int) (MAX_PROGRESS - millisUntilFinished / 100));
        }

        @Override
        public void onFinish() {
            m_progressBar.setProgress(MAX_PROGRESS);
            stopRecord();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_video);

        init();
    }

    /**
     * 初始化
     */
    private void init(){
        findView();
        initView();
    }

    /**
     * 查找视图
     */
    private void findView(){
        m_titleTextView = (TextView) findViewById(R.id.id_title_text_view);
        m_switchCamera = (ImageView) findViewById(R.id.id_switch_camera);
        m_progressBar = (ProgressBar) findViewById(R.id.id_process_bar);
        m_recordImageView = (ImageView) findViewById(R.id.id_record_video_turn_image_view);
        m_surfaceView = (SurfaceView) findViewById(R.id.id_record_video_surface_view);
    }

    /**
     * 初始化视图
     */
    @SuppressWarnings("deprecated")
    private void initView(){
        m_titleTextView.setText("录像");
        m_switchCamera.setOnClickListener(this);
        m_recordImageView.setOnClickListener(this);
        findViewById(R.id.id_back_image_view).setOnClickListener(this);
        m_acceptButton = (Button) findViewById(R.id.id_accept_button);
        m_acceptButton.setOnClickListener(this);
        m_recordImageView.setEnabled(false);

        SurfaceHolder holder = m_surfaceView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * 返回启动activity 的 Intent
     * @param context 上下文
     * @return intent
     */
    public static Intent getIntent(@NonNull Context context) {
        return new Intent(context, RecordVideoActivity.class);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id){
            case R.id.id_back_image_view:
                onBackClick();
                break;
            case R.id.id_switch_camera:
                onSwitchCamera();
                break;
            case R.id.id_record_video_turn_image_view:
                onRecordButtonClick();
                break;
            case R.id.id_accept_button:
                onAcceptClick();
                break;
            default: break;
        }
    }

    private boolean isRecording(){
        return m_mediaRecorder != null;
    }

    private void onAcceptClick(){

        if(isRecording()) stopRecord();

        if(m_hasRecorded) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_FILE_NAME, StorageUtility.getVideoTempFile().getAbsolutePath());
            setResult(RESULT_OK, intent);
            finish();
            return;
        }

        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * 当转换摄像头按下时触发
     */
    private void onSwitchCamera() {

        //如果就一个摄像头 那么什么也不做
        if (Camera.getNumberOfCameras() <= 1) return;

        recycleCamera();

        //如果是后置摄像头
        if (m_isBackCamera) {
            m_isBackCamera = false;
            m_camera = CameraUtility.getCamera(
                    Camera.CameraInfo.CAMERA_FACING_FRONT,
                    90,
                    m_surfaceView.getHolder()
            );
        }
        else{
            //如果是前置摄像头
            m_isBackCamera = true;
            m_camera = CameraUtility.getCamera(
                    Camera.CameraInfo.CAMERA_FACING_BACK,
                    90,
                    m_surfaceView.getHolder()
            );
        }

        m_camera.startPreview();
    }

    /**
     * 当返回键按下时触发
     */
    private void onBackClick(){

        if(isRecording()) stopRecord();

        if(!m_hasRecorded){
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        DialogReleaseUtility.ButtonInfoHolder accept = new DialogReleaseUtility.
                ButtonInfoHolder("确定",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RecordVideoActivity.this.setResult(RESULT_CANCELED);
                RecordVideoActivity.this.finish();
            }
        });

        DialogReleaseUtility.ButtonInfoHolder cancel = new DialogReleaseUtility.
                ButtonInfoHolder("取消",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog dialog = DialogReleaseUtility.releaseDialog(
                this,
                null,
                "提示",
                "未保存录像,确定退出吗?",
                false,
                accept,
                cancel
        );
        dialog.show();
    }

    /**
     * 当录制按钮按下时触发
     */
    private void onRecordButtonClick(){
        if(m_mediaRecorder == null){
            startRecord();
        }else stopRecord();
    }

    /**
     * 开始录制
     */
    private void startRecord(){
        m_hasRecorded = false;
        m_mediaRecorder = CameraUtility.getMediaRecorder(
                m_camera,
                m_surfaceView.getHolder(),
                StorageUtility.getVideoTempFile(),
                m_isBackCamera
        );
        if(m_mediaRecorder == null) return;
        m_mediaRecorder.start();
        m_countDownTimer.start();
        m_acceptButton.setEnabled(false);
        m_switchCamera.setEnabled(false);
        m_recordImageView.setImageResource(R.drawable.video_recorder_stop_btn);
    }

    /**
     * 暂停录制
     */
    private void stopRecord(){
        if(m_mediaRecorder == null) return;
        m_acceptButton.setEnabled(true);
        m_mediaRecorder.stop();
        m_mediaRecorder.reset();
        m_mediaRecorder.release();
        m_mediaRecorder = null;
        m_hasRecorded = true;
        m_countDownTimer.cancel();
        m_camera.lock();
        m_switchCamera.setEnabled(true);
        m_recordImageView.setImageResource(R.drawable.video_recorder_start_btn);
    }

    @Override
    public void onBackPressed() {
        onBackClick();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        releaseCamera(holder);
    }

    /**
     * 获得摄像机
     */
    private void releaseCamera(SurfaceHolder surfaceHolder) {
        m_camera = CameraUtility.getCamera(
                Camera.CameraInfo.CAMERA_FACING_BACK,
                90,
                surfaceHolder
        );
        m_camera.startPreview();
        m_camera.lock();
        m_recordImageView.setEnabled(true);
    }

    /**
     * 释放摄像机
     */
    private void recycleCamera(){
        if(m_camera == null) return;
        m_camera.release();
        m_camera = null;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

    @SuppressWarnings("deprecated")
    @Override
    protected void onResume() {
        super.onResume();

        PowerManager powerManager = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        m_wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK,"chan_lock");
        m_wakeLock.acquire();
    }

    @Override
    protected void onStop() {
        super.onStop();
        m_wakeLock.release();
        recycleCamera();
    }
}
