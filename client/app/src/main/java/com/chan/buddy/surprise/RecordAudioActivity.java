package com.chan.buddy.surprise;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chan.buddy.R;
import com.chan.buddy.magic.CircularProgressDrawable;
import com.chan.buddy.model.SensorModel;
import com.chan.buddy.service.AudioService;
import com.chan.buddy.utility.DialogReleaseUtility;

/**
 * Created by chan on 15-8-28.
 */
public class RecordAudioActivity
        extends Activity
        implements View.OnClickListener,SensorModel.OnSensorEventInvoke{

    public static final String EXTRA_FILE_NAME = "chan_extra_audio";
    private static final int MAX_DURATION = 3600;
    private TextView m_textView;
    private TextView m_titleTextView;
    private ImageView m_startRecordImageView;
    private AudioService.AudioServiceBind m_audioServiceBind;
    private Animator m_animator;
    private CircularProgressDrawable m_circularProgressDrawable;
    private Button m_acceptButton;
    private boolean m_hasRecord = false;
    private static final long MAX_AUDIO_LENGTH = 60000;
    private SensorModel m_sensorModel;


    private CountDownTimer m_countDownTimer = new CountDownTimer(MAX_AUDIO_LENGTH,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if(millisUntilFinished < 5000){
                warnNoEnoughTime();
            }

            if(m_animator != null && !m_animator.isRunning()){
                m_animator.start();
            }
        }

        @Override
        public void onFinish() {
            stopRecord();
            cancelWarn();
        }
    };

    private Vibrator m_vibrator;

    private void warnNoEnoughTime() {
        if (m_vibrator != null) return;
        m_vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        m_vibrator.vibrate(new long[]{1000, 1000}, -1);
        Toast.makeText(this,"还剩五秒不到的时间",Toast.LENGTH_SHORT).show();
    }

    private void cancelWarn(){
        if(m_vibrator == null) return;
        m_vibrator.cancel();
    }

    private ServiceConnection m_serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            m_audioServiceBind = (AudioService.AudioServiceBind) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            m_audioServiceBind = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_audio);
        init();
    }

    @SuppressWarnings("deprecated")
    private void init(){
        m_titleTextView = (TextView) findViewById(R.id.id_title_text_view);
        m_textView = (TextView) findViewById(R.id.id_text_view);
        m_startRecordImageView = (ImageView) findViewById(R.id.id_start_record_image_view);
        m_acceptButton = (Button) findViewById(R.id.id_accept_button);

        m_startRecordImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        startRecord();
                        break;
                    case MotionEvent.ACTION_UP:
                        stopRecord();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        m_titleTextView.setText("录音");

        findViewById(R.id.id_back_image_view).
                setOnClickListener(this);
        m_acceptButton.setOnClickListener(this);

        m_circularProgressDrawable = new CircularProgressDrawable.Builder()
                .setRingWidth(getResources().getDimensionPixelSize(R.dimen.drawable_ring_size))
                .setOutlineColor(getResources().getColor(android.R.color.darker_gray))
                .setRingColor(getResources().getColor(android.R.color.holo_green_light))
                .setCenterColor(getResources().getColor(android.R.color.holo_blue_dark))
                .create();
        m_startRecordImageView.setImageDrawable(m_circularProgressDrawable);
    }

    private void startRecord() {

        //当之前还没结束录制的时候 先结束上次的录制
        if (m_animator != null)
            stopRecord();

        //开始录制
        if (m_audioServiceBind == null) return;

        m_textView.setText("摇晃播放");
        m_animator = getAnimator();
        m_audioServiceBind.startRecordAudio();
        m_animator.start();
        m_acceptButton.setEnabled(false);
        m_countDownTimer.start();
    }


    private void stopRecord() {
        //如果动画没准备好
        if (m_animator == null) return;

        if (m_audioServiceBind == null) return;

        m_countDownTimer.cancel();
        m_textView.setText("按住录音");
        m_audioServiceBind.stopRecordAudio();
        m_acceptButton.setEnabled(true);
        m_animator.cancel();
        m_animator = null;
        m_hasRecord = true;
    }

    /**
     * 绑定音频服务
     */
    private void bindAudioService() {

        Intent intent = new Intent(this, AudioService.class);
        bindService(intent, m_serviceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 解绑音频服务
     */
    private void unBindAudioService() {
        unbindService(m_serviceConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindAudioService();
        registerSensor();
    }

    private void registerSensor() {
        if (m_sensorModel == null)
            m_sensorModel = new SensorModel(this, Sensor.TYPE_ACCELEROMETER);
        m_sensorModel.registerListener(this);
    }

    private void unregisterSensor(){
        if(m_sensorModel != null)
            m_sensorModel.unregisterListener();
    }

    private void enableSensor(){

    }

    private void disableSensor(){

    }

    @Override
    protected void onStop() {
        super.onStop();
        unBindAudioService();
        unregisterSensor();
    }

    @Override
    public void onBackPressed() {
        onBackClick();
    }

    /**
     * 返回启动activity的intent
     * @param context
     * @return
     */
    public static Intent getIntent(@NonNull Context context) {
        return new Intent(context, RecordAudioActivity.class);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id){
            case R.id.id_back_image_view:
                onBackClick();
                break;
            case R.id.id_accept_button:
                onAcceptClick();
                break;
            default: break;
        }
    }

    /**
     * 当返回按钮按下时
     */
    private void onBackClick(){
        if(m_animator != null)
            stopRecord();

        DialogReleaseUtility.ButtonInfoHolder accept = new DialogReleaseUtility.
                ButtonInfoHolder("确定",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RecordAudioActivity.this.setResult(RESULT_CANCELED);
                RecordAudioActivity.this.finish();
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
                "未保存录音,确定退出吗?",
                false,
                accept,
                cancel
        );
        dialog.show();
    }

    /**
     * 当确定按钮按下时
     */
    private void onAcceptClick(){

        if(m_hasRecord && m_audioServiceBind != null){
            String fileName = m_audioServiceBind.getAudioFileName();
            Intent intent = new Intent();
            intent.putExtra(EXTRA_FILE_NAME, fileName);
            setResult(RESULT_OK, intent);
        }else{
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    /**
     * @return 返回动画
     */
    @SuppressWarnings("deprecated")
    private Animator getAnimator(){
        AnimatorSet animation = new AnimatorSet();

        final Animator indeterminateAnimation = ObjectAnimator.ofFloat(
                m_circularProgressDrawable,
                CircularProgressDrawable.PROGRESS_PROPERTY,
                0,
                3600
        );
        indeterminateAnimation.setDuration(MAX_DURATION);

        Animator innerCircleAnimation = ObjectAnimator.ofFloat(
                m_circularProgressDrawable,
                CircularProgressDrawable.CIRCLE_SCALE_PROPERTY,
                0f,
                0.75f
        );
        indeterminateAnimation.setDuration(MAX_DURATION);
        indeterminateAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                m_circularProgressDrawable.setIndeterminate(true);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                m_circularProgressDrawable.setIndeterminate(false);
                m_circularProgressDrawable.setProgress(0);
            }
        });

        animation.play(indeterminateAnimation).with(innerCircleAnimation);

        return animation;
    }

    @Override
    public void onSensorEventInvoke(float[] values) {

        if (m_audioServiceBind != null &&
                !m_audioServiceBind.isRecording()) {
            m_audioServiceBind.playAudio(
                    m_audioServiceBind.getAudioFileName(),
                    this,
                    AudioManager.MODE_NORMAL
            );
        }
    }
}
