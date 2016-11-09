package com.nearby.syncpad;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.participants, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.participants) {

            startActivity(new Intent(MainActivity.this , MyProfileActivity.class));

            return true;
        }else{
            finish();
        }

        return super.onOptionsItemSelected(item);
    }





}
