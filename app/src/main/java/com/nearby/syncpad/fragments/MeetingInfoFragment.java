package com.nearby.syncpad.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nearby.syncpad.R;
import com.nearby.syncpad.models.Meeting;
import com.nearby.syncpad.util.Constants;
import com.nearby.syncpad.util.DateTimeUtils;
import com.nearby.syncpad.util.GeneralUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by 587823 on 11/25/2016.
 */

public class MeetingInfoFragment extends Fragment {

    @BindView(R.id.tvDatevalue)
    TextView tvDateValue;

    @BindView(R.id.tvTimeValue)
    TextView tvTimeValue;

    @BindView(R.id.tvVenueValue)
    TextView tvVenueValue;

    @BindView(R.id.tvAgendaValue)
    TextView tvAgendaValue;

    @BindView(R.id.tvAttendeesValue)
    TextView tvAttendeesValue;

    private Unbinder binder;
    private Meeting mMeeting;

    public static MeetingInfoFragment newInstance(Meeting meeting) {
        MeetingInfoFragment fragment = new MeetingInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.MEETING, meeting);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mMeeting = getArguments().getParcelable(Constants.MEETING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meeting_info_frag_layout, container, false);

        binder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showMeetingInfo();
    }

    private void showMeetingInfo() {

        if (mMeeting != null) {
            if (!GeneralUtils.isEmpty(mMeeting.getMeetingDate()))
                tvDateValue.setText(DateTimeUtils.getFormattedDate(mMeeting.getMeetingDate()));
            else
                tvDateValue.setText(getString(R.string.na));

            if (!GeneralUtils.isEmpty(mMeeting.getMeetingTime()))
                tvTimeValue.setText(mMeeting.getMeetingTime());
            else
                tvTimeValue.setText(getString(R.string.na));

            if (!GeneralUtils.isEmpty(mMeeting.getMeetingVenue()))
                tvVenueValue.setText(mMeeting.getMeetingVenue());
            else
                tvVenueValue.setText(getString(R.string.na));

            if (!GeneralUtils.isEmpty(mMeeting.getMeetingAgenda()))
                tvAgendaValue.setText(mMeeting.getMeetingAgenda());
            else
                tvAgendaValue.setText(getString(R.string.na));

            if (!GeneralUtils.isEmpty(mMeeting.getMeetingParticipants()))
                tvAttendeesValue.setText(mMeeting.getMeetingParticipants().replace("||", "\n"));
            else
                tvAttendeesValue.setText(getString(R.string.na));

        } else {
            tvDateValue.setText(getString(R.string.na));
            tvTimeValue.setText(getString(R.string.na));
            tvVenueValue.setText(getString(R.string.na));
            tvAgendaValue.setText(getString(R.string.na));
            tvAttendeesValue.setText(getString(R.string.na));
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        binder.unbind();
    }
}
