package com.nearby.syncpad;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.SharedPreferences;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.google.gson.Gson;
import com.nearby.syncpad.adapter.ParticipantListItemAdapter;
import com.nearby.syncpad.fragments.ParticipantsFragment;
import com.nearby.syncpad.models.Meeting;
import com.nearby.syncpad.models.MeetingNote;
import com.nearby.syncpad.models.Participant;
import com.nearby.syncpad.storedata.ProfileStore;
import com.nearby.syncpad.util.Constants;
import com.nearby.syncpad.util.DataItemDecoration;
import com.nearby.syncpad.util.GeneralUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class ActiveMeetingActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, SharedPreferences.OnSharedPreferenceChangeListener
{

    private static final String TAG = "ActiveMeetingActivity";

    private ArrayList<Participant> chatParticipantsList;
    RecyclerView mRecyclerView;
    private ParticipantListItemAdapter adapter;
    private boolean isMeetingStarted;
    private GoogleApiClient mGoogleApiClient;
    private Message mProfileInformation , myNotes;
    private boolean mIsHost;
    private Meeting mCurrentMeeting;
    private ImageView btnSend , ivImgCross;
    private boolean mResolvingNearbyPermissionError = false;
    private String mProfileImageBytes;
    TextView startMeetingText;
    EditText edtMeetingNotes;
    RelativeLayout rl_ParticipantsAttendanceSlidingView;
    Animation animationIn;
    private ParticipantsFragment participantListFragment;
    private ArrayList<MeetingNote> noteList = new ArrayList<>();
    private ArrayList<String> participantNameList = new ArrayList<>();

    /**
     * A {@link MessageListener} for processing messages from nearby devices.
     */
    private MessageListener mMessageListener;

    /**
     * Sets the time in seconds for a published message or a subscription to live. Set to five
     * minutes.
     */
    private static final Strategy PUB_SUB_STRATEGY = new Strategy.Builder()
            .setTtlSeconds(Constants.TTL_IN_SECONDS)
            .setDistanceType(Strategy.DISTANCE_TYPE_EARSHOT).build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.broadcast_activity_layout);

        if(getIntent()!=null){
            mCurrentMeeting = getIntent().getExtras().getParcelable(Constants.MEETING);
            mIsHost = getIntent().getBooleanExtra(Constants.IS_HOST , false);
        }

        init();
        setUpUI();
    }


    private void init() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(mCurrentMeeting.getMeetingName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.chatParticipantListView) ;
        startMeetingText = (TextView) findViewById(R.id.startMeetingText);
        edtMeetingNotes = (EditText) findViewById(R.id.edtMeetingNotes);
        rl_ParticipantsAttendanceSlidingView = (RelativeLayout) findViewById(R.id.rl_ParticipantsAttendance);
        btnSend = (ImageView) findViewById(R.id.send_button);
        ivImgCross = (ImageView) findViewById(R.id.ivImgCross);

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

    public void setUpUI(){


      /*  TODO Profile picture
      if (ProfileStore.getImagePath(this) != null) {
            new UpdatePicTask().execute(ProfileStore.getImagePath(this), "");
        }
*/
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
        getPreferences(Context.MODE_PRIVATE)
                .registerOnSharedPreferenceChangeListener(this);

        Participant participant = new Participant();
        participant.setName(ProfileStore.getUserName(this));
        participant.setRole(ProfileStore.getUserRole(this));
        participant.setEmailAddress(ProfileStore.getEmailAddress(this));
        participant.setAttendance("present");
        participant.setIsHost(mIsHost);
        participant.setMeeting(mCurrentMeeting);

        participantNameList.add(participant.getName());

      /*  TODO Profile picture
      if(uploadBitmap != null){
            participant.setImageBytes(
                    GeneralUtils.getProfileImageBytes(this, uploadBitmap));
        }else{
            uploadBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.default_user);
            participant.setImageBytes(
                    GeneralUtils.getProfileImageBytes(this, uploadBitmap));
        }*/

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
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    //All related to NearBy Message API
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected called");


        startMeeting();
        //executePendingTasks();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended: "
                + connectionSuspendedCauseToString(cause));
        GeneralUtils.displayCustomToast(ActiveMeetingActivity.this ,
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

        GeneralUtils.displayCustomToast(ActiveMeetingActivity.this ,
                getString(R.string.nearby_connection_error));

        stopMeeting();

    }

    private void startMeeting(){
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

            GeneralUtils.displayCustomToast(ActiveMeetingActivity.this, "Meeting Started");
            startMeetingText.setText("Stop Meeting");
            startMeetingText.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(ActiveMeetingActivity.this, R.drawable.stop_btn), null, null, null);
            startMeetingText.setCompoundDrawablePadding(5);
            isMeetingStarted = true;
            startNearByAPI();
        }
        else{
            buildGoogleApiClient();
        }
    }

    private void stopMeeting(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_end_meeting)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener).show();

    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    GeneralUtils.displayCustomToast(ActiveMeetingActivity.this , "Meeting Stopped");
                    startMeetingText.setText("Start Meeting");
                    startMeetingText.setCompoundDrawablesWithIntrinsicBounds(
                            ContextCompat.getDrawable(ActiveMeetingActivity.this , R.drawable.start_btn), null, null, null);
                    startMeetingText.setCompoundDrawablePadding(5);
                    isMeetingStarted = false;
                    generateMeetingMOM();
                    stopNearByAPI();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };

    private void generateMeetingMOM(){
        mCurrentMeeting.setNotesList(noteList);
        mCurrentMeeting.setParticipantNameList(participantNameList);

        Gson gson = new Gson();
        String meetingJSONString = gson.toJson(mCurrentMeeting);
    }

    public void setMessageListener(){
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

                        Toast.makeText(ActiveMeetingActivity.this, "" + participant.getName(), Toast.LENGTH_LONG).show();

                        if (participantListFragment.isAdded() && (participant.getAttendance()!=null && participant.getAttendance().equals("present"))) {
                            Log.i(TAG, "participant addded");
                            participantListFragment.addParticipant(participant);
                            participantNameList.add(participant.getName());
                        }
                        else{
                            noteList.add(new MeetingNote(participant.getMeetingNotes(), participant.getName()));
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
        transaction.replace(R.id.mainFrame, participantListFragment ,"Participant");
        transaction.commit();

    }
    public void setUpRecyclerView(){
        Log.i(TAG, "setUpRecyclerView called");

        mRecyclerView.addItemDecoration(new DataItemDecoration(this, DataItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chatParticipantsList = new ArrayList<>();
        adapter = new ParticipantListItemAdapter(ActiveMeetingActivity.this ,chatParticipantsList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void publishNotes(){

        Log.i(TAG, "publishNotes called");

        InputMethodManager imm = (InputMethodManager) this
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null)
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

        if(edtMeetingNotes.getText()!=null && edtMeetingNotes.getText().toString().length()>0){

            Participant participant = new Participant();
            participant.setName(ProfileStore.getUserName(this));
            participant.setMeetingNotes(edtMeetingNotes.getText().toString());
            noteList.add(new MeetingNote(edtMeetingNotes.getText().toString(),ProfileStore.getUserName(this)));
            participant.setToWhom("to_Me");

           /* TODO Profile picture
           if(uploadBitmap != null){
                participant.setImageBytes(
                        GeneralUtils.getProfileImageBytes(this, uploadBitmap));
            }else{
                uploadBitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.default_user);
                participant.setImageBytes(
                        GeneralUtils.getProfileImageBytes(this, uploadBitmap));
            }
*/

            myNotes = participant.newNearbyMessage();

            adapter.updateList(participant);

            edtMeetingNotes.setText("");
            publish_MyNotes();
        }
    }


    private void startNearByAPI() {
        //Add code to start publishing

        Log.i(TAG, "startNearByAPI called");

        startSubscription();
        startPublishing();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void stopNearByAPI() {
        //Add code to start publishing

        Log.i(TAG, "stopNearByAPI called");

        if(mGoogleApiClient !=null && mGoogleApiClient.isConnected()){
            unsubscribe();
            if(myNotes!=null)
                unpublish_MyNotes();
            if(mProfileInformation != null)
                unpublish_MyProfile();
        }

    }



    private void startSubscription() {
        Log.i(TAG, "startSubscription called");
        String subscriptionTask = getPubSubTask(Constants.KEY_SUBSCRIPTION_TASK);
        if (TextUtils.equals(subscriptionTask, Constants.TASK_NONE) ||
                TextUtils.equals(subscriptionTask, Constants.TASK_UNSUBSCRIBE)) {
            updateSharedPreference(Constants.KEY_SUBSCRIPTION_TASK,
                    Constants.TASK_SUBSCRIBE);
        } else {
            updateSharedPreference(Constants.KEY_SUBSCRIPTION_TASK,
                    Constants.TASK_UNSUBSCRIBE);
        }

    }



    private void startPublishing() {
        Log.i(TAG, "startPublishing called");
        String publicationTask = getPubSubTask(Constants.KEY_PUBLICATION_TASK);
        if (TextUtils.equals(publicationTask, Constants.TASK_NONE) ||
                TextUtils.equals(publicationTask, Constants.TASK_UNPUBLISH)) {
            updateSharedPreference(Constants.KEY_PUBLICATION_TASK, Constants.TASK_PUBLISH);
        } else {
            updateSharedPreference(Constants.KEY_PUBLICATION_TASK,
                    Constants.TASK_UNPUBLISH);
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
        }else{
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Based on values stored in SharedPreferences, determines the subscription or .publication task
     * that should be performed
     */
    private String getPubSubTask(String taskKey) {
        return getPreferences(Context.MODE_PRIVATE)
                .getString(taskKey, Constants.TASK_NONE);
    }

    void executePendingTasks() {
        Log.i(TAG, "executePendingTasks called");
        executePendingSubscriptionTask();
        executePendingPublicationTask();
    }

    /**
     * Invokes a pending task based on the subscription state.
     */
    void executePendingSubscriptionTask() {
        Log.i(TAG, "executePendingSubscriptionTask called");
        String pendingSubscriptionTask = getPubSubTask(Constants.KEY_SUBSCRIPTION_TASK);
        if (TextUtils.equals(pendingSubscriptionTask, Constants.TASK_SUBSCRIBE)) {
            subscribe();
        } else if (TextUtils.equals(pendingSubscriptionTask, Constants.TASK_UNSUBSCRIBE)) {
            unsubscribe();
        }
    }

    /**
     * Invokes a pending task based on the publication state.
     */
    void executePendingPublicationTask() {
        Log.i(TAG, "executePendingPublicationTask called");
        String pendingPublicationTask = getPubSubTask(Constants.KEY_PUBLICATION_TASK);
        if (TextUtils.equals(pendingPublicationTask, Constants.TASK_PUBLISH)) {
            if(mProfileInformation!=null)
                Log.d(TAG, "executePendingPublicationTask: My Profile publishing");
                publish_MyProfile();
            if(myNotes!=null)
                publish_MyNotes();
        } else if (TextUtils.equals(pendingPublicationTask, Constants.TASK_UNPUBLISH)) {
            if(mProfileInformation!=null)
                unpublish_MyProfile();
            if(myNotes!=null)
                unpublish_MyNotes();
        }
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
            clearDeviceList();
            SubscribeOptions options = new SubscribeOptions.Builder()
                    .setStrategy(PUB_SUB_STRATEGY)
                    .setCallback(new SubscribeCallback() {
                        @Override
                        public void onExpired() {
                            super.onExpired();
                            Log.i(TAG, "no longer subscribing");
                            updateSharedPreference(Constants.KEY_SUBSCRIPTION_TASK,
                                    Constants.TASK_NONE);
                        }
                    }).build();

            Log.d("", "@@@@" + mMessageListener);

            Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, options)
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
                                updateSharedPreference(Constants.KEY_SUBSCRIPTION_TASK,
                                        Constants.TASK_NONE);
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
            PublishOptions options = new PublishOptions.Builder()
                    .setStrategy(PUB_SUB_STRATEGY)
                    .setCallback(new PublishCallback() {
                        @Override
                        public void onExpired() {
                            super.onExpired();
                            Log.i(TAG, "no longer publishing");
                            updateSharedPreference(Constants.KEY_PUBLICATION_TASK,
                                    Constants.TASK_NONE);
                        }
                    }).build();

            Nearby.Messages.publish(mGoogleApiClient, mProfileInformation, options)
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
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            PublishOptions options = new PublishOptions.Builder()
                    .setStrategy(PUB_SUB_STRATEGY)
                    .setCallback(new PublishCallback() {
                        @Override
                        public void onExpired() {
                            super.onExpired();
                            Log.i(TAG, "no longer publishing");
                            updateSharedPreference(Constants.KEY_PUBLICATION_TASK,
                                    Constants.TASK_NONE);
                        }
                    }).build();

            Nearby.Messages.publish(mGoogleApiClient, myNotes, options)
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
                                updateSharedPreference(Constants.KEY_PUBLICATION_TASK,
                                        Constants.TASK_NONE);
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
                                updateSharedPreference(Constants.KEY_PUBLICATION_TASK,
                                        Constants.TASK_NONE);
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
                GeneralUtils.displayCustomToast(ActiveMeetingActivity.this , getString(R.string.no_internet_connectivity));
                resetToDefaultState();
            } else {
                // To keep things simple, pop a toast for all other error messages.
                GeneralUtils.displayCustomToast(ActiveMeetingActivity.this , getString(R.string.unsuccessful)+ " "+status.getStatusMessage());
            }

        }
    }


    /**
     * Clears items from the adapter.
     */
    private void clearDeviceList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Add code to remove participants list here after meeting is over
            }
        });
    }

    /**
     * Resets the state of pending subscription and publication tasks.
     */
    void resetToDefaultState() {
        getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.KEY_SUBSCRIPTION_TASK, Constants.TASK_NONE)
                .putString(Constants.KEY_PUBLICATION_TASK, Constants.TASK_NONE)
                .apply();
    }

    /**
     * Helper for editing entries in SharedPreferences.
     */
    private void updateSharedPreference(String key, String value) {

        getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putString(key, value)
                .apply();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, final String key) {

        Log.i(TAG, "onSharedPreferenceChanged called");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.equals(key, Constants.KEY_SUBSCRIPTION_TASK)) {
                    executePendingSubscriptionTask();
                } else if (TextUtils.equals(key, Constants.KEY_PUBLICATION_TASK)) {
                    executePendingPublicationTask();
                }
            }
        });

    }

    @Override
    protected void onStop() {

        super.onStop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && ! isChangingConfigurations()) {
            // Using Nearby is battery intensive. To preserve battery, stop subscribing or
            // publishing when the fragment is inactive.
            unsubscribe();
            if(myNotes!=null)
                unpublish_MyNotes();
            if(mProfileInformation!=null)
                unpublish_MyProfile();

            updateSharedPreference(Constants.KEY_SUBSCRIPTION_TASK, Constants.TASK_NONE);
            updateSharedPreference(Constants.KEY_PUBLICATION_TASK, Constants.TASK_NONE);

            mGoogleApiClient.disconnect();
            getPreferences(Context.MODE_PRIVATE)
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
    }
    private Bitmap uploadBitmap;

    private class UpdatePicTask extends AsyncTask<String, Void, Void> {
        private ProgressDialog progDialog;
        private boolean err = false;
        private String errMessage = "";
        private String imgPath;


        @Override
        protected Void doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                imgPath = params[0];
                errMessage = params[1];

                if (imgPath != null) {
                    uploadBitmap = grabImage(imgPath);
                    uploadBitmap = uploadBitmap.copy(Bitmap.Config.ARGB_8888, true);
                }


                if (uploadBitmap == null) {
                    err = true;
                }
            } catch (Exception e) {
                err = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {

            if (err) {

                Toast.makeText(ActiveMeetingActivity.this , errMessage , Toast.LENGTH_SHORT).show();
                ProfileStore.saveImagePath(ActiveMeetingActivity.this, null);

            } else {

                ProfileStore.saveImagePath(ActiveMeetingActivity.this , imgPath);

            }
        }
    }

    public Bitmap grabImage(String path) throws FileNotFoundException,
            IOException {
        Bitmap bitmap = null;
        bitmap = new GeneralUtils().decodeSampledBitmapFromPath(path, 800, 600);
        return bitmap;
    }


    public void openParticipantsSlidingLayout(){
        rl_ParticipantsAttendanceSlidingView.setVisibility(rl_ParticipantsAttendanceSlidingView.VISIBLE);
        rl_ParticipantsAttendanceSlidingView.bringToFront();
        animationIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.right_to_left_in);

        rl_ParticipantsAttendanceSlidingView.startAnimation(animationIn);
    }

    public void closeParticipantsSlidingLayout(){
        rl_ParticipantsAttendanceSlidingView.setVisibility(rl_ParticipantsAttendanceSlidingView.GONE);
        animationIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.left_to_right_out);

        rl_ParticipantsAttendanceSlidingView.startAnimation(animationIn);

    }

}
