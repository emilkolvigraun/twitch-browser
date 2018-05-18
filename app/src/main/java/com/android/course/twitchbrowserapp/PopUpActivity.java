package com.android.course.twitchbrowserapp;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

/**
 * Created by Emil S. Kolvig-Raun on 17-05-2018.
 */

public class PopUpActivity extends AppCompatActivity {

    /**
     * The PopUp Activity is responsible for initiating the ViewPager variable,
     * which is held by the popup_placeholder layout.
     */
    private ViewPager viewPager;
    private int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_placeholder);

        //Retrieving the screen resolution once again
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        this.width = display.widthPixels;
        this.height = display.heightPixels;
        //Setting the activity-window size
        getWindow().setLayout((int) (this.width * 0.8), (int) (this.height * 0.65));

        //Locates the ViewPager element and sets the adapter.
        viewPager = findViewById(R.id.fragment_viewPager);
        viewPager.setAdapter(setUpViewPager());
    }

    /**
     * Used to add the fragments which are supposed to be part of the Activity.
     * Instatiates the StatePagerAdapter with the FragmentManager.
     * @returns a FragmentStatePagerAdapter.
     */
    private StatePagerAdapter setUpViewPager(){
        StatePagerAdapter adapter = new StatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TopUserFragment());
        adapter.addFragment(new UserListFragment());

        return adapter;
    }
}
