package com.nearby.syncpad;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nearby.syncpad.callbacks.AddMeetingListener;
import com.nearby.syncpad.data.ItemsContract;
import com.nearby.syncpad.models.Meeting;
import com.nearby.syncpad.util.Constants;
import com.nearby.syncpad.util.GeneralUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MeetingsSaveActivity extends AppCompatActivity {

    private static final String TAG = "MeetingsSaveActivity";
    private Meeting mMeeting;
    private AddMeetingListener mAddMeetingListener;
    private DatabaseReference mDatabase;
    private TextView tvNotes , tvParticipants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetings_save);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        if(getIntent()!=null){
            mMeeting = getIntent().getExtras().getParcelable(Constants.MEETING);
        }

        init();
        displayMeetingDetails();
    }



    public void init(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(mMeeting.getMeetingName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        tvNotes = (TextView) findViewById(R.id.tvNotes);
        tvParticipants = (TextView) findViewById(R.id.tvParticipant);


    }
    private void displayMeetingDetails() {

        //TODO display meeting using bean

            tvNotes.setText(mMeeting.getNotesList());

            tvParticipants.setText(mMeeting.getParticipantNameList());
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
        }else{
           buildAlertDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveAndSyncMeeting(){

        syncWithFirebaseDb();
        finish();
/*
        mDatabase.child("user-meetings").child(GeneralUtils.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Meeting meeting = dataSnapshot.getValue(Meeting.class);

                        // [START_EXCLUDE]
                        if (meeting == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + GeneralUtils.getUid() + " is unexpectedly null");
                            Toast.makeText(MeetingsSaveActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            syncWithFirebaseDb();
                        }

                        // Finish this Activity, back to the stream
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });*/


    }

    private void syncWithFirebaseDb() {
        final String userId = GeneralUtils.getUid();
        String key = mDatabase.child("user-meetings/"+userId).push().getKey();
        mMeeting.setMeetingId(key);
        Map<String, Object> postValues = mMeeting.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/user-meetings/" + userId + "/" + key, postValues);
        mDatabase.updateChildren(childUpdates);

        addMeetingToDb(mMeeting);
    }

    private void addMeetingToDb(Meeting meeting){
        try {

        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();
        Uri dirUri = ItemsContract.Items.buildDirUri();

        ContentValues values = null;
            values = GeneralUtils.getContentValues(new JSONObject(new Gson().toJson(meeting)));

        cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    private void buildAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_not_to_save)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener).show();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
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


}
