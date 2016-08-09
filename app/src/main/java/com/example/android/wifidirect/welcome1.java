package com.example.android.wifidirect;

/**
 * Created by beibei on 2016/8/4.
 */

import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.ImageView;

public class welcome1 extends Activity{

    private Timer timer;
    private TimerTask task;
    private ImageView imageView;

    private Handler handler;

    int[] images = new int[]{
            R.drawable.wel1,
            R.drawable.wel2,
            R.drawable.wel3,
    };
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wel1);
        timer = new Timer();

        imageView = (ImageView) findViewById(R.id.come);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                changeBackground();
            }
        };

        task = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0x123);
            }
        };
        timer.schedule(task, 0, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        task.cancel();
    }

    private void changeBackground (){
        if(count == 3){
            Intent it = new Intent(welcome1.this, mode_choose.class);
            startActivity(it);
            finish();
        }else {
            imageView.setImageResource(images[count]);
        }
        count++;
    }
}
