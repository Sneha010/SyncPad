package com.nearby.syncpad.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.nearby.syncpad.R;
import com.nearby.syncpad.adapter.AvailableMeetingListAdapter;
import com.nearby.syncpad.models.Participant;
import com.nearby.syncpad.util.Constants;
import com.nearby.syncpad.util.DataItemDecoration;
import com.nearby.syncpad.util.GeneralUtils;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Sneha Khadatare : 587823
 * on 11/11/2016.
 */

public class ScanMeetingsDialogFragment extends AppCompatDialogFragment
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public static final String DIALOG_TITLE = "dialog_title";

    private RelativeLayout mRlProgress;
    private TextView mNoActiveMeetingText;
    private RecyclerView mAvailableMeetingsList;
    private ArrayList<Participant> meetingList;
    private AvailableMeetingListAdapter mAdapter;

    private GoogleApiClient mGoogleApiClient;
    private MessageListener mMessageListener;

    /**
     * Sets the time in seconds for a published message or a subscription to live. Set to five
     * minutes.
     */
    private static final Strategy PUB_SUB_STRATEGY = new Strategy.Builder()
            .setTtlSeconds(Constants.TTL_IN_SECONDS)
            .setDistanceType(Strategy.DISTANCE_TYPE_EARSHOT).build();


    static public ScanMeetingsDialogFragment newInstance() {
        ScanMeetingsDialogFragment f = new ScanMeetingsDialogFragment();
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.scan_meeting_dialog_layout, container,
                false);

        initialise(rootView);
        setAvailableMeetingList();
        buildGoogleApiClient();
        setMessageListener();

        showProgress();

        return rootView;
    }

    private void initialise(View _view) {

        mRlProgress = (RelativeLayout) _view.findViewById(R.id.rl_progress);
        mNoActiveMeetingText = (TextView) _view.findViewById(R.id.tvNoActiveMeeting);
        mAvailableMeetingsList = (RecyclerView) _view.findViewById(R.id.availableMeetingRVList);
    }
    public void setAvailableMeetingList(){
        mAvailableMeetingsList.addItemDecoration(new DataItemDecoration(getActivity(), DataItemDecoration.VERTICAL_LIST));
        mAvailableMeetingsList.setHasFixedSize(true);
        mAvailableMeetingsList.setItemAnimator(new DefaultItemAnimator());

        meetingList = new ArrayList<>();
        mAdapter = new AvailableMeetingListAdapter(getActivity() ,meetingList);

        mAvailableMeetingsList.setAdapter(mAdapter);

        mAvailableMeetingsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAvailableMeetingsList.setVisibility(View.GONE);

    }
    private void performTask() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRlProgress.setVisibility(View.GONE);
                mNoActiveMeetingText.setVisibility(View.VISIBLE);
            }
        },1000);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected called");
        subscribe();
    }


    private void buildGoogleApiClient() {

        Log.d(TAG, "buildGoogleApiClient: Trying to connect to nearby");

        if (mGoogleApiClient != null) {
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .enableAutoManage(getActivity(), this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended: "
                + connectionSuspendedCauseToString(cause));
        GeneralUtils.displayCustomToast(getActivity() ,
                getString(R.string.nearby_connection_error));

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

        GeneralUtils.displayCustomToast(getActivity(),
                getString(R.string.nearby_connection_error));

    }

    public void setMessageListener(){
        mMessageListener = new MessageListener() {
            @Override
            public void onFound(final Message message) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.i(TAG, "setMessageListener called");

                        //get message from this, use it later to get profile data and other stuff
                        Participant participant = Participant.fromNearbyMessage(message);
                        GeneralUtils.displayCustomToast(getActivity() , participant.getMeeting().getMeetingName());

                        mAdapter.updateList(participant);

                        showList();
                    }
                });
            }

            @Override
            public void onLost(final Message message) {
                // Called when a message is no longer detectable nearby.
                getActivity().runOnUiThread(new Runnable() {
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
            SubscribeOptions options = new SubscribeOptions.Builder()
                    .setStrategy(PUB_SUB_STRATEGY)
                    .setCallback(new SubscribeCallback() {
                        @Override
                        public void onExpired() {
                            super.onExpired();
                            Log.i(TAG, "no longer subscribing");
                            showError(getString(R.string.unable_to_subscribe));
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
                               showError(getString(R.string.unable_to_subscribe));
                            }
                        }
                    });
        }
    }

    private void showError(String errorString){
        mNoActiveMeetingText.setVisibility(View.VISIBLE);
        mNoActiveMeetingText.setText(errorString);
        mRlProgress.setVisibility(View.GONE);
        mAvailableMeetingsList.setVisibility(View.GONE);
    }

    private void showList(){
        mNoActiveMeetingText.setVisibility(View.GONE);
        mRlProgress.setVisibility(View.GONE);
        mAvailableMeetingsList.setVisibility(View.VISIBLE);
    }

    private void showProgress(){
        mNoActiveMeetingText.setVisibility(View.GONE);
        mRlProgress.setVisibility(View.VISIBLE);
        mAvailableMeetingsList.setVisibility(View.GONE);
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

                            }
                        }
                    });
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            unsubscribe();
            mGoogleApiClient.disconnect();
        }
    }
}
