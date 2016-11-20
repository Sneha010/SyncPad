package com.nearby.syncpad;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.nearby.syncpad.models.Meeting;
import com.nearby.syncpad.util.Constants;
import com.nearby.syncpad.util.GeneralUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.nearby.syncpad.R.id.tvNotes;

/**
 * Created by Sneha Khadatare on 11/20/2016.
 */

public class MeetingDetailsActivity extends AppCompatActivity {


    @BindView(tvNotes)
    TextView mTvNotes;

    @BindView(R.id.tvParticipant)
    TextView mTvParticipant;

    private Unbinder mUnbinder;
    private Meeting mMeeting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.meetings_details_layout);
        mUnbinder = ButterKnife.bind(this);

        if(getIntent()!=null){
            mMeeting = getIntent().getExtras().getParcelable(Constants.MEETING);
        }

        initialise();
        displayMeetingDetails();
    }


    public void initialise(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(mMeeting.getMeetingName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }
    private void displayMeetingDetails() {

        //TODO display meeting using bean

        if(!GeneralUtils.isEmpty(mMeeting.getNotesList()))
            mTvNotes.setText(mMeeting.getNotesList());
        if(!GeneralUtils.isEmpty(mMeeting.getParticipantNameList()))
            mTvParticipant.setText(mMeeting.getParticipantNameList());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mUnbinder.unbind();
    }
}
