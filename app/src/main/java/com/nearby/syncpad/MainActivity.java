package com.nearby.syncpad;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.nearby.syncpad.adapter.MyPagerAdapter;
import com.nearby.syncpad.fragments.MeetingCenterFragment;
import com.nearby.syncpad.fragments.MyMeetingsFragment;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager) ;
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout) ;

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(MeetingCenterFragment.newInstance());
        fragmentList.add(MyMeetingsFragment.newInstance());

        MyPagerAdapter mAdapter = new MyPagerAdapter(getSupportFragmentManager(),fragmentList);

        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(fragmentList.size());
        tabLayout.setupWithViewPager(viewPager);

    }

}
