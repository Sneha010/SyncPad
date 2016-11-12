package com.nearby.syncpad.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nearby.syncpad.R;

/**
 * Created by Sneha Khadatare : 587823
 * on 11/11/2016.
 */

public class ScanMeetingsDialogFragment extends AppCompatDialogFragment {

    public static final String DIALOG_TITLE = "dialog_title";

    private RelativeLayout mRlProgress;
    private TextView mNoActiveMeetingText;

    static public ScanMeetingsDialogFragment newInstance(String title) {
        ScanMeetingsDialogFragment f = new ScanMeetingsDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, title);
        f.setArguments(args);
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
        performTask();
        return rootView;
    }

    private void initialise(View _view) {

        mRlProgress = (RelativeLayout) _view.findViewById(R.id.rl_progress);
        mNoActiveMeetingText = (TextView) _view.findViewById(R.id.tvNoActiveMeeting);
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
}
