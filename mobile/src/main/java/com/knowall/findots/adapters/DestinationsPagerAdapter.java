package com.knowall.findots.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.knowall.findots.fragments.DestinationFragment;
import com.knowall.findots.fragments.DestinationsMapFragment;

/**
 * Created by parijathar on 7/4/2016.
 */
public class DestinationsPagerAdapter extends FragmentStatePagerAdapter {

    final int List = 1;
    final int Map = 0;

    int numOfTabs;

    public DestinationsPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case Map:
                DestinationsMapFragment tabMap = new DestinationsMapFragment();
                return tabMap;

            case List:
                DestinationFragment tabList = new DestinationFragment();
                return tabList;
        }

        return null;
    }
}
