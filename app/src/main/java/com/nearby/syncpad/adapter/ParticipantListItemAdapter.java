package com.nearby.syncpad.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nearby.syncpad.R;
import com.nearby.syncpad.models.Participant;
import com.nearby.syncpad.util.GeneralUtils;

import java.util.ArrayList;


public class ParticipantListItemAdapter extends RecyclerView.Adapter<ParticipantListItemAdapter.ViewHolder>{

    private ArrayList<Participant> mParticipants;

    public ParticipantListItemAdapter(Context context , ArrayList<Participant> participants) {
        this.mParticipants = participants;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.participant_item_view, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //holder.mProfileImage.setImageDrawable(mParticipants.get(position).getProfilePicture());
        if(mParticipants.get(position).getToWhom()!=null && mParticipants.get(position).getToWhom().equals("to_Me")){
            holder.rl_fromMyselfNotes.setVisibility(View.VISIBLE);
            holder.rl_fromOthersNotes.setVisibility(View.GONE);
            holder.mPersonName_mine.setText(mParticipants.get(position).getName());
            holder.mProfileImage_mine.setImageDrawable(GeneralUtils.getImageFromByteArray(mParticipants.get(position).getImageBytes()));

            if(mParticipants.get(position).getMeetingNotes()!=null){
                holder.meetingNotes_mine.setVisibility(View.VISIBLE);
                holder.meetingNotes_mine.setText(mParticipants.get(position).getMeetingNotes());
            }else{
                holder.meetingNotes_mine.setVisibility(View.GONE);
            }
            if(mParticipants.get(position).getRole()!=null){
                holder.mPersonRole_mine.setVisibility(View.VISIBLE);
                holder.mPersonRole_mine.setText(mParticipants.get(position).getRole());
            }else{
                holder.mPersonRole_mine.setVisibility(View.GONE);
            }

        }else{
            holder.rl_fromOthersNotes.setVisibility(View.VISIBLE);
            holder.rl_fromMyselfNotes.setVisibility(View.GONE);
            holder.mPersonName.setText(mParticipants.get(position).getName());
            holder.meetingNotes.setText(mParticipants.get(position).getMeetingNotes());
            holder.mProfileImage.setImageDrawable(GeneralUtils.getImageFromByteArray(mParticipants.get(position).getImageBytes()));

            if(mParticipants.get(position).getMeetingNotes()!=null){
                holder.meetingNotes.setVisibility(View.VISIBLE);
                holder.meetingNotes.setText(mParticipants.get(position).getMeetingNotes());
            }else{
                holder.meetingNotes.setVisibility(View.GONE);
            }
            if(mParticipants.get(position).getRole()!=null){
                holder.mPersonRole.setVisibility(View.VISIBLE);
                holder.mPersonRole.setText(mParticipants.get(position).getRole());
            }else{
                holder.mPersonRole.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mParticipants.size();
    }

    public void updateList(Participant participant) {

        boolean gotSameProfile = false;
        if(mParticipants.size() > 0){
            for( int i = 0 ; i < mParticipants.size() ; i++){
                if(mParticipants.get(i).getEmailAddress()!=null && participant.getEmailAddress()!=null
                        && mParticipants.get(i).getEmailAddress().equals(participant.getEmailAddress())){
                    gotSameProfile = true;
                }
            }
        }
        if(!gotSameProfile){
            mParticipants.add(participant);
            notifyDataSetChanged();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rl_fromOthersNotes;
        RelativeLayout rl_fromMyselfNotes;
        ImageView mProfileImage;
        TextView mPersonName;
        TextView mPersonRole;
        TextView meetingNotes;
        ImageView mProfileImage_mine;
        TextView mPersonName_mine;
        TextView mPersonRole_mine;
        TextView meetingNotes_mine;


        public ViewHolder(View itemView) {
            super(itemView);

            rl_fromOthersNotes = (RelativeLayout) itemView.findViewById(R.id.rl_fromOthersNotes);
            rl_fromMyselfNotes = (RelativeLayout) itemView.findViewById(R.id.rl_fromMyselfNotes);
            mProfileImage = (ImageView) itemView.findViewById(R.id.profileImage);
            mPersonName = (TextView) itemView.findViewById(R.id.personName);
            mPersonRole = (TextView) itemView.findViewById(R.id.personRole);
            meetingNotes = (TextView) itemView.findViewById(R.id.meetingNotes);
            mPersonName_mine = (TextView) itemView.findViewById(R.id.personName_mine);
            mPersonRole_mine = (TextView) itemView.findViewById(R.id.personRole_mine);
            meetingNotes_mine = (TextView) itemView.findViewById(R.id.meetingNotes_mine);
            mProfileImage_mine = (ImageView) itemView.findViewById(R.id.profileImage_mine);

        }
    }
}
