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
import com.nearby.syncpad.MainActivity;
import com.nearby.syncpad.R;
import com.nearby.syncpad.models.Meeting;
import com.nearby.syncpad.util.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sneha Khadatare : 587823
 * on 11/18/2016.
 */

public class AddMeetingDialogFragment extends AppCompatDialogFragment{

    EditText edtName ,edtDate ,edtTime ,edtVenue ,edtAgenda;
    TextView tvCancel , tvAdd , tv_selectDateButton , tv_selectTimeButton;

    static public AddMeetingDialogFragment newInstance() {
        AddMeetingDialogFragment f = new AddMeetingDialogFragment();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.add_meeting_dialog_layout, container,
                false);

        initialise(rootView);


        return rootView;
    }

    private void initialise(View view) {


        edtName = (EditText)view.findViewById(R.id.edtMeetingName);
        edtDate = (EditText)view.findViewById(R.id.edtMeetingDate);
        edtTime = (EditText)view.findViewById(R.id.edtMeetingTime);
        edtVenue = (EditText)view.findViewById(R.id.edtMeetingVenue);
        edtAgenda = (EditText)view.findViewById(R.id.edtMeetingAgenda);
        tvCancel = (TextView)view.findViewById(R.id.tvCancel);
        tvAdd = (TextView)view.findViewById(R.id.tvAdd);
        tv_selectDateButton = (TextView)view.findViewById(R.id.select_date_button);
        tv_selectTimeButton = (TextView) view.findViewById(R.id.select_time_button) ;

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

                    Intent i = new Intent(getActivity() , ActiveMeetingActivity.class);
                    i.putExtra(Constants.MEETING, meetingBean);
                    i.putExtra(Constants.IS_HOST , true);
                    startActivity(i);
                    dismiss();
                }

            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        tv_selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Use the current date as the default date in the picker
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        edtDate.setText(dayOfMonth+"/"+month+"/"+year);
                    }
                },year, month , day);
                datePickerDialog.show();

            }
        });

        tv_selectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        SimpleDateFormat format = new SimpleDateFormat("HH:MM");
                        try {
                            Date date = format.parse(""+hourOfDay+":"+minute);
                            edtTime.setText(new SimpleDateFormat("hh:mm aa").format(date.getTime()));
                        }catch(ParseException ex){
                            ex.printStackTrace();
                        }
                    }
                },hour, minute, false);
                timePickerDialog.show();
            }
        });

    }

}
