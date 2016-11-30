package com.nearby.syncpad.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nearby.syncpad.R;
import com.nearby.syncpad.adapter.ParticipantListItemAdapter;
import com.nearby.syncpad.models.Participant;
import com.nearby.syncpad.util.DataItemDecoration;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ParticipantsFragment extends Fragment {

    @BindView(R.id.participantListView)
    RecyclerView mRecyclerView;

    private ArrayList<Participant> mParticipants;

    private ParticipantListItemAdapter adapter;


    public static ParticipantsFragment newInstance() {
        ParticipantsFragment fragment = new ParticipantsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_participants, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.addItemDecoration(new DataItemDecoration(getActivity(), DataItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mParticipants = new ArrayList<>();

        adapter = new ParticipantListItemAdapter(getActivity(), mParticipants);

        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    public void addParticipant(Participant participant) {
     /*   if (mParticipants.size() == 0) {
            adapter.updateList(participant);
        } else {

            for (Participant p : mParticipants) {

                if (!p.getEmailAddress().equalsIgnoreCase(participant.getEmailAddress())) {
                    adapter.updateList(participant);
                }

            }

        }*/

        adapter.updateList(participant);

    }

}
