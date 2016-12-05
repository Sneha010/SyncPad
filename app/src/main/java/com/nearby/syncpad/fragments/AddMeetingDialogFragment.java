package com.nearby.syncpad.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.nearby.syncpad.ActiveMeetingActivity;
import com.nearby.syncpad.R;
import com.nearby.syncpad.models.Meeting;
import com.nearby.syncpad.util.Constants;
import com.nearby.syncpad.util.DateTimeUtils;
import com.nearby.syncpad.util.GeneralUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Sneha Khadatare : 587823
 * on 11/18/2016.
 */

public class AddMeetingDialogFragment extends AppCompatDialogFragment {

    @BindView(R.id.edtMeetingName)
    EditText edtMeetingName;

    @BindView(R.id.edtMeetingDate)
    EditText edtMeetingDate;

    @BindView(R.id.select_date_button)
    TextView selectDateButton;

    @BindView(R.id.edtMeetingTime)
    EditText edtMeetingTime;

    @BindView(R.id.select_time_button)
    TextView selectTimeButton;

    @BindView(R.id.edtMeetingVenue)
    EditText edtMeetingVenue;

    @BindView(R.id.edtMeetingAgenda)
    EditText edtMeetingAgenda;

    Unbinder binder;

    static public AddMeetingDialogFragment newInstance() {
        AddMeetingDialogFragment f = new AddMeetingDialogFragment();
        f.setStyle(STYLE_NO_TITLE, 0);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.add_meeting_dialog_layout, container,
                false);

        binder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setDefaultDateAndTime();
    }

    private void setDefaultDateAndTime() {

        edtMeetingDate.setText(DateTimeUtils.getCurrentDate());
        edtMeetingTime.setText(DateTimeUtils.getCurrentTime());


    }

    @OnClick(R.id.tvAdd)
    public void addMeetingBtnClicked(View view) {

        //validation for form data entered
        if (edtMeetingName.getText() == null || (edtMeetingName.getText() != null && edtMeetingName.getText().toString().length() == 0)) {
            GeneralUtils.displayCustomToast(getActivity(), getString(R.string.enter_meeting_title));
        } else {
            Meeting meetingBean = new Meeting();
            if (edtMeetingName.getText() != null && edtMeetingName.getText().toString().length() > 0) {
                meetingBean.setMeetingName(edtMeetingName.getText().toString());
            }
            if (edtMeetingDate.getText() != null && edtMeetingDate.getText().toString().length() > 0) {
                meetingBean.setMeetingDate(edtMeetingDate.getText().toString());
            }
            if (edtMeetingTime.getText() != null && edtMeetingTime.getText().toString().length() > 0) {
                meetingBean.setMeetingTime(edtMeetingTime.getText().toString());
            }
            if (edtMeetingVenue.getText() != null && edtMeetingVenue.getText().toString().length() > 0) {
                meetingBean.setMeetingVenue(edtMeetingVenue.getText().toString());
            }
            if (edtMeetingAgenda.getText() != null && edtMeetingAgenda.getText().toString().length() > 0) {
                meetingBean.setMeetingAgenda(edtMeetingAgenda.getText().toString());
            }

            Intent i = new Intent(getActivity(), ActiveMeetingActivity.class);
            i.putExtra(Constants.MEETING, meetingBean);
            i.putExtra(Constants.IS_HOST, true);
            startActivity(i);
            dismiss();
        }


    }

    @OnClick(R.id.tvCancel)
    public void cancelButtonPressed(View view) {
        dismiss();
    }


    @OnClick(R.id.select_date_button)
    public void selectDate() {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                edtMeetingDate.setText(DateTimeUtils.getDateUsingData(dayOfMonth, month, year));

            }
        }, year, month, day);

        datePickerDialog.show();

    }

    @OnClick(R.id.select_time_button)
    public void selectTime() {

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                edtMeetingTime.setText(DateTimeUtils.getTimeUsingData(hourOfDay, minute));
            }
        }, hour, minute, false);
        timePickerDialog.show();


    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        binder.unbind();
    }
}
