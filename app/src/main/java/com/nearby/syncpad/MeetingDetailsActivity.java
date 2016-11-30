package com.nearby.syncpad;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nearby.syncpad.data.MeetingNotesLoader;
import com.nearby.syncpad.fragments.MeetingInfoFragment;
import com.nearby.syncpad.fragments.MeetingNotesFragment;
import com.nearby.syncpad.models.Meeting;
import com.nearby.syncpad.util.Constants;
import com.nearby.syncpad.util.GeneralUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Sneha Khadatare on 11/20/2016.
 */

public class MeetingDetailsActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

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
                mItemId = getIntent().getExtras().getString(Constants.ITEM_ID);
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
            meeting.setMeetingNotes(mCursor.getString(MeetingNotesLoader.Query.MEETING_NOTES));
            meeting.setMeetingParticipants(mCursor.getString(MeetingNotesLoader.Query.MEETING_PARTICIPANTS));
            mMeeting = meeting;

            showContent();
            displayMeetingContent(meeting);

        } else {
            showError();
        }


    }

    private void displayMeetingContent(Meeting meeting) {

        getSupportActionBar().setTitle(meeting.getMeetingName());

        ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();

        fragmentList.add(MeetingInfoFragment.newInstance(mMeeting));
        fragmentList.add(MeetingNotesFragment.newInstance(meeting.getMeetingNotes()));

        mAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewpager.setAdapter(mAdapter);
        viewpager.setOffscreenPageLimit(fragmentList.size());
        tabLayout.setupWithViewPager(viewpager);

        GeneralUtils.applyFontedTab(MeetingDetailsActivity.this, viewpager, tabLayout);
    }


    class MyPagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<Fragment> fragmentList;
        String[] titleArray = {Constants.MEETING_INFO, Constants.MEETING_NOTES};

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


    private void showProgress() {
        rlProgress.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        llMainContent.setVisibility(View.GONE);
    }

    private void showError() {
        rlProgress.setVisibility(View.GONE);
        tvError.setVisibility(View.VISIBLE);
        llMainContent.setVisibility(View.GONE);
    }

    private void showContent() {
        rlProgress.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);
        llMainContent.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.share) {

            createShareChooser();
            return true;
        } else {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void createShareChooser() {

        String venue , agenda , notes ;
        venue = !GeneralUtils.isEmpty(mMeeting.getMeetingVenue()) ? mMeeting.getMeetingVenue() : "NA";
        agenda =  !GeneralUtils.isEmpty(mMeeting.getMeetingAgenda()) ? mMeeting.getMeetingAgenda() : "NA";
        notes =  !GeneralUtils.isEmpty(mMeeting.getMeetingNotes())? mMeeting.getMeetingNotes().replace("||", "\n") : "";

        String shareBody = mMeeting.getMeetingDate() + " at " + mMeeting.getMeetingTime() + "\n" +
                "Venue : "+venue +"\n"+ "Agenda : "+ agenda +"\n"+ "Points : " + notes;

        Log.d(TAG, "createShareChooser: "+shareBody);

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "MOM of "+mMeeting.getMeetingName());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_via)));
    }
}
