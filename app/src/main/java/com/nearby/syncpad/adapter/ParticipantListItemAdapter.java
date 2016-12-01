package com.nearby.syncpad.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nearby.syncpad.R;
import com.nearby.syncpad.SyncPadApplication;
import com.nearby.syncpad.models.Participant;
import com.nearby.syncpad.util.GeneralUtils;
import com.nearby.syncpad.util.ImageUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class ParticipantListItemAdapter extends RecyclerView.Adapter<ParticipantListItemAdapter.ViewHolder> {

    private ArrayList<Participant> mParticipants;

    @Inject
    ImageUtils mImageUtility;

    private Context mContext;


    public ParticipantListItemAdapter(Activity context, ArrayList<Participant> participants) {
        this.mParticipants = participants;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.participant_item_view, parent, false);

        ((SyncPadApplication) parent.getContext().getApplicationContext()).getMyApplicationComponent().inject(this);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.personName.setText(mParticipants.get(position).getName());

        if (mParticipants.get(position).getImageBytes() != null)
            holder.profileImage.setImageDrawable(GeneralUtils.getImageFromByteArray(mParticipants.get(position).getImageBytes()));
        else
            holder.profileImage.setImageDrawable(ContextCompat.getDrawable(mContext , R.drawable.default_user));

        String role = "", org = "";
        if (mParticipants.get(position).getRole() != null) {
            role = mParticipants.get(position).getRole();
        }

        if (mParticipants.get(position).getOrganisation() != null) {
            org = mParticipants.get(position).getOrganisation();

        }

        if(!GeneralUtils.isEmpty(role) || !GeneralUtils.isEmpty(org)){
            holder.personOrgAndRole.setVisibility(View.VISIBLE);
            holder.personOrgAndRole.setText(role +", "+org);
        }else{
            holder.personOrgAndRole.setVisibility(View.GONE);
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
                    break;
                }
            }
        }
        if (!gotSameProfile) {
            mParticipants.add(participant);
            notifyItemChanged(mParticipants.size()-1);
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.profileImage)
        CircleImageView profileImage;

        @BindView(R.id.personName)
        TextView personName;

        @BindView(R.id.personOrgAndRole)
        TextView personOrgAndRole;


        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
