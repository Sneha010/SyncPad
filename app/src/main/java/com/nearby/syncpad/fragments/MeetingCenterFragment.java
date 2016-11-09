package com.nearby.syncpad.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nearby.syncpad.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeetingCenterFragment extends BaseFragment {

    public static final String SCREEN_NAME = "MEETING CENTER";

    public static MeetingCenterFragment newInstance() {
        MeetingCenterFragment fragment = new MeetingCenterFragment();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meeting_center, container, false);
    }


    public void onlandingButtonClick(View v) {

        switch (v.getId()) {
            case R.id.btnStartMeeting:

                break;

            case R.id.btnJoinMeeting:

                break;
        }
    }

    @NonNull
    @Override
    public String getTitle() {
        return SCREEN_NAME;
    }
}
