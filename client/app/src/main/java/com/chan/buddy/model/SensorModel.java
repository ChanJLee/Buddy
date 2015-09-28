package com.chan.buddy.model;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

/**
 * Created by chan on 15-9-16.
 */
public class SensorModel implements SensorEventListener{

    /**
     * 加速度的阈值
     */
    private static final int ACC_THRESHOLD = 9;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private SensorManager m_sensorManager;
    private Sensor m_sensor;
    private OnSensorEventInvoke m_onSensorEventInvoke;
    private int m_type;
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param context
     * @param type 传感器类型 参考 {@link Sensor#TYPE_ACCELEROMETER}
     *             {@link Sensor#TYPE_GRAVITY}
     *             etc
     */
    public SensorModel(@NonNull Context context,int type) {
        m_type = type;
        m_sensorManager = (SensorManager)
                context.getSystemService(Context.SENSOR_SERVICE);
        m_sensor = m_sensorManager.getDefaultSensor(type);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (m_type == Sensor.TYPE_ACCELEROMETER)
            onAccSensorChanged(event);
    }

    private void onAccSensorChanged(SensorEvent event) {

        int max = (int) Math.max(Math.abs(event.values[0]), Math.abs(event.values[1]));
        if (max > ACC_THRESHOLD && m_onSensorEventInvoke != null)
            m_onSensorEventInvoke.onSensorEventInvoke(event.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /**
     * 当传感器传回数据的时候触发
     */
    public interface OnSensorEventInvoke{
        /**
         * @param values 传感器传回的数据
         *              参考 {@link SensorEvent#values}
         */
        void onSensorEventInvoke(float[] values);
    }

    /**
     * 注册传感器触发事件
     * @param onSensorEventInvoke 传感器触发事件触发监听器
     */
    public void registerListener(OnSensorEventInvoke onSensorEventInvoke) {
        m_onSensorEventInvoke = onSensorEventInvoke;
        m_sensorManager.registerListener(this, m_sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * 取消触发事件
     */
    public void unregisterListener() {
        m_sensorManager.unregisterListener(this);
    }
}
