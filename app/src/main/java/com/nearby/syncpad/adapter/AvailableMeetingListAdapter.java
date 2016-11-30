package com.nearby.syncpad.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nearby.syncpad.R;
import com.nearby.syncpad.models.Participant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AvailableMeetingListAdapter extends RecyclerView.Adapter<AvailableMeetingListAdapter.ViewHolder> {

    private ArrayList<Participant> meetingHosts;
    private OnMeetingItemSelectedListener mOnMeetingItemSelectedListener;

    public AvailableMeetingListAdapter(OnMeetingItemSelectedListener mOnMeetingItemSelectedListener, ArrayList<Participant> meetingHost) {
        this.mOnMeetingItemSelectedListener = mOnMeetingItemSelectedListener;
        this.meetingHosts = meetingHost;
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

        if (meetingHosts.get(position).isIsHost()) {

            holder.tvMeetingTitle.setText(meetingHosts.get(position).getMeeting().getMeetingName());

            if (!meetingHosts.get(position).getName().isEmpty()) {
                holder.tvInitiatedBy.setVisibility(View.VISIBLE);
                holder.tvInitiatedBy.append(meetingHosts.get(position).getName());
            } else {
                holder.tvInitiatedBy.setVisibility(View.GONE);
            }

            holder.rlDataContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mOnMeetingItemSelectedListener.onMeetingItemSelected(meetingHosts.get(position));
                }
            });

        }


    }

    public interface OnMeetingItemSelectedListener{
        void onMeetingItemSelected(Participant meeting);
    }

    @Override
    public int getItemCount() {
        return meetingHosts.size();
    }

    public void updateList(Participant meeting) {

        meetingHosts.add(meeting);
        notifyItemChanged(meetingHosts.size()-1);

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
