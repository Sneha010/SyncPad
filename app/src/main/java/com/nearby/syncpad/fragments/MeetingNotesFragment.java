package com.nearby.syncpad.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nearby.syncpad.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by 587823 on 11/25/2016.
 */

public class MeetingNotesFragment extends Fragment {

    private Unbinder binder;

    public static MeetingNotesFragment newInstance() {
        MeetingNotesFragment fragment = new MeetingNotesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meeting_notes_frag_layout, container, false);

        binder = ButterKnife.bind(this , view);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        binder.unbind();
    }
}
