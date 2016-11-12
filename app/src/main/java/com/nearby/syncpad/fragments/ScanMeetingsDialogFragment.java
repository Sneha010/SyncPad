package com.nearby.syncpad.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nearby.syncpad.R;

/**
 * Created by Sneha Khadatare : 587823
 * on 11/11/2016.
 */

public class ScanMeetingsDialogFragment extends Fragment {

    public static final String DIALOG_TITLE = "dialog_title";

    static ScanMeetingsDialogFragment newInstance(String title) {
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
        //setStyle(STYLE_NORMAL , android.R.style.Theme_Material_Dialog);
        return rootView;
    }
}
