package com.hammad13060.datingapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;

/**
 * Created by Hammad on 20-10-2015.
 */
public class DisplayActivityPagerAdapter extends FragmentPagerAdapter {

    private Context context = null;
    //tab specific constants
    private static final int VIEW_COUNT = 3;
    public static final int DISPLAY_PEOPLE = 0;
    public static final int DISPLAY_MATCH = 1;
    public static final int DISPLAY_PROFILE = 2;
    private static final String[] PAGE_TITLE = {
            "People",
            "Matches",
            "Profile"
    };


    public DisplayActivityPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        if (position == DISPLAY_PEOPLE) {
            f = new DisplayPeopleFragment();
        } else if (position == DISPLAY_MATCH){
            f = new DisplayMatchFragment();
        } else if (position == DISPLAY_PROFILE) {
            f = new DisplayProfileFragment();
        } else {
            f = new DisplayPeopleFragment();
        }

        return f;
    }

    @Override
    public int getCount() {
        return VIEW_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return PAGE_TITLE[position];
    }
}


