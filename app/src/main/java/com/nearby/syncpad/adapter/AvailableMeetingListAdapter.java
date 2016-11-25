package com.nearby.syncpad.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nearby.syncpad.ActiveMeetingActivity;
import com.nearby.syncpad.R;
import com.nearby.syncpad.callbacks.DismissScanDialogListener;
import com.nearby.syncpad.models.Participant;
import com.nearby.syncpad.util.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AvailableMeetingListAdapter extends RecyclerView.Adapter<AvailableMeetingListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Participant> meetingList;

    public AvailableMeetingListAdapter(Context context, ArrayList<Participant> meetings) {
        this.context = context;
        this.meetingList = meetings;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.available_meeting_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if (meetingList.get(position).isIsHost()) {

            holder.tvMeetingTitle.setText(meetingList.get(position).getMeeting().getMeetingName());

            if (!meetingList.get(position).getName().isEmpty()) {
                holder.tvInitiatedBy.setVisibility(View.VISIBLE);
                holder.tvInitiatedBy.setText("Initiated by " + meetingList.get(position).getName());
            } else {
                holder.tvInitiatedBy.setVisibility(View.GONE);
            }

            //holder.tvMeetingDateAndTime.setText(meetingList.get(position).get);
            holder.rlDataContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, ActiveMeetingActivity.class);
                    i.putExtra(Constants.MEETING, meetingList.get(position).getMeeting());
                    i.putExtra(Constants.IS_HOST, false);
                    context.startActivity(i);
                    if (context instanceof DismissScanDialogListener) {
                        ((DismissScanDialogListener) context).dismissDialog();
                    }
                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return meetingList.size();
    }

    public void updateList(Participant meeting) {

        meetingList.add(meeting);
        notifyDataSetChanged();

    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.tvMeetingTitle)
        TextView tvMeetingTitle;

        @BindView(R.id.tvInitiatedBy)
        TextView tvInitiatedBy;

        @BindView(R.id.rlDataContent)
        RelativeLayout rlDataContent;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
