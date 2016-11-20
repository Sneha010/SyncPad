package com.nearby.syncpad;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Point;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.nearby.syncpad.callbacks.DismissScanDialogListener;
import com.nearby.syncpad.data.MeetingNotesLoader;
import com.nearby.syncpad.data.UpdaterService;
import com.nearby.syncpad.fragments.AddMeetingDialogFragment;
import com.nearby.syncpad.fragments.ScanMeetingsDialogFragment;
import com.nearby.syncpad.models.Meeting;
import com.nearby.syncpad.util.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
                startMeetingDialog();
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

    private Dialog dialog;
    EditText edtName, edtDate, edtTime, edtVenue, edtAgenda;
    TextView tvCancel, tvAdd, tv_selectDateButton, tv_selectTimeButton;

    private void startMeetingDialog() {

        dialog = new Dialog(this);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;

        View view = View.inflate(this, R.layout.add_meeting_dialog_layout, null);

        edtName = (EditText) view.findViewById(R.id.edtMeetingName);
        edtDate = (EditText) view.findViewById(R.id.edtMeetingDate);
        edtTime = (EditText) view.findViewById(R.id.edtMeetingTime);
        edtVenue = (EditText) view.findViewById(R.id.edtMeetingVenue);
        edtAgenda = (EditText) view.findViewById(R.id.edtMeetingAgenda);
        tvCancel = (TextView) view.findViewById(R.id.tvCancel);
        tvAdd = (TextView) view.findViewById(R.id.tvAdd);
        tv_selectDateButton = (TextView) view.findViewById(R.id.select_date_button);
        tv_selectTimeButton = (TextView) view.findViewById(R.id.select_time_button);

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtName.getText() == null || (edtName.getText() != null && edtName.getText().toString().length() == 0)) {
                    Toast.makeText(MainActivity.this, "Please enter meeting title", Toast.LENGTH_SHORT).show();
                } else {
                    Meeting meetingBean = new Meeting();
                    if (edtName.getText() != null && edtName.getText().toString().length() > 0) {
                        meetingBean.setMeetingName(edtName.getText().toString());
                    }
                    if (edtDate.getText() != null && edtDate.getText().toString().length() > 0) {
                        meetingBean.setMeetingDate(edtDate.getText().toString());
                    }
                    if (edtTime.getText() != null && edtTime.getText().toString().length() > 0) {
                        meetingBean.setMeetingTime(edtTime.getText().toString());
                    }
                    if (edtVenue.getText() != null && edtVenue.getText().toString().length() > 0) {
                        meetingBean.setMeetingVenue(edtVenue.getText().toString());
                    }
                    if (edtAgenda.getText() != null && edtAgenda.getText().toString().length() > 0) {
                        meetingBean.setMeetingAgenda(edtAgenda.getText().toString());
                    }

                    Intent i = new Intent(MainActivity.this, ActiveMeetingActivity.class);
                    i.putExtra(Constants.MEETING, meetingBean);
                    i.putExtra(Constants.IS_HOST, true);
                    startActivity(i);
                    dialog.dismiss();
                }

            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tv_selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Use the current date as the default date in the picker
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        edtDate.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();

            }
        });

        tv_selectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        SimpleDateFormat format = new SimpleDateFormat("HH:MM");
                        try {
                            Date date = format.parse("" + hourOfDay + ":" + minute);
                            edtTime.setText(new SimpleDateFormat("hh:mm aa").format(date.getTime()));
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        dialog.getWindow().

                requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.dialog_bg));

        dialog.setContentView(view);
        dialog.show();

        dialog.getWindow().

                setLayout((6 * width)

                                / 7,
                        WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(false);
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

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            mCursor.moveToPosition(position);
            holder.mTvMeetingTitle.setText(mCursor.getString(MeetingNotesLoader.Query.MEETING_NAME));
            holder.mTvNotes.setText(mCursor.getString(MeetingNotesLoader.Query.MEETING_NOTES));

            final Meeting detailBean = new Meeting();
            detailBean.setMeetingName(mCursor.getString(MeetingNotesLoader.Query.MEETING_NAME));
            detailBean.setMeetingDate(mCursor.getString(MeetingNotesLoader.Query.MEETING_DATE));
            detailBean.setMeetingTime(mCursor.getString(MeetingNotesLoader.Query.MEETING_TIME));
            detailBean.setMeetingVenue(mCursor.getString(MeetingNotesLoader.Query.MEETING_VENUE));
            detailBean.setMeetingAgenda(mCursor.getString(MeetingNotesLoader.Query.MEETING_AGENDA));
            detailBean.setNotesList(mCursor.getString(MeetingNotesLoader.Query.MEETING_NOTES));
            detailBean.setParticipantNameList(mCursor.getString(MeetingNotesLoader.Query.MEETING_PARTICIPANTS));

            holder.mRlContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Intent i = new Intent(MainActivity.this , MeetingDetailsActivity.class);
                    i.putExtra(Constants.MEETING , detailBean);
                    startActivity(i);
                }
            });

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
