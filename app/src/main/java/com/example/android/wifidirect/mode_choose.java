package com.example.android.wifidirect;

/**
 * Created by beibei on 2016/8/4.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class mode_choose extends Activity{
    Button pedestrian;
    Button car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_choose);
        //final Intent it = new Intent(mode_choose.this, WiFiDirectActivity.class); //��Ҫת���Activity
        pedestrian = (Button) findViewById(R.id.pedestrian);
        pedestrian.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent1 = new Intent(mode_choose.this, start.class);
                String flag = "C1X";
                Bundle bundle = new Bundle();
                bundle.putString("flag",flag);
                intent1.putExtras(bundle);
                startActivity(intent1);
                finish();
            }
        });
        car = (Button) findViewById(R.id.car);
        car.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent2 = new Intent(mode_choose.this, start_car.class);
                String flag = "C2X";
                Bundle bundle = new Bundle();
                bundle.putString("flag",flag);
                intent2.putExtras(bundle);
                startActivity(intent2);
                finish();
            }
        });
    }

}
