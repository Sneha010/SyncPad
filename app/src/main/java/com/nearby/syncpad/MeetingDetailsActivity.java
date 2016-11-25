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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nearby.syncpad.data.MeetingNotesLoader;
import com.nearby.syncpad.fragments.MeetingInfoFragment;
import com.nearby.syncpad.fragments.MeetingNotesFragment;
import com.nearby.syncpad.models.Meeting;
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

    @BindView(R.id.llMainContent)
    LinearLayout llMainContent;

    @BindView(R.id.rl_progress)
    RelativeLayout rlProgress;

    @BindView(R.id.tvError)
    TextView tvError;



    private Unbinder mUnbinder;
    private Cursor mCursor;
    private String mItemId;
    private MyPagerAdapter mAdapter;
    private Meeting mMeeting;

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

        setToolbar();
        showProgress();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void setToolbar() {

        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void loadContent() {

        if (mCursor != null) {
            Meeting meeting = new Meeting();
            meeting.setMeetingName(mCursor.getString(MeetingNotesLoader.Query.MEETING_NAME));
            meeting.setMeetingDate(mCursor.getString(MeetingNotesLoader.Query.MEETING_DATE));
            meeting.setMeetingTime(mCursor.getString(MeetingNotesLoader.Query.MEETING_TIME));
            meeting.setMeetingVenue(mCursor.getString(MeetingNotesLoader.Query.MEETING_VENUE));
            meeting.setMeetingAgenda(mCursor.getString(MeetingNotesLoader.Query.MEETING_AGENDA));
            meeting.setNotesList(mCursor.getString(MeetingNotesLoader.Query.MEETING_NOTES));
            meeting.setParticipantNameList(mCursor.getString(MeetingNotesLoader.Query.MEETING_PARTICIPANTS));
            mMeeting = meeting;

            showContent();
            displayMeetingContent(meeting);

        }else{
            showError();
        }


    }

    private void displayMeetingContent(Meeting meeting) {

        getSupportActionBar().setTitle(meeting.getMeetingName());

        ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();

        fragmentList.add(MeetingInfoFragment.newInstance(mMeeting));
        fragmentList.add(MeetingNotesFragment.newInstance(meeting.getNotesList()));

        mAdapter = new MyPagerAdapter(getSupportFragmentManager(),fragmentList);
        viewpager.setAdapter(mAdapter);
        viewpager.setOffscreenPageLimit(fragmentList.size());
        tabLayout.setupWithViewPager(viewpager);

        GeneralUtils.applyFontedTab(MeetingDetailsActivity.this, viewpager, tabLayout);
    }


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

        loadContent();


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

    private void showProgress(){
        rlProgress.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        llMainContent.setVisibility(View.GONE);
    }

    private void showError(){
        rlProgress.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        llMainContent.setVisibility(View.GONE);
    }

    private void showContent(){
        rlProgress.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);
        llMainContent.setVisibility(View.VISIBLE);
    }
}
