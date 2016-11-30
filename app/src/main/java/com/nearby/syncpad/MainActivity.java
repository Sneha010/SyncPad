package com.nearby.syncpad;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.nearby.syncpad.data.MeetingNotesLoader;
import com.nearby.syncpad.data.UpdaterService;
import com.nearby.syncpad.fragments.AddMeetingDialogFragment;
import com.nearby.syncpad.fragments.ScanMeetingsDialogFragment;
import com.nearby.syncpad.util.Constants;
import com.nearby.syncpad.util.GeneralUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.meetingsListView)
    RecyclerView meetingRycyclerView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.ll_no_meetings)
    LinearLayout llNoMeetingsAdded;

    @BindView(R.id.menu_item1)
    FloatingActionButton fab_StartMeeting;

    @BindView(R.id.menu_item2)
    FloatingActionButton fab_JoinMeeting;

    @BindView(R.id.floatingActionMenu)
    FloatingActionMenu floatingActionMenu;

    private boolean isDataAvailable;

    private boolean mIsRefreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUnbinder = ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        getLoaderManager().initLoader(0, null, this);

        init();

        if (savedInstanceState == null) {
            refresh();
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isDataAvailable)
                    refresh();
                else
                    swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void init() {

        llNoMeetingsAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMeeting_Dialog();
            }
        });

        fab_StartMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMeeting_Dialog();
                floatingActionMenu.close(true);
            }
        });

        fab_JoinMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanNearbyMeetings();
                floatingActionMenu.close(true);
            }
        });
    }


    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("@@@", "onStart: mainActivity");

        if(!isDataAvailable){
            swipeRefreshLayout.setRefreshing(true);
            refresh();
        }

        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }




    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }
    };

    private void updateRefreshingUI() {
        swipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profile) {

            startActivity(new Intent(MainActivity.this, MyProfileActivity.class));

            return true;
        } else {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



    private void startMeeting_Dialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(getString(R.string.meeting_dialog));
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        AddMeetingDialogFragment addMeetingDialogFragment = AddMeetingDialogFragment.newInstance();
        addMeetingDialogFragment.show(ft, getString(R.string.meeting_dialog));
    }


    private void scanNearbyMeetings() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(getString(R.string.scan_dialog));
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        ScanMeetingsDialogFragment dFragment = ScanMeetingsDialogFragment.newInstance();
        dFragment.show(ft, getString(R.string.scan_dialog));

    }



    private void displayNoNnotes(){
        llNoMeetingsAdded.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.GONE);
    }

    private void displayMeetingList(){
        llNoMeetingsAdded.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return MeetingNotesLoader.newAllNotesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d("@@@", "onLoadFinished: ");

        if (cursor != null && cursor.getCount() > 0) {
            Log.d("@@@", "onLoadFinished: cursor not null");
            displayMeetingList();
            Adapter adapter = new Adapter(cursor);
            adapter.setHasStableIds(true);
            meetingRycyclerView.setAdapter(adapter);
            StaggeredGridLayoutManager sglm =
                    new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            meetingRycyclerView.setLayoutManager(sglm);
            swipeRefreshLayout.setRefreshing(mIsRefreshing);
            isDataAvailable = true;

        }
        else{
            Log.d("@@@", "onLoadFinished: cursor null");
            isDataAvailable = false;
            displayNoNnotes();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (meetingRycyclerView != null){
            isDataAvailable = false;
            meetingRycyclerView.setAdapter(null);
        }

    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private Cursor mCursor;

        public Adapter(Cursor cursor) {
            mCursor = cursor;
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(MeetingNotesLoader.Query.MEETING_ID);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.meetings_list_item, parent, false);
            final ViewHolder vh = new ViewHolder(view);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mCursor.moveToPosition(vh.getAdapterPosition());

                    Intent i = new Intent(MainActivity.this, MeetingDetailsActivity.class);
                    i.putExtra(Constants.ITEM_ID, mCursor.getString(MeetingNotesLoader.Query.MEETING_ID));
                    startActivity(i);
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            mCursor.moveToPosition(position);
            holder.tvMeetingTitle.setText(mCursor.getString(MeetingNotesLoader.Query.MEETING_NAME));
            holder.tvDate.setText(GeneralUtils.getFormattedDate(mCursor.getString(MeetingNotesLoader.Query.MEETING_DATE)));
            holder.tvTime.setText(mCursor.getString(MeetingNotesLoader.Query.MEETING_TIME));
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvMeetingTitle)
        TextView tvMeetingTitle;

        @BindView(R.id.tvDate)
        TextView tvDate;

        @BindView(R.id.tvTime)
        TextView tvTime;

        @BindView(R.id.rlMainContentView)
        RelativeLayout rlMainContentView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
