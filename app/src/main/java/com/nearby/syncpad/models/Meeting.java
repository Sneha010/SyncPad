package com.nearby.syncpad.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class Meeting implements Parcelable {

    private String meetingName;
    private String meetingDate;
    private String meetingTime;
    private String meetingVenue;
    private String meetingAgenda;
    private ArrayList<String> participantNameList = new ArrayList<>();
    private ArrayList<String> notesList = new ArrayList<>();

    public Meeting(Parcel in) {
        meetingName = in.readString();
        meetingDate = in.readString();
        meetingTime = in.readString();
        meetingVenue = in.readString();
        meetingAgenda = in.readString();
        participantNameList = in.readArrayList(String.class.getClassLoader());
        notesList = in.readArrayList(String.class.getClassLoader());
    }

    public static final Creator<Meeting> CREATOR = new Creator<Meeting>() {
        @Override
        public Meeting createFromParcel(Parcel in) {
            return new Meeting(in);
        }

        @Override
        public Meeting[] newArray(int size) {
            return new Meeting[size];
        }
    };

    public Meeting() {

    }

    public ArrayList<String> getNotesList() {
        return notesList;
    }

    public void setNotesList(ArrayList<String> notesList) {
        this.notesList = notesList;
    }

    public ArrayList<String> getParticipantNameList() {
        return participantNameList;
    }

    public void setParticipantNameList(ArrayList<String> participantNameList) {
        this.participantNameList = participantNameList;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getMeetingDate() {
        return meetingDate;
    }

    public void setMeetingDate(String meetingDate) {
        this.meetingDate = meetingDate;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }

    public String getMeetingVenue() {
        return meetingVenue;
    }

    public void setMeetingVenue(String meetingVenue) {
        this.meetingVenue = meetingVenue;
    }

    public String getMeetingAgenda() {
        return meetingAgenda;
    }

    public void setMeetingAgenda(String meetingAgenda) {
        this.meetingAgenda = meetingAgenda;
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(meetingName);
        parcel.writeString(meetingDate);
        parcel.writeString(meetingTime);
        parcel.writeString(meetingVenue);
        parcel.writeString(meetingAgenda);
        parcel.writeList(participantNameList);
        parcel.writeList(notesList);
    }
}
