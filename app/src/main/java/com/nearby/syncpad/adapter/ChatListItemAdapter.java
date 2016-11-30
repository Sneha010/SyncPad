package com.nearby.syncpad.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nearby.syncpad.R;
import com.nearby.syncpad.models.Participant;
import com.nearby.syncpad.util.Constants;
import com.nearby.syncpad.util.ImageUtility;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChatListItemAdapter extends RecyclerView.Adapter<ChatListItemAdapter.ViewHolder> {


    private ArrayList<Participant> mParticipants;


    public ChatListItemAdapter(Activity context, ArrayList<Participant> participants) {
        this.mParticipants = participants;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item_view, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (mParticipants.get(position).getToWhom() != null && mParticipants.get(position).getToWhom().equals(Constants.TO_ME)) {
            holder.rlFromMyselfNotes.setVisibility(View.VISIBLE);
            holder.rlFromOthersNotes.setVisibility(View.GONE);
            holder.personNameMine.setText(mParticipants.get(position).getName());

            if (mParticipants.get(position).getMeetingNotes() != null) {
                holder.meetingNotesMine.setVisibility(View.VISIBLE);
                holder.meetingNotesMine.setText(mParticipants.get(position).getMeetingNotes());
            } else {
                holder.meetingNotesMine.setVisibility(View.GONE);
            }

        } else {
            holder.rlFromOthersNotes.setVisibility(View.VISIBLE);
            holder.rlFromMyselfNotes.setVisibility(View.GONE);
            holder.personNameOther.setText(mParticipants.get(position).getName());

            if (mParticipants.get(position).getMeetingNotes() != null) {
                holder.meetingNotesOther.setVisibility(View.VISIBLE);
                holder.meetingNotesOther.setText(mParticipants.get(position).getMeetingNotes());
            } else {
                holder.meetingNotesOther.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public int getItemCount() {
        return mParticipants.size();
    }

    public void updateList(Participant participant) {

        boolean gotSameProfile = false;
        if (mParticipants.size() > 0) {
            for (int i = 0; i < mParticipants.size(); i++) {
                if (mParticipants.get(i).getEmailAddress() != null && participant.getEmailAddress() != null
                        && mParticipants.get(i).getEmailAddress().equals(participant.getEmailAddress())) {
                    gotSameProfile = true;
                }
            }
        }
        if (!gotSameProfile) {
            mParticipants.add(participant);
            notifyDataSetChanged();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.personName_other)
        TextView personNameOther;

        @BindView(R.id.meetingNotes_other)
        TextView meetingNotesOther;

        @BindView(R.id.ll_fromOthersNotes)
        LinearLayout rlFromOthersNotes;

        @BindView(R.id.personName_mine)
        TextView personNameMine;

        @BindView(R.id.meetingNotes_mine)
        TextView meetingNotesMine;

        @BindView(R.id.ll_fromMyselfNotes)
        LinearLayout rlFromMyselfNotes;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

        }
    }
}
