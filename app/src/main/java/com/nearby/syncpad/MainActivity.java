package com.nearby.syncpad;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.nearby.syncpad.models.Meeting;


public class MainActivity extends AppCompatActivity {

    private LinearLayout llNoMeetingsAdded;
    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton fab_StartMeeting;
    private FloatingActionButton fab_JoinMeeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        llNoMeetingsAdded = (LinearLayout) findViewById(R.id.ll_no_meetings);
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.floatingActionMenu);
        fab_StartMeeting = (FloatingActionButton) findViewById(R.id.menu_item1) ;
        fab_JoinMeeting = (FloatingActionButton) findViewById(R.id.menu_item2) ;

        llNoMeetingsAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMeetingDialog();
            }
        });

        fab_StartMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMeetingDialog();
                floatingActionMenu.close(true);
            }
        });

        fab_JoinMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanNearbyMeetings();
                floatingActionMenu.close(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.participants, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.participants) {

            startActivity(new Intent(MainActivity.this , MyProfileActivity.class));

            return true;
        }else{
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private Dialog dialog;
    EditText edtName ,edtDate ,edtTime ,edtVenue ,edtAgenda;
    TextView tvCancel , tvAdd , tv_selectdateButton;
    private void startMeetingDialog() {

        dialog = new Dialog(this);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;

        View view = View.inflate(this, R.layout.add_meeting_dialog_layout, null);

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
                    Toast.makeText(MainActivity.this, "Please enter meeting title" , Toast.LENGTH_SHORT).show();
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

                    Intent i = new Intent(MainActivity.this , ActiveMeetingActivity.class);
                    i.putExtra("meeting_name" ,edtName.getText().toString());
                    startActivity(i);
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

//        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
//        if (prev != null) {
//            ft.remove(prev);
//        }
//        ft.addToBackStack(null);
//
//        ScanMeetingsDialogFragment dFragment = ScanMeetingsDialogFragment.newInstance("Select the meeting");


    }

}
