package com.android.course.twitchbrowserapp;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

/**
 * Created by Emil S. Kolvig-Raun on 17-05-2018.
 */

public class PopUpActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_placeholder);

        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);

        this.width = display.widthPixels;
        this.height = display.heightPixels;

        viewPager = findViewById(R.id.fragment_viewPager);

        viewPager.setAdapter(setUpViewPager());

        getWindow().setLayout((int) (this.width * 0.8), (int) (this.height * 0.65));
    }

    private StatePagerAdapter setUpViewPager(){
        StatePagerAdapter adapter = new StatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TopUserFragment());
        adapter.addFragment(new UserListFragment());

        return adapter;
    }
}
