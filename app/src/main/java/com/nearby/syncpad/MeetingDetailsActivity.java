package com.nearby.syncpad;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.nearby.syncpad.data.MeetingNotesLoader;
import com.nearby.syncpad.fragments.MeetingInfoFragment;
import com.nearby.syncpad.fragments.MeetingNotesFragment;
import com.nearby.syncpad.util.GeneralUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Sneha Khadatare on 11/20/2016.
 */

public class MeetingDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "MeetingDetailsActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @BindView(R.id.rlMainContentView)
    RelativeLayout rlMainContentView;

    private Unbinder mUnbinder;
    private Cursor mCursor;
    private String mItemId;
    private MyPagerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.meetings_details_layout);
        mUnbinder = ButterKnife.bind(this);

        if (savedInstanceState == null) {
            if (getIntent() != null) {
                mItemId = getIntent().getExtras().getString("item_id");
            }
        }
        getLoaderManager().initLoader(0, null, this);

        initialise();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void initialise() {

        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();

        fragmentList.add(MeetingInfoFragment.newInstance());
        fragmentList.add(MeetingNotesFragment.newInstance());

        mAdapter = new MyPagerAdapter(getSupportFragmentManager(),fragmentList);
        viewpager.setAdapter(mAdapter);
        viewpager.setOffscreenPageLimit(fragmentList.size());
        tabLayout.setupWithViewPager(viewpager);

        GeneralUtils.applyFontedTab(MeetingDetailsActivity.this, viewpager, tabLayout);
    }

   /* private void displayMeetingDetails() {

        //TODO display meeting using bean

        if (mCursor != null) {

            getSupportActionBar().setTitle(mCursor.getString(MeetingNotesLoader.Query.MEETING_NAME));

            if (!GeneralUtils.isEmpty(mCursor.getString(MeetingNotesLoader.Query.MEETING_NOTES)))
                mTvNotes.setText(mCursor.getString(MeetingNotesLoader.Query.MEETING_NOTES));
            if (!GeneralUtils.isEmpty(mCursor.getString(MeetingNotesLoader.Query.MEETING_PARTICIPANTS)))
            mTvParticipant.setText(mCursor.getString(MeetingNotesLoader.Query.MEETING_PARTICIPANTS));

        }

    }
*/

    class MyPagerAdapter extends FragmentStatePagerAdapter{

        ArrayList<Fragment> fragmentList;
        String[] titleArray = {"MEETING INFO" , "MEETING NOTES"};

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
            return titleArray[position];
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return MeetingNotesLoader.newInstanceForItemId(this, mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }
        //displayMeetingDetails();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
