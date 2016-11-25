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
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.nearby.syncpad.R;
import com.nearby.syncpad.SyncPadApplication;
import com.nearby.syncpad.adapter.AvailableMeetingListAdapter;
import com.nearby.syncpad.models.Participant;
import com.nearby.syncpad.util.DataItemDecoration;
import com.nearby.syncpad.util.GeneralUtils;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.ContentValues.TAG;

/**
 * Created by Sneha Khadatare : 587823
 * on 11/11/2016.
 */

public class ScanMeetingsDialogFragment extends AppCompatDialogFragment
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.availableMeetingRVList)
    RecyclerView availableMeetingRVList;

    @BindView(R.id.tvNoActiveMeeting)
    TextView tvNoActiveMeeting;

    @BindView(R.id.rl_progress)
    RelativeLayout rlProgress;

    private ArrayList<Participant> meetingList;
    private AvailableMeetingListAdapter mAdapter;

    private GoogleApiClient mGoogleApiClient;
    private MessageListener mMessageListener;

    private Unbinder binder;

    @Inject
    SubscribeOptions mSubscribeOptions;


    static public ScanMeetingsDialogFragment newInstance() {
        ScanMeetingsDialogFragment f = new ScanMeetingsDialogFragment();
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((SyncPadApplication) getActivity().getApplication()).getMyApplicationComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.scan_meeting_dialog_layout, container,
                false);

        binder = ButterKnife.bind(this, rootView);

        setAvailableMeetingList();
        buildGoogleApiClient();
        setMessageListener();

        showProgress();


        return rootView;
    }

    public void setAvailableMeetingList() {
        availableMeetingRVList.addItemDecoration(new DataItemDecoration(getActivity(), DataItemDecoration.VERTICAL_LIST));
        availableMeetingRVList.setHasFixedSize(true);
        availableMeetingRVList.setItemAnimator(new DefaultItemAnimator());

        meetingList = new ArrayList<>();
        mAdapter = new AvailableMeetingListAdapter(getActivity(), meetingList);

        availableMeetingRVList.setAdapter(mAdapter);

        availableMeetingRVList.setLayoutManager(new LinearLayoutManager(getActivity()));
        availableMeetingRVList.setVisibility(View.GONE);

    }

    private void performTask() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rlProgress.setVisibility(View.GONE);
                tvNoActiveMeeting.setVisibility(View.VISIBLE);
            }
        }, 1000);
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
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended: "
                + connectionSuspendedCauseToString(cause));
        GeneralUtils.displayCustomToast(getActivity(),
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

    public void setMessageListener() {
        mMessageListener = new MessageListener() {
            @Override
            public void onFound(final Message message) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.i(TAG, "setMessageListener called");

                        //get message from this, use it later to get profile data and other stuff
                        Participant participant = Participant.fromNearbyMessage(message);
                        GeneralUtils.displayCustomToast(getActivity(), participant.getMeeting().getMeetingName());

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

            Log.d("", "@@@@" + mMessageListener);

            Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, mSubscribeOptions)
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

    private void showError(String errorString) {
        tvNoActiveMeeting.setVisibility(View.VISIBLE);
        tvNoActiveMeeting.setText(errorString);
        rlProgress.setVisibility(View.GONE);
        availableMeetingRVList.setVisibility(View.GONE);
    }

    private void showList() {
        tvNoActiveMeeting.setVisibility(View.GONE);
        rlProgress.setVisibility(View.GONE);
        availableMeetingRVList.setVisibility(View.VISIBLE);
    }

    private void showProgress() {
        tvNoActiveMeeting.setVisibility(View.GONE);
        rlProgress.setVisibility(View.VISIBLE);
        availableMeetingRVList.setVisibility(View.GONE);
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

        binder.unbind();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            unsubscribe();
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }
}
