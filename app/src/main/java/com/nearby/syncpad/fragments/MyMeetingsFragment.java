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
public class MyMeetingsFragment extends BaseFragment {

    public static final String SCREEN_NAME = "MY MEETINGS";

    public static MyMeetingsFragment newInstance() {
        MyMeetingsFragment fragment = new MyMeetingsFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_meetings, container, false);
    }
    @NonNull
    @Override
    public String getTitle() {
        return SCREEN_NAME;
    }

}
