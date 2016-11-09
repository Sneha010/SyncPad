package com.nearby.syncpad.models;

import java.util.ArrayList;


public class Meeting {

    private String meetingName;
    private String meetingDate;
    private String meetingTime;
    private String meetingVenue;
    private String meetingAgenda;
    private String meetingSummary;

    private ArrayList<Participant> noOfParticipants;

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

    public String getMeetingSummary() {
        return meetingSummary;
    }

    public void setMeetingSummary(String meetingSummary) {
        this.meetingSummary = meetingSummary;
    }

    public ArrayList<Participant> getNoOfParticipants() {
        return noOfParticipants;
    }

    public void setNoOfParticipants(ArrayList<Participant> noOfParticipants) {
        this.noOfParticipants = noOfParticipants;
    }
}
