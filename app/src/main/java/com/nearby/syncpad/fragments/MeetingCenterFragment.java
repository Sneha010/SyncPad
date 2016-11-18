package com.nearby.syncpad.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nearby.syncpad.ActiveMeetingActivity;
import com.nearby.syncpad.R;
import com.nearby.syncpad.models.Meeting;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeetingCenterFragment extends BaseFragment implements View.OnClickListener{

    public static final String SCREEN_NAME = "MEETING CENTER";

    private View _view;

    public static MeetingCenterFragment newInstance() {
        MeetingCenterFragment fragment = new MeetingCenterFragment();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        _view = inflater.inflate(R.layout.fragment_meeting_center, container, false);

        _view.findViewById(R.id.btnStartMeeting).setOnClickListener(this);
        _view.findViewById(R.id.btnJoinMeeting).setOnClickListener(this);

        return _view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartMeeting:
                startMeetingDialog();
                break;

            case R.id.btnJoinMeeting:
                scanNearbyMeetings();
                break;
        }
    }

    private Dialog dialog;
    EditText edtName ,edtDate ,edtTime ,edtVenue ,edtAgenda;
    TextView tvCancel , tvAdd , tv_selectdateButton;

    private void startMeetingDialog() {

        dialog = new Dialog(getActivity());

        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;

        View view = View.inflate(getActivity(), R.layout.add_meeting_dialog_layout, null);

        edtName = (EditText)view.findViewById(R.id.edtMeetingName);
        edtDate = (EditText)view.findViewById(R.id.edtMeetingDate);
        edtTime = (EditText)view.findViewById(R.id.edtMeetingTime);
        edtVenue = (EditText)view.findViewById(R.id.edtMeetingVenue);
        edtAgenda = (EditText)view.findViewById(R.id.edtMeetingAgenda);
        tvCancel = (TextView)view.findViewById(R.id.tvCancel);
        tvAdd = (TextView)view.findViewById(R.id.tvAdd);
        tv_selectdateButton = (TextView)view.findViewById(R.id.select_date_button);


        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtName.getText()==null || (edtName.getText()!=null && edtName.getText().toString().length()== 0)){
                    Toast.makeText(getActivity(), "Please enter meeting title" , Toast.LENGTH_SHORT).show();
                }
                else{
                    Meeting meetingBean = new Meeting();
                    if(edtName.getText()!=null && edtName.getText().toString().length()>0){
                        meetingBean.setMeetingName(edtName.getText().toString());
                    }
                    if(edtDate.getText()!=null && edtDate.getText().toString().length()>0){
                        meetingBean.setMeetingDate(edtDate.getText().toString());
                    }
                    if(edtTime.getText()!=null && edtTime.getText().toString().length()>0){
                        meetingBean.setMeetingTime(edtTime.getText().toString());
                    }
                    if(edtVenue.getText()!=null && edtVenue.getText().toString().length()>0){
                        meetingBean.setMeetingVenue(edtVenue.getText().toString());
                    }
                    if(edtAgenda.getText()!=null && edtAgenda.getText().toString().length()>0){
                        meetingBean.setMeetingAgenda(edtAgenda.getText().toString());
                    }

                    Intent i = new Intent(getContext() , ActiveMeetingActivity.class);
                    i.putExtra("meeting_name" ,edtName.getText().toString());
                    getActivity().startActivity(i);
                    dialog.dismiss();
                }

            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tv_selectdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        dialog.getWindow().

                requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.dialog_bg));

        dialog.setContentView(view);
        dialog.show();

        dialog.getWindow().

                setLayout((6 * width)

                                / 7,
                        WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
    }

    private void scanNearbyMeetings(){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        ScanMeetingsDialogFragment dFragment = ScanMeetingsDialogFragment.newInstance();


    }

    @NonNull
    @Override
    public String getTitle() {
        return SCREEN_NAME;
    }


}
