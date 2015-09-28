package com.chan.buddy.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chan.buddy.R;
import com.chan.buddy.utility.QRUtility;
import com.google.zxing.WriterException;

/**
 * Created by chan on 15-9-5.
 */
public class QRShowActivity extends Activity {
    private ImageView m_imageView;
    private final int WHAT_QR_IMAGE = 0x0521;
    private int m_sizePrev;

    private Handler m_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == WHAT_QR_IMAGE){
                m_imageView.setImageBitmap((Bitmap)msg.obj);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_show);
        init();
    }

    private void init(){

        TextView title = (TextView) findViewById(R.id.id_title_text_view);
        title.setText("二维码");
        findViewById(R.id.id_back_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        m_imageView = (ImageView) findViewById(R.id.id_show_qr);
        m_sizePrev = (int) getResources().getDimension(R.dimen.qr_width);

        new Thread(new Runnable() {
            @Override
            public void run() {

                Bitmap bitmap = null;
                try {
                    bitmap = QRUtility.createQRCode(
                            "http://94.05.21.09:8080/fllow?user=chan&from=yu",
                            m_sizePrev
                    );
                } catch (WriterException e) {
                    e.printStackTrace();
                    return;
                }

                Message message = m_handler.obtainMessage();
                message.obj = bitmap;
                message.what = WHAT_QR_IMAGE;
                m_handler.sendMessage(message);
            }}).start();
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, QRShowActivity.class);
    }
}
