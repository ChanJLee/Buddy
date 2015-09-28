package com.chan.buddy.surprise;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import com.chan.buddy.R;
import com.chan.buddy.model.SensorModel;
import com.chan.buddy.service.AudioService;

/**
 * Created by chan on 15-9-16.
 */
public class ShakeActivity extends Activity implements
        SensorModel.OnSensorEventInvoke,View.OnClickListener {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int DURATION = 900;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private View m_cardResult;
    private SensorModel m_sensorModel;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private ObjectAnimator m_upAnimator;
    private ObjectAnimator m_downAnimator;
    private AnimatorSet m_animatorSet;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private AudioService.AudioServiceBind m_audioServiceBind;

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
        setContentView(R.layout.shake_layout);

        init();
    }

    private void init() {
        m_cardResult = findViewById(R.id.id_result_card);
        m_sensorModel = new SensorModel(this, Sensor.TYPE_ACCELEROMETER);

        final float translation = getResources().getDimension(R.dimen.card_height) + 30f;
        m_downAnimator = ObjectAnimator.ofFloat(m_cardResult, "translationY", 0, translation);
        m_upAnimator = ObjectAnimator.ofFloat(m_cardResult, "translationY", translation, 0);
        m_downAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                m_cardResult.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (m_audioServiceBind != null)
                    m_audioServiceBind.playAudio(
                            R.raw.shake_match,
                            ShakeActivity.this,
                            AudioManager.MODE_NORMAL
                    );
            }
        });
        m_upAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                m_cardResult.setVisibility(View.GONE);
            }
        });
        m_downAnimator.setDuration(DURATION);
        m_upAnimator.setDuration(DURATION);

        TextView titleView = (TextView) findViewById(R.id.id_title_text_view);
        titleView.setText("摇一摇");
        findViewById(R.id.id_back_image_view).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        m_sensorModel.registerListener(this);
        Intent intent = new Intent(this, AudioService.class);
        bindService(intent, m_serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        m_sensorModel.unregisterListener();
        unbindService(m_serviceConnection);
    }

    /**
     * @param context
     * @return
     */
    public static Intent getIntent(Context context) {
        return new Intent(context, ShakeActivity.class);
    }

    @Override
    public void onSensorEventInvoke(float[] values) {
        startAnimation(null, null, null, null);
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

    private void onBackClick(){
        finish();
    }

    private void startAnimation(Bitmap avatar,String nickname,String distance,String sex) {

        //如果正在有动画播放 那就不管
        if(m_downAnimator.isRunning() ||
                (m_animatorSet != null && m_animatorSet.isRunning()))
            return;

        //第一次的时候只是向下把卡片调出
        if (m_animatorSet == null) {
            m_downAnimator.start();
            m_animatorSet = new AnimatorSet();
            m_animatorSet.play(m_upAnimator).before(m_downAnimator);
           //m_animatorSet.setDuration(DURATION * 2);
            return;
        }

        //******************************************************************************************
        //设置卡片的内容

        //其他情况先要把卡片提起 然后再次放下
        m_animatorSet.start();
    }
}
