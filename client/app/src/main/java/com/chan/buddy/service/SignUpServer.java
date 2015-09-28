package com.chan.buddy.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chan.buddy.utility.ConnectivityUtility;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;

/**
 * Created by chan on 15-9-8.
 */
public class SignUpServer extends Service {
    public static final int MESSAGE_SIGN_UP = 0x0521;
    public static final int SIGN_UP_SUCCESS = 0x0525;
    public static final int SIGN_UP_FAILED = 0x0529;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new SignUpBinder();
    }

    private interface OnSignUpResponse{
        void onSignUpResponse(boolean isSuccess);
    }

    public final static class SignUpResponse implements OnSignUpResponse{
        private Handler m_handler;

        public SignUpResponse(Handler handler) {
            m_handler = handler;
        }

        @Override
        public void onSignUpResponse(boolean isSuccess) {
            Message message = m_handler.obtainMessage();
            message.what = MESSAGE_SIGN_UP;
            message.arg1 = isSuccess ? SIGN_UP_SUCCESS : SIGN_UP_FAILED;
            m_handler.sendMessage(message);
        }
    }

    public static final class SignUpBinder extends Binder {

        public void setOnSignUpResponse(SignUpResponse onSignUpResponse) {
            m_onSignUpResponse = onSignUpResponse;
        }
        private SignUpResponse m_onSignUpResponse;

        /**
         * @param name
         * @param userName
         * @param passWord
         * @param avatar
         */
        public void signUp(final String name,
                           final String userName,
                           final String passWord,
                           @NonNull final String avatar) {
            if (m_onSignUpResponse == null) return;

            RequestBody fileBody = RequestBody.create(
                    MediaType.parse("application/octet-stream"), new File(avatar));
            RequestBody registerInfo = RequestBody.create(null, name + " " + userName + " " + passWord);

            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addPart(Headers.of(
                            "Content-Disposition",
                            "form-data; name=\"info\""), registerInfo)
                    .addPart(Headers.of("Content-Disposition",
                            "form-data; name=\"mFile\";filename=\"" + avatar + "\""), fileBody)
                    .build();

            Request request = new Request.Builder()
                    .post(requestBody)
                    .url(ConnectivityUtility.getSignUpHost())
                    .build();

            OkHttpClient okHttpClient = new OkHttpClient();
            com.squareup.okhttp.Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {}

                @Override
                public void onResponse(Response response) throws IOException {
                    String result = response.body().string();

                    if (TextUtils.isEmpty(result) || "false".equals(result))
                        m_onSignUpResponse.onSignUpResponse(false);
                    else m_onSignUpResponse.onSignUpResponse(true);
                }
            });
        }
    }
}
