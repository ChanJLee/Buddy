package com.chan.buddy.utility;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by chan on 15-8-20.
 * 网络工具 用于上传 或者下载服务器数据
 */
public class NetworkUtility {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private final static short TIME_OUT         = 5000;
    private final static short RESPONSE_OK      = 200;
    private final static String METHOD_GET      = "Get";
    private final static String METHOD_POST     = "Post";
    private final static String FIELD_ENCODING  = "encoding";
    private final static String VALUE_ENCODING  = "utf-8";
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private OnPushListener  m_onPushListener;
    private OnGetListener   m_onGetListener;
    private String          m_uri;
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public NetworkUtility(@NonNull String uri){
        m_uri = uri;
    }

    /**
     * 向服务器发送数据
     * @param context 上下文
     * @throws ConnectException
     * @throws MalformedURLException
     */
    public void push(@NonNull Context context)
            throws ConnectException,
            MalformedURLException {
        if(!ConnectivityUtility.isConnected(context))
            throw new ConnectException("未连接网络");

        //创建url
        final URL uri = new URL(m_uri);

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                try{
                    httpURLConnection = (HttpURLConnection) uri.openConnection();
                    httpURLConnection.setRequestMethod(METHOD_GET);
                    httpURLConnection.setRequestProperty(FIELD_ENCODING, VALUE_ENCODING);
                    httpURLConnection.setConnectTimeout(TIME_OUT);

                    //如果连接失败
                    if(httpURLConnection.getResponseCode() != RESPONSE_OK) return;

                    OutputStream os = httpURLConnection.getOutputStream();
                    if(m_onPushListener != null) m_onPushListener.onPush(os);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(httpURLConnection != null) httpURLConnection.disconnect();
                }
            }
        }).start();
    }


    /**
     * 向服务器获取数据
     * @param context 上下文
     * @throws ConnectException
     * @throws MalformedURLException
     */
    public void get(@NonNull Context context)
            throws ConnectException,
            MalformedURLException {
        if(!ConnectivityUtility.isConnected(context))
            throw new ConnectException("未连接网络");

        //创建url
        final URL uri = new URL(m_uri);

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                try{
                    httpURLConnection = (HttpURLConnection) uri.openConnection();
                    httpURLConnection.setRequestMethod(METHOD_POST);
                    httpURLConnection.setRequestProperty(FIELD_ENCODING, VALUE_ENCODING);
                    httpURLConnection.setConnectTimeout(TIME_OUT);

                    //如果连接失败
                    if(httpURLConnection.getResponseCode() != RESPONSE_OK) return;

                    InputStream inputStream = httpURLConnection.getInputStream();
                    if(m_onGetListener != null) m_onGetListener.onGet(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(httpURLConnection != null) httpURLConnection.disconnect();
                }
            }
        }).start();
    }

    public void setOnPushListener(OnPushListener onPushListener)
        { m_onPushListener = onPushListener; }
    public void setOnGetListener(OnGetListener onGetListener)
        { m_onGetListener = onGetListener; }
    public OnPushListener getOnPushListener()
        { return m_onPushListener; }
    public OnGetListener getOnGetListener()
        { return m_onGetListener; }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 当push成功后调用的接口
     */
    public interface OnPushListener{
        /**
         * @param os 连接成功后的写流
         */
        public void onPush(OutputStream os);
    }

    /**
     * 当get成功之后调用的接口
     */
    public interface OnGetListener{
        /**
         * @param is 连接成功后的读流
         */
        public void onGet(InputStream is);
    }
}
