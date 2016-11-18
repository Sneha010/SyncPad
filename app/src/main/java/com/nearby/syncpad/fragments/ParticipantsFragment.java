package com.nearby.syncpad.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ParticipantsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ParticipantsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParticipantsFragment  extends Fragment{

    private OnFragmentInteractionListener mListener;

    private ArrayList<Participant> mParticipants;

    RecyclerView mRecyclerView;

    private ParticipantListItemAdapter adapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment ParticipantsFragment.
     */

    public static ParticipantsFragment newInstance() {
        ParticipantsFragment fragment = new ParticipantsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public ParticipantsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_participants, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.participantListView);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.addItemDecoration(new DataItemDecoration(getActivity(), DataItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mParticipants = new ArrayList<>();

        /*Participant participant = new Participant();
        participant.setProfilePicture(getResources().getDrawable(R.drawable.profile));
        participant.setName("Piyush Agarwal");
        participant.setRole("Developer");
        participant.setEmailAddress("piyush.agarwal2@tcs.com");
        mParticipants.add(participant);

        Participant participant2 = new Participant();
        participant2.setProfilePicture(getResources().getDrawable(R.drawable.profile2));
        participant2.setName("Sneha Khadatare");
        participant2.setRole("Developer");
        participant2.setEmailAddress("sneha.khadtare@tcs.com");
        mParticipants.add(participant2);

        Participant participant3 = new Participant();
        participant3.setProfilePicture(getResources().getDrawable(R.drawable.profile3));
        participant3.setName("Viral Islania");
        participant3.setRole("Developer");
        participant3.setEmailAddress("viral.islania@tcs.com");
        mParticipants.add(participant3);*/

        adapter = new ParticipantListItemAdapter(getActivity(),mParticipants);

        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

   /* public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

    public void addParticipant(Participant participant) {
        if (mParticipants.size() == 0) {
            adapter.updateList(participant);
        }else {
            for (Participant p : mParticipants) {

                if (!p.getEmailAddress().equalsIgnoreCase(participant.getEmailAddress())) {
                    adapter.updateList(participant);
                }

            }

        }

    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);

    }

}
