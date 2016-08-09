package com.example.android.wifidirect;

/**
 * Created by beibei on 2016/8/7.
 */
import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class FgActivity extends FragmentActivity implements OnClickListener{

    private ViewPager viewpager;
    private PagerAdapter adapter;
    private Left_fragment left_fragment = new Left_fragment();;
    private Right_fragment right_fragment = new Right_fragment();

    private ImageView button1;
    private ImageView button2;

    ArrayList<Fragment> list = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);
        Log.d("onCreate", "onCreate");
        viewpager = (ViewPager) findViewById(R.id.viewPager);
        viewpager.setOffscreenPageLimit(1);
        viewpager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                clearSel();
                viewpager.setCurrentItem(arg0);
                switch (arg0) {
                    case 0:
                        button1.setBackgroundResource(R.drawable.frag_1_on);
                        break;
                    case 1:
                        button2.setBackgroundResource(R.drawable.frag_2_on);
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        initFragment();

        initViewPager();

        button1 = (ImageView) findViewById(R.id.frag_left);
        button2 = (ImageView) findViewById(R.id.frag_right);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button1.setBackgroundResource(R.drawable.frag_1_on);
    }



    private void initViewPager() {
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), list);
        viewpager.setAdapter(adapter);
    }

    private void initFragment() {
        list.add(left_fragment);
        list.add(right_fragment);
    }


    private void clearSel(){
        button1.setBackgroundResource(R.drawable.frag_1);
        button2.setBackgroundResource(R.drawable.frag_2);
    }

    @Override
    public void onClick(View v) {
        clearSel();
        switch (v.getId()) {
            case R.id.frag_left:
                this.viewpager.setCurrentItem(0);
                button1.setBackgroundResource(R.drawable.frag_1_on);
                break;
            case R.id.frag_right:
                this.viewpager.setCurrentItem(1);
                button2.setBackgroundResource(R.drawable.frag_2_on);
                break;
         }

    }
}
