package com.nearby.syncpad.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import com.nearby.syncpad.fragments.BaseFragment;

import java.util.ArrayList;


public class MyPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<Fragment> fragmentList;

    public MyPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ((BaseFragment)fragmentList.get(position)).getTitle();
    }
}
