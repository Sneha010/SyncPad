package com.nearby.syncpad;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.nearby.syncpad.data.ItemsContract;
import com.nearby.syncpad.models.Meeting;
import com.nearby.syncpad.util.Constants;
import com.nearby.syncpad.util.GeneralUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.nearby.syncpad.R.id.llContainer;
import static com.nearby.syncpad.R.id.tvAgendaValue;
import static com.nearby.syncpad.R.id.tvAttendeesValue;

public class MeetingsSaveActivity extends AppCompatActivity {

    private static final String TAG = "MeetingsSaveActivity";
    public static final String ACTION_DATA_UPDATED =
            "com.nearby.syncpad.ACTION_DATA_UPDATED";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(tvAgendaValue)
    TextView mTvAgendaValue;

    @BindView(llContainer)
    LinearLayout mLlContainer;

    @BindView(tvAttendeesValue)
    TextView mTvAttendeesValue;

    private Meeting mMeeting;

    @Inject
    DatabaseReference mDatabase;

    private Unbinder binder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetings_save);
        binder = ButterKnife.bind(this);

        ((SyncPadApplication) getApplication()).getMyApplicationComponent().inject(this);

        if (getIntent() != null) {
            mMeeting = getIntent().getExtras().getParcelable(Constants.MEETING);
        }

        init();
        displayMeetingDetails();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(mMeeting.getMeetingName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


    }

    private void displayMeetingDetails() {

        if (mMeeting != null) {

            if(!GeneralUtils.isEmpty(mMeeting.getMeetingAgenda()))
                mTvAgendaValue.setText(mMeeting.getMeetingAgenda());
            else
                mTvAgendaValue.setText(getString(R.string.na));

            if(!GeneralUtils.isEmpty(mMeeting.getParticipantNameList()))
                mTvAttendeesValue.setText(mMeeting.getParticipantNameList().replace("||","\n"));
            else
                mTvAttendeesValue.setText(getString(R.string.na));

        } else {

            mTvAgendaValue.setText(getString(R.string.na));
            mTvAttendeesValue.setText(getString(R.string.na));
        }

        showMeetingNotes();

    }

    private void showMeetingNotes() {

        String[] noteList = mMeeting.getNotesList().split("\\|\\|");

        if (noteList != null && noteList.length > 0) {
            for (int i = 0; i < noteList.length; i++) {
                TextView textView = new TextView(this);
                textView.setText(noteList[i]);
                textView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/gothambook.ttf"));

                if(i%2==0)
                    textView.setBackground(ContextCompat.getDrawable(this , R.drawable.notes_bg_pink));
                else
                    textView.setBackground(ContextCompat.getDrawable(this , R.drawable.notes_bg_blue));



                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0 , 8 ,0,0);
                textView.setPadding(15,15,15,15);
                textView.setTextSize(16);
                textView.setTextColor(ContextCompat.getColor(this , R.color.primaryTextColor));
                textView.setLayoutParams(params);
                mLlContainer.addView(textView, params);
            }
        } else {
            mLlContainer.setVisibility(View.GONE);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.save) {
            saveAndSyncMeeting();
            // TODO finish();
        } else {
            buildAlertDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveAndSyncMeeting() {

        syncWithFirebaseDb();
//        mDatabase.child("user-meetings").child(GeneralUtils.getUid()).addListenerForSingleValueEvent(
//                new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        // Get user value
//                        Meeting meeting = dataSnapshot.getValue(Meeting.class);
//
//                        // [START_EXCLUDE]
//                        if (meeting == null) {
//                            // User is null, error out
//                            Log.d(TAG, "onDataChange: Meeting is null");
//                        } else {
//                            Log.d(TAG, "onDataChange: meeting is not null added meeting is"+meeting.getMeetingName());
//                            // Write new post
//                            syncWithFirebaseDb();
//                        }
//
//                        // Finish this Activity, back to the stream
//                        finish();
//                        // [END_EXCLUDE]
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
//                    }
//                });


    }

    private void syncWithFirebaseDb() {
        final String userId = GeneralUtils.getUid();
        String key = mDatabase.child("user-meetings/" + userId).push().getKey();
        mMeeting.setMeetingId(key);
        mMeeting.setMeetingTimeStamp(GeneralUtils.getTimeInMillis(mMeeting.getMeetingDate(),
                mMeeting.getMeetingTime()));
        Map<String, Object> postValues = mMeeting.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/user-meetings/" + userId + "/" + key, postValues);
        mDatabase.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, "onComplete: error");
                } else {
                    finish();
                }
            }
        });

        //addMeetingToDb(mMeeting);
        //pdateWidgets();
    }

    private void addMeetingToDb(Meeting meeting) {
        try {

            ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();
            Uri dirUri = ItemsContract.Items.buildDirUri();

            ContentValues values = GeneralUtils.getContentValues(new JSONObject(new Gson().toJson(meeting)));

            cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateWidgets() {
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(getApplicationContext().getPackageName());
        sendBroadcast(dataUpdatedIntent);
    }

    private void buildAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_not_to_save)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener).show();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        buildAlertDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        binder.unbind();
    }
}
