package com.android.course.twitchbrowserapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emil S. Kolvig-Raun on 17-05-2018.
 */

/**
 * Implementation of PageAdapter that uses a fragment to manage each page.
 * This adapter handles saving and restoring of fragment state, enabling the user to swipe
 * between fragments and enter the same page state as user left it in when swiping back.
 */
public class StatePagerAdapter extends FragmentStatePagerAdapter {

    //Uses a list of fragments to maintain order.
    private final List<Fragment> fragmentList = new ArrayList<>();

    //Assigns a FragmentManager to super class
    public StatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    //Used to add a fragment to the list.
    //Used by the PopUpActivity.class
    public void addFragment(Fragment fragment){
        fragmentList.add(fragment);
    }

    //Can be used go get particular fragment in return, if more fragments are added later.
    //(ex. a settings screen for custom layout)
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
