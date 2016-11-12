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



public class AvailableMeetingListAdapter extends RecyclerView.Adapter<AvailableMeetingListAdapter.ViewHolder>{

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
    public void onBindViewHolder(final ViewHolder holder,final int position) {

        if(meetingList.get(position).ismIsHost()) {

            holder.tvMeetingTitle.setText(meetingList.get(position).getmMeetingTitle());

            if(!meetingList.get(position).getName().isEmpty()){
                holder.tvMeetingInitiatedBy.setVisibility(View.VISIBLE);
                holder.tvMeetingInitiatedBy.setText(meetingList.get(position).getName());
            }else{
                holder.tvMeetingInitiatedBy.setVisibility(View.GONE);
            }

            //holder.tvMeetingDateAndTime.setText(meetingList.get(position).get);
            holder.rlDataContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, ActiveMeetingActivity.class);
                    i.putExtra(Constants.MEETING_NAME, meetingList.get(position).getmMeetingTitle());
                    i.putExtra(Constants.IS_HOST , false);
                    context.startActivity(i);
                    if(context instanceof DismissScanDialogListener){
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

        RelativeLayout rlDataContent;
        TextView tvMeetingTitle;
        TextView tvMeetingInitiatedBy;
        TextView tvMeetingDateAndTime;

        public ViewHolder(View itemView) {
            super(itemView);
            rlDataContent = (RelativeLayout) itemView.findViewById(R.id.rlDataContent);
            tvMeetingTitle = (TextView) itemView.findViewById(R.id.tvMeetingTitle);
            tvMeetingInitiatedBy = (TextView) itemView.findViewById(R.id.tvInitiatedBy);
            tvMeetingDateAndTime = (TextView) itemView.findViewById(R.id.tvDateAndTime);
        }
    }
}
