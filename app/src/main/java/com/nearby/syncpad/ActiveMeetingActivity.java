package com.nearby.syncpad;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.NearbyMessagesStatusCodes;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.nearby.syncpad.adapter.ChatListItemAdapter;
import com.nearby.syncpad.fragments.ParticipantsFragment;
import com.nearby.syncpad.models.Meeting;
import com.nearby.syncpad.models.Participant;
import com.nearby.syncpad.storedata.ProfileStore;
import com.nearby.syncpad.util.Constants;
import com.nearby.syncpad.util.DataItemDecoration;
import com.nearby.syncpad.util.GeneralUtils;
import com.nearby.syncpad.util.ImageUtility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActiveMeetingActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "ActiveMeetingActivity";


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.chatParticipantListView)
    RecyclerView mRecyclerView;

    @BindView(R.id.startMeetingText)
    TextView startMeetingText;

    @BindView(R.id.edtMeetingNotes)
    EditText edtMeetingNotes;

    @BindView(R.id.rl_ParticipantsAttendance)
    RelativeLayout rl_ParticipantsAttendanceSlidingView;

    @BindView(R.id.send_button)
    ImageView btnSend;

    @BindView(R.id.ivImgCross)
    ImageView ivImgCross;

    @Inject
    ImageUtility mImageUtility;

    @Inject
    PublishOptions mPublishOptions;

    @Inject
    SubscribeOptions mSubscribeOptions;

    @Inject
    ProfileStore mProfileStore;

    private ArrayList<Participant> chatParticipantsList;
    private ChatListItemAdapter adapter;
    private GoogleApiClient mGoogleApiClient;
    private Message mProfileInformation, myNotes;
    private boolean mIsHost;
    private Meeting mCurrentMeeting;
    private boolean mResolvingNearbyPermissionError = false;
    Animation animationIn;
    private ParticipantsFragment participantListFragment;
    private ArrayList<String> noteList = new ArrayList<>();
    private ArrayList<String> participantNameList = new ArrayList<>();
    private MessageListener mMessageListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.broadcast_activity_layout);
        ButterKnife.bind(this);

        ((SyncPadApplication) getApplication()).getMyApplicationComponent().inject(this);

        if (getIntent() != null) {
            mCurrentMeeting = getIntent().getExtras().getParcelable(Constants.MEETING);
            mIsHost = getIntent().getBooleanExtra(Constants.IS_HOST, false);
        }

        init();
        setUpUI();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void init() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(mCurrentMeeting.getMeetingName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishNotes();
            }
        });

        ivImgCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeParticipantsSlidingLayout();
            }
        });

    }

    public void setUpUI() {

        setUpRecyclerView();
        setMessageListener();
        addParticipantListFragment();

        startMeetingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((TextView) view).getText().equals("Stop Meeting")) {
                    stopMeeting();
                } else if (((TextView) view).getText().equals("Start Meeting")) {
                    startMeeting();
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart called");
        Participant participant = new Participant();
        participant.setName(mProfileStore.getMyProfile().getName());
        participant.setRole(mProfileStore.getMyProfile().getRole());
        participant.setEmailAddress(mProfileStore.getMyProfile().getEmailAddress());
        participant.setOrganisation(mProfileStore.getMyProfile().getOrganisation());
        participant.setAttendance("present");
        participant.setIsHost(mIsHost);
        participant.setMeeting(mCurrentMeeting);
        participant.setImageBytes(mProfileStore.getMyProfile().getImageBytes());

        participantNameList.add(participant.getName());

        if (participantListFragment.isAdded()) {

            participantListFragment.addParticipant(participant);
        }

        mProfileInformation = participant.newNearbyMessage();


    }

    private void buildGoogleApiClient() {

        Log.d(TAG, "buildGoogleApiClient: Trying to connect to nearby");

        if (mGoogleApiClient != null) {
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .build();
        mGoogleApiClient.connect();

    }

    //All related to NearBy Message API
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected called");

        startMeeting();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended: "
                + connectionSuspendedCauseToString(cause));
        GeneralUtils.displayCustomToast(ActiveMeetingActivity.this,
                getString(R.string.nearby_connection_error));
        stopMeeting();
    }

    private static String connectionSuspendedCauseToString(int cause) {
        switch (cause) {
            case CAUSE_NETWORK_LOST:
                return "CAUSE_NETWORK_LOST";
            case CAUSE_SERVICE_DISCONNECTED:
                return "CAUSE_SERVICE_DISCONNECTED";
            default:
                return "CAUSE_UNKNOWN: " + cause;
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "connection to GoogleApiClient failed");

        GeneralUtils.displayCustomToast(ActiveMeetingActivity.this,
                getString(R.string.nearby_connection_error));

        stopMeeting();

    }

    private void startMeeting() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

            GeneralUtils.displayCustomToast(ActiveMeetingActivity.this, "Meeting Started");
            startMeetingText.setText("Stop Meeting");
            startMeetingText.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(ActiveMeetingActivity.this, R.drawable.stop_btn), null, null, null);
            startMeetingText.setCompoundDrawablePadding(5);
            startNearByAPI();
        } else {
            buildGoogleApiClient();
        }
    }

    private void stopMeeting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_end_meeting)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener).show();

    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    GeneralUtils.displayCustomToast(ActiveMeetingActivity.this, "Meeting Stopped");
                    startMeetingText.setText("Start Meeting");
                    startMeetingText.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(ActiveMeetingActivity.this, R.drawable.start_btn), null, null, null);
                    startMeetingText.setCompoundDrawablePadding(5);
                    stopNearByAPI();
                    generateMeetingMOM();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };

    private void generateMeetingMOM() {
        mCurrentMeeting.setNotesList(convertToStringFromArrayList(noteList));
        mCurrentMeeting.setParticipantNameList(convertToStringFromArrayList(participantNameList));

        Intent i = new Intent(ActiveMeetingActivity.this, MeetingsSaveActivity.class);
        i.putExtra(Constants.MEETING, mCurrentMeeting);
        startActivity(i);
        finish();
    }

    //Forming the list of comma separated string
    private String convertToStringFromArrayList(ArrayList<String> list) {
        String converted_string = "";

        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                converted_string = converted_string + list.get(i) + "||";
            }

        }
        return converted_string;
    }

    public void setMessageListener() {
        mMessageListener = new MessageListener() {
            @Override
            public void onFound(final Message message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.i(TAG, "setMessageListener called");

                        //get message from this, use it later to get profile data and other stuff
                        Participant participant = Participant.fromNearbyMessage(message);
                        participant.setToWhom("from_other");

                        if (participantListFragment.isAdded() && (participant.getAttendance() != null && participant.getAttendance().equals("present"))) {
                            Log.i(TAG, "participant addded");
                            GeneralUtils.displayCustomToast(ActiveMeetingActivity.this, participant.getName());
                            participantListFragment.addParticipant(participant);
                            participantNameList.add(participant.getName());
                        } else {
                            noteList.add(participant.getMeetingNotes());
                            adapter.updateList(participant);
                        }

                        publish_MyProfile();


                    }
                });
            }

            @Override
            public void onLost(final Message message) {
                // Called when a message is no longer detectable nearby.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Remove from list of participants

                        Log.d("", "###@@@@ #@#@# Message Lost");

                    }
                });
            }
        };

        Log.d("", "@@@@ Created listener " + mMessageListener);

    }

    private void addParticipantListFragment() {

        participantListFragment = ParticipantsFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFrame, participantListFragment, "Participant");
        transaction.commit();

    }

    public void setUpRecyclerView() {
        Log.i(TAG, "setUpRecyclerView called");

        mRecyclerView.addItemDecoration(new DataItemDecoration(this, DataItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chatParticipantsList = new ArrayList<>();
        adapter = new ChatListItemAdapter(ActiveMeetingActivity.this, chatParticipantsList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void publishNotes() {

        Log.i(TAG, "publishNotes called");

        InputMethodManager imm = (InputMethodManager) this
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null)
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

        if (edtMeetingNotes.getText() != null && edtMeetingNotes.getText().toString().length() > 0) {

            Participant participant = new Participant();
            participant.setName(mProfileStore.getMyProfile().getName());
            participant.setMeetingNotes(edtMeetingNotes.getText().toString());
            noteList.add(edtMeetingNotes.getText().toString());
            participant.setToWhom("to_Me");

            myNotes = participant.newNearbyMessage();

            adapter.updateList(participant);

            edtMeetingNotes.setText("");
            publish_MyNotes();
        }
    }


    private void startNearByAPI() {
        //Add code to start publishing

        Log.i(TAG, "startNearByAPI called");

        subscribe();
        publishMyData();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void stopNearByAPI() {
        //Add code to start publishing

        Log.i(TAG, "stopNearByAPI called");

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            unsubscribe();
            unpublishMyData();
        }

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

            openParticipantsSlidingLayout();
            return true;
        } else {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    private void publishMyData() {
        if (mProfileInformation != null)
            publish_MyProfile();
        if (myNotes != null)
            publish_MyNotes();
    }


    private void unpublishMyData() {
        if (mProfileInformation != null)
            unpublish_MyProfile();
        if (myNotes != null)
            unpublish_MyNotes();
    }

    /**
     * Subscribes to messages from nearby devices. If not successful, attempts to resolve any error
     * related to Nearby permissions by displaying an opt-in dialog. Registers a callback which
     * updates state when the subscription expires.
     */
    private void subscribe() {
        Log.i(TAG, "subscribe called");
        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {

            Log.d("", "@@@@" + mMessageListener);

            Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, mSubscribeOptions)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "subscribed successfully");
                            } else {
                                Log.i(TAG, "could not subscribe");
                                handleUnsuccessfulNearbyResult(status);
                            }
                        }
                    });
        }
    }

    /**
     * Ends the subscription to messages from nearby devices. If successful, resets state. If not
     * successful, attempts to resolve any error related to Nearby permissions by
     * displaying an opt-in dialog.
     */
    private void unsubscribe() {
        Log.i(TAG, "unsubscribe called");
        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "unsubscribed successfully");
                            } else {
                                Log.i(TAG, "could not unsubscribe");
                                handleUnsuccessfulNearbyResult(status);
                            }
                        }
                    });
        }
    }

    /**
     * Publishes device information to nearby devices. If not successful, attempts to resolve any
     * error related to Nearby permissions by displaying an opt-in dialog. Registers a callback
     * that updates the UI when the publication expires.
     */
    private void publish_MyProfile() {
        Log.i(TAG, "publish called");
        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (mGoogleApiClient == null || (mGoogleApiClient != null && !mGoogleApiClient.isConnected())) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {

            Nearby.Messages.publish(mGoogleApiClient, mProfileInformation, mPublishOptions)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "Profile published successfully");
                            } else {
                                Log.i(TAG, "could not publish");
                                handleUnsuccessfulNearbyResult(status);
                            }
                        }
                    });
        }
    }

    private void publish_MyNotes() {
        Log.i(TAG, "publish called");
        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            if (mGoogleApiClient == null) {

                GeneralUtils.displayCustomToast(ActiveMeetingActivity.this, getString(R.string.meeting_not_started));
            } else {
                Nearby.Messages.publish(mGoogleApiClient, myNotes, mPublishOptions)
                        .setResultCallback(new ResultCallback<Status>() {

                            @Override
                            public void onResult(Status status) {
                                if (status.isSuccess()) {
                                    Log.i(TAG, "published successfully");
                                } else {
                                    Log.i(TAG, "could not publish");
                                    handleUnsuccessfulNearbyResult(status);
                                }
                            }
                        });
            }

        }
    }

    /**
     * Stops publishing device information to nearby devices. If successful, resets state. If not
     * successful, attempts to resolve any error related to Nearby permissions by displaying an
     * opt-in dialog.
     */
    private void unpublish_MyProfile() {
        Log.i(TAG, "unpublish called");
        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            Nearby.Messages.unpublish(mGoogleApiClient, mProfileInformation)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "unpublish successful");
                            } else {
                                Log.i(TAG, "could not unpublish");
                                handleUnsuccessfulNearbyResult(status);
                            }
                        }
                    });
        }
    }

    private void unpublish_MyNotes() {
        Log.i(TAG, "unpublish called");
        // Cannot proceed without a connected GoogleApiClient. Reconnect and execute the pending
        // task in onConnected().
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            Nearby.Messages.unpublish(mGoogleApiClient, myNotes)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "unpublish successful");
                            } else {
                                Log.i(TAG, "could not unpublish");
                                handleUnsuccessfulNearbyResult(status);
                            }
                        }
                    });
        }
    }

    /**
     * Handles errors generated when performing a subscription or publication action. Uses
     * {@link Status#startResolutionForResult} to display an opt-in dialog to handle the case
     * where a device is not opted into using Nearby.
     */
    private void handleUnsuccessfulNearbyResult(Status status) {
        Log.i(TAG, "handleUnsuccessfulNearbyResult called" + status);
        if (status.getStatusCode() == NearbyMessagesStatusCodes.APP_NOT_OPTED_IN) {
            if (!mResolvingNearbyPermissionError) {
                try {
                    mResolvingNearbyPermissionError = true;
                    status.startResolutionForResult(this,
                            Constants.REQUEST_RESOLVE_ERROR);

                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (status.getStatusCode() == ConnectionResult.NETWORK_ERROR) {
                GeneralUtils.displayCustomToast(ActiveMeetingActivity.this, getString(R.string.no_internet_connectivity));
            } else {
                // To keep things simple, pop a toast for all other error messages.
                GeneralUtils.displayCustomToast(ActiveMeetingActivity.this, getString(R.string.unsuccessful) + " " + status.getStatusMessage());
            }

        }
    }


    @Override
    protected void onStop() {

        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && !isChangingConfigurations()) {
            // Using Nearby is battery intensive. To preserve battery, stop subscribing or
            // publishing when the fragment is inactive.
            unsubscribe();
            unpublishMyData();

        }
    }


    public void openParticipantsSlidingLayout() {
        rl_ParticipantsAttendanceSlidingView.setVisibility(rl_ParticipantsAttendanceSlidingView.VISIBLE);
        rl_ParticipantsAttendanceSlidingView.bringToFront();
        animationIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.right_to_left_in);

        rl_ParticipantsAttendanceSlidingView.startAnimation(animationIn);
    }

    public void closeParticipantsSlidingLayout() {
        rl_ParticipantsAttendanceSlidingView.setVisibility(rl_ParticipantsAttendanceSlidingView.GONE);
        animationIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.left_to_right_out);

        rl_ParticipantsAttendanceSlidingView.startAnimation(animationIn);

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_end_meeting)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
            })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }
}
