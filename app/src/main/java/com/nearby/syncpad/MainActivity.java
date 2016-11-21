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
import android.support.v7.app.AppCompatActivity;
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
import com.nearby.syncpad.callbacks.DismissScanDialogListener;
import com.nearby.syncpad.data.MeetingNotesLoader;
import com.nearby.syncpad.data.UpdaterService;
import com.nearby.syncpad.fragments.AddMeetingDialogFragment;
import com.nearby.syncpad.fragments.ScanMeetingsDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.nearby.syncpad.R.id.tvMeetingTitle;

public class MainActivity extends AppCompatActivity implements DismissScanDialogListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private LinearLayout llNoMeetingsAdded;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mMeetingRycyclerView;
    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton fab_StartMeeting;
    private FloatingActionButton fab_JoinMeeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLoaderManager().initLoader(0, null, this);
        init();

        if (savedInstanceState == null) {
            refresh();
        }
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }


    public void init() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        llNoMeetingsAdded = (LinearLayout) findViewById(R.id.ll_no_meetings);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mMeetingRycyclerView = (RecyclerView) findViewById(R.id.meetingsListView);
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.floatingActionMenu);
        fab_StartMeeting = (FloatingActionButton) findViewById(R.id.menu_item1);
        fab_JoinMeeting = (FloatingActionButton) findViewById(R.id.menu_item2);

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
        refresh();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }


    private boolean mIsRefreshing = false;

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
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
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

            startActivity(new Intent(MainActivity.this, MyProfileActivity.class));

            return true;
        } else {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    AddMeetingDialogFragment addMeetingDialogFragment;

    private void startMeeting_Dialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("meeting_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        addMeetingDialogFragment = AddMeetingDialogFragment.newInstance();
        addMeetingDialogFragment.show(ft, "meeting_dialog");
    }

    ScanMeetingsDialogFragment dFragment;

    private void scanNearbyMeetings() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("scan_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        dFragment = ScanMeetingsDialogFragment.newInstance();
        dFragment.show(ft, "scan_dialog");

    }

    @Override
    public void dismissDialog() {
        if (dFragment != null) {
            dFragment.dismiss();
        }
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return MeetingNotesLoader.newAllNotesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d("@@@", "onLoadFinished: ");
        Adapter adapter = new Adapter(cursor);
        adapter.setHasStableIds(true);
        mMeetingRycyclerView.setAdapter(adapter);
        // int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mMeetingRycyclerView.setLayoutManager(sglm);
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mMeetingRycyclerView != null)
            mMeetingRycyclerView.setAdapter(null);
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

                    Intent i = new Intent(MainActivity.this , MeetingDetailsActivity.class);
                    i.putExtra("item_id" , mCursor.getString(MeetingNotesLoader.Query.MEETING_ID));
                    startActivity(i);

                   /* startActivity(new Intent(Intent.ACTION_VIEW,
                            ItemsContract.Items.buildItemUri(mCursor.getString(MeetingNotesLoader.Query.MEETING_ID))));*/
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            mCursor.moveToPosition(position);
            holder.mTvMeetingTitle.setText(mCursor.getString(MeetingNotesLoader.Query.MEETING_NAME));
            holder.mTvNotes.setText(mCursor.getString(MeetingNotesLoader.Query.MEETING_NOTES));
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(tvMeetingTitle)
        TextView mTvMeetingTitle;
        @BindView(R.id.tvNotes)
        TextView mTvNotes;
        @BindView(R.id.rlContentView)
        RelativeLayout mRlContentView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
