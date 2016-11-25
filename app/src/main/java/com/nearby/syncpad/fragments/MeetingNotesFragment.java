package com.nearby.syncpad.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nearby.syncpad.R;
import com.nearby.syncpad.util.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by 587823 on 11/25/2016.
 */

public class MeetingNotesFragment extends Fragment {

    @BindView(R.id.llContainer)
    LinearLayout llContainer;

    @BindView(R.id.tvError)
    TextView tvError;

    private String mNotes;

    private Unbinder binder;

    public static MeetingNotesFragment newInstance(String notes) {
        MeetingNotesFragment fragment = new MeetingNotesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MEETING_NOTES, notes);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meeting_notes_frag_layout, container, false);

        binder = ButterKnife.bind(this, view);

        mNotes = getArguments().getString(Constants.MEETING_NOTES);

        if (mNotes != null) {
            tvError.setVisibility(View.GONE);
            llContainer.setVisibility(View.VISIBLE);
            showMeetingNotes();
        } else {
            tvError.setVisibility(View.VISIBLE);
            llContainer.setVisibility(View.GONE);
        }


        return view;
    }

    private void showMeetingNotes() {

        String[] noteList = mNotes.split("||");

        if (noteList != null && noteList.length > 0) {
            for (int i = 0; i < noteList.length; i++) {
                TextView textView = new TextView(getActivity());
                textView.setText(noteList[i]);

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);

                textView.setLayoutParams(params);
                llContainer.addView(textView, params);
            }
        } else {
            tvError.setVisibility(View.VISIBLE);
            llContainer.setVisibility(View.GONE);
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        binder.unbind();
    }
}
