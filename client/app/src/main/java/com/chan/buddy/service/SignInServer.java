package com.chan.buddy.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chan.buddy.utility.ConnectivityUtility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by chan on 15-9-8.
 */
public class SignInServer extends Service {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int MESSAGE_SIGN_IN = 0x0521;
    public static final int SIGN_IN_SUCCESS = 0x0525;
    public static final int SIGN_IN_FAILED = 0x0529;
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new SignInBinder();
    }

    public static class SignInBinder extends Binder{
        private ForwardOnSignInResponse m_onSignInResponse;

        public void setOnSignInResponse(ForwardOnSignInResponse onSignInResponse) {
            m_onSignInResponse = onSignInResponse;
        }

        /**
         * 当登录服务器响应后调用
         * warning: 这个接口是在其它线程运行的!
         */
        private interface OnSignInResponse{
            void onSignInResponse(boolean isSuccess,String nickName);
        }

        public static class ForwardOnSignInResponse implements OnSignInResponse{
            private Handler m_handler;
            public ForwardOnSignInResponse(Handler handler){
                m_handler = handler;
            }

            @Override
            public void onSignInResponse(boolean isSuccess,String nickName) {
                Message message = m_handler.obtainMessage();
                message.what = MESSAGE_SIGN_IN;
                message.arg1 = isSuccess ? SIGN_IN_SUCCESS : SIGN_IN_FAILED;
                if(isSuccess) message.obj = nickName;
                m_handler.sendMessage(message);
            }
        }

        /**
         * 调用后台接口 与服务器连接登录服务
         * @param userName
         * @param passWord
         */
        public void signIn(@NonNull final String userName,@NonNull final String passWord){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //如果响应接口没有注册 那么就直接返回
                    if(m_onSignInResponse == null)
                        return;
                    String response = "true";

                    try {
                        URL url = new URL(ConnectivityUtility.getSignInHost());
                        HttpURLConnection httpURLConnection = (HttpURLConnection)
                                url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setUseCaches(false);
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.setRequestProperty("Content-type", "text/plain");
                        httpURLConnection.setRequestProperty("Charset", "utf-8");

                        httpURLConnection.connect();

                        BufferedWriter bufferedWriter = new BufferedWriter(
                                new OutputStreamWriter(
                                        httpURLConnection.getOutputStream()
                                )
                        );

                        bufferedWriter.write(userName + "\n");
                        bufferedWriter.write(passWord + "\n");
                        bufferedWriter.flush();
                        bufferedWriter.close();

                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(
                                        httpURLConnection.getInputStream()
                                ,Charset.forName("UTF-8"))
                        );
                        response = bufferedReader.readLine();
                        bufferedReader.close();

                        httpURLConnection.disconnect();
                    } catch (Exception e) {
                        response = "false";
                    }

                    if("false".equals(response)){
                        m_onSignInResponse.onSignInResponse(false,null);
                    }else {
                        m_onSignInResponse.onSignInResponse(true, response);
                    }
                }
            }).start();
        }
    }
}
