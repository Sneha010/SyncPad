package com.nearby.syncpad.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;


public class Meeting implements Parcelable {

    private String meetingId;
    private String meetingName;
    private String meetingDate = "";
    private String meetingTime = "";
    private String meetingVenue = "";
    private String meetingAgenda = "";
    private String meetingParticipants = "";
    private String meetingNotes = "";
    private long meetingTimeStamp;

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
        meetingParticipants = in.readString();
        meetingNotes = in.readString();
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

    public long getMeetingTimeStamp() {
        return meetingTimeStamp;
    }

    public void setMeetingTimeStamp(long meetingTimeStamp) {
        this.meetingTimeStamp = meetingTimeStamp;
    }

    public Meeting() {

    }

    public String getMeetingParticipants() {
        return meetingParticipants;
    }

    public void setMeetingParticipants(String meetingParticipants) {
        this.meetingParticipants = meetingParticipants;
    }

    public String getMeetingNotes() {
        return meetingNotes;
    }

    public void setMeetingNotes(String meetingNotes) {
        this.meetingNotes = meetingNotes;
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
        parcel.writeString(meetingParticipants);
        parcel.writeString(meetingNotes);
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
        result.put("meetingNotes", meetingNotes);
        result.put("meetingParticipants", meetingParticipants);
        result.put("meetingTimeStamp", meetingTimeStamp);

        return result;
    }
}
