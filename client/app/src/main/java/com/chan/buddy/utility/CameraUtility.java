package com.chan.buddy.utility;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.IOException;

/**
 * Created by chan on 15-8-26.
 */
public class CameraUtility {

    /**
     * 获得摄像头
     *
     * @param id            相机id
     * @param degrees       旋转角度
     * @param surfaceHolder
     * @return 相机
     */
    public static Camera getCamera(int id, int degrees, SurfaceHolder surfaceHolder) {
        Camera camera = Camera.open(id);
        camera.setDisplayOrientation(degrees);
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (Exception e) {
            e.printStackTrace();
            camera.release();
            return null;
        }
        return camera;
    }

    /**
     * 获得录制录像的media recorder
     * @param camera
     * @param surfaceHolder
     * @param out 输出文件
     * @return
     */
    @SuppressWarnings("deprecated")
    public static MediaRecorder getMediaRecorder(@NonNull Camera camera,
                                                 @NonNull SurfaceHolder surfaceHolder,
                                                 @NonNull File out,
                                                 boolean isBackCamera,
                                                 int maxDuration){

        if(camera == null) return null;

        MediaRecorder mediaRecorder = new MediaRecorder();

        camera.stopPreview();
        camera.unlock();
        mediaRecorder.setCamera(camera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        CamcorderProfile camcorderProfile = null;

        if(CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_LOW))
            camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
        else if(CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P))
            camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);

        try {
            mediaRecorder.setProfile(camcorderProfile);
            mediaRecorder.setOutputFile(out.getAbsolutePath());

            if(isBackCamera)
                mediaRecorder.setOrientationHint(90);
            else mediaRecorder.setOrientationHint(270);

            //防止录制过短而崩溃
            mediaRecorder.setOnErrorListener(null);
            mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
            mediaRecorder.setMaxDuration(maxDuration);
            mediaRecorder.prepare();
        } catch (Exception e) {
            mediaRecorder.release();
            return null;
        }

        return mediaRecorder;
    }
}
