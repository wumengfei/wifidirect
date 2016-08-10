package com.example.android.wifidirect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by beibei on 2016/8/4.
 */
public class start extends Activity {
    Button start;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //final Intent it = new Intent(mode_choose.this, WiFiDirectActivity.class); //��Ҫת���Activity

        //传入模式特征值
        Bundle bd=getIntent().getExtras();
        final String flag = bd.getString("flag");
        final String mode = bd.getString("mode");
        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(start.this, FgActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("flag",flag);
                bundle.putString("mode",mode);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }
}
