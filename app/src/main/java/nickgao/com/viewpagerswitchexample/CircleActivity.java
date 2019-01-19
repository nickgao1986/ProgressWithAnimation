package nickgao.com.viewpagerswitchexample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import nickgao.com.viewpagerswitchexample.view.SportStepCountView;

public class CircleActivity extends Activity {


    private SportStepCountView sportStepCountView;
    private Handler mHandler = new MyHandler();
    private int mProgress = 0;
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            sportStepCountView.setValueDuringRefresh(mProgress,100);

        }
    }


    public static void startActivity(Context context) {
        Intent intent = new Intent();
        intent.setClass(context,CircleActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sportStepCountView = (SportStepCountView)findViewById(R.id.circleProgress);
//        sportStepCountView.setValue(50,100);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mProgress<100) {
                    mProgress++;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(0);
                }
            }
        }).start();
    }


}
