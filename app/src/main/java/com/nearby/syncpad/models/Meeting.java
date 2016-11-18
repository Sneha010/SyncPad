package com.nearby.syncpad.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Meeting implements Parcelable {

    private String meetingId;
    private String meetingName;
    private String meetingDate = "";
    private String meetingTime = "";
    private String meetingVenue = "";
    private String meetingAgenda = "";
    private String participantNameList = "";
    private String notesList = "";

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public Meeting(Parcel in) {
        meetingName = in.readString();
        meetingDate = in.readString();
        meetingTime = in.readString();
        meetingVenue = in.readString();
        meetingAgenda = in.readString();
        participantNameList = in.readString();
        notesList = in.readString();
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

    public String getParticipantNameList() {
        return participantNameList;
    }

    public void setParticipantNameList(String participantNameList) {
        this.participantNameList = participantNameList;
    }

    public String getNotesList() {
        return notesList;
    }

    public void setNotesList(String notesList) {
        this.notesList = notesList;
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
        parcel.writeString(participantNameList);
        parcel.writeString(notesList);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("meetingId", meetingId);
        result.put("meetingName", meetingName);
        result.put("meetingDate", meetingDate);
        result.put("meetingTime", meetingTime);
        result.put("meetingVenue", meetingVenue);
        result.put("meetingAgenda", meetingAgenda);
        result.put("meetingNotes", notesList);
        result.put("meetingParticipants", participantNameList);

        return result;
    }
}
