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

        setDateAndTime();
    }

    @OnClick(R.id.tvAdd)
    public void addMeetingBtnClicked(View view) {


        if (edtMeetingName.getText() == null || (edtMeetingName.getText() != null && edtMeetingName.getText().toString().length() == 0)) {
            Toast.makeText(getActivity(), R.string.enter_meeting_title, Toast.LENGTH_SHORT).show();
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


    private void setDateAndTime() {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);

        edtMeetingDate.setText(day + "/" + (month + 1) + "/" + year);

        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        edtMeetingDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();

            }
        });

        final int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minute = c.get(Calendar.MINUTE);

        edtMeetingTime.setText(getTimeString(hour, minute));

        selectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getTimeString(hour, minute);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        edtMeetingTime.setText(getTimeString(hourOfDay, minute));
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });
    }


    private String getTimeString(int hourOfDay, int min) {

        String timeString = null;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            Date date = format.parse("" + hourOfDay + ":" + min);
            timeString = new SimpleDateFormat("hh:mm aa").format(date);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return timeString;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        binder.unbind();
    }
}
