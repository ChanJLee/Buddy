package com.chan.buddy.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chan.buddy.utility.StorageUtility;

import java.io.File;
import java.io.IOException;

/**
 * Created by chan on 15-8-28.
 */
public class AudioService extends Service {
    static private AudioServiceBind s_audioServiceBind;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return getBind();
    }

    private static AudioServiceBind getBind() {
        if (s_audioServiceBind == null)
            s_audioServiceBind = new AudioServiceBind();
        return s_audioServiceBind;
    }

    public static class AudioServiceBind extends Binder {

        private MediaRecorder m_mediaRecorder;
        public void startRecordAudio(){
            if(m_mediaRecorder != null) stopRecordAudio();
            m_mediaRecorder = new MediaRecorder();
            m_mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            m_mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            m_mediaRecorder.setOutputFile(StorageUtility.getAudioTempFile().getAbsolutePath());
            m_mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                m_mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                m_mediaRecorder.release();
                m_mediaRecorder = null;
                return;
            }
            m_mediaRecorder.start();
        }

        public void stopRecordAudio(){
            if(m_mediaRecorder == null) return;
            m_mediaRecorder.stop();
            m_mediaRecorder.release();
            m_mediaRecorder = null;
        }

        /**
         * @return 临时存储的音频文件位置
         */
        public String getAudioFileName(){
            return StorageUtility.getAudioTempFile().getAbsolutePath();
        }

        /**
         * 播放声音
         * @param fileName 文件名
         * @param context
         * @param mode 模式 如听筒播放 外放播放 参考
         *             {@link AudioManager#MODE_IN_CALL}
         *             {@link AudioManager#MODE_NORMAL}
         *             etc
         */
        public void playAudio(@NonNull String fileName,@NonNull Context context,int mode){

            if((mode & AudioManager.MODE_NORMAL) != AudioManager.MODE_NORMAL){
                AudioManager audioManager = (AudioManager)
                        context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setMode(mode);
            }

            if(m_mediaPlayer != null)
                stopAudio();
            m_mediaPlayer = new MediaPlayer();
            try {
                m_mediaPlayer.setDataSource(fileName);
                m_mediaPlayer.prepare();
                m_mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
                m_mediaPlayer.reset();
                m_mediaPlayer.release();
                m_mediaPlayer = null;
            }
        }

        public void playAudio(final int rawId,@NonNull Context context,int mode){

            if((mode & AudioManager.MODE_NORMAL) != AudioManager.MODE_NORMAL){
                AudioManager audioManager = (AudioManager)
                        context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setMode(mode);
            }

            if(m_mediaPlayer != null)
                stopAudio();
            m_mediaPlayer = MediaPlayer.create(context,rawId);
            m_mediaPlayer.start();
        }

        /**
         * 重新播放刚刚的音乐
         */
        public void restartPlayAudio() {
            if (m_mediaPlayer == null) return;
            m_mediaPlayer.start();
        }

        /**
         * 暂停播放音乐
         */
        public void stopAudio(){
            if(m_mediaPlayer == null) return;
            m_mediaPlayer.stop();
            m_mediaPlayer.release();
        }

        public boolean isRecording(){
            return m_mediaRecorder != null;
        }

        private MediaPlayer m_mediaPlayer;
    }
}
