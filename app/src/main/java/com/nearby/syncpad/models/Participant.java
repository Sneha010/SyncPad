package com.nearby.syncpad.models;

import android.graphics.drawable.Drawable;

import com.google.android.gms.nearby.messages.Message;
import com.google.gson.Gson;

import java.nio.charset.Charset;


public class Participant {

    private String mName;
    private String mEmailAddress;

    private transient Drawable mProfilePicture;

    private String mTCSLocation;
    private String mRole;


    private String imageBytes;
    private String meetingNotes;
    private String toWhom;
    private String attendance;


    private static final Gson gson = new Gson();

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public String getToWhom() {
        return toWhom;
    }

    public void setToWhom(String toWhom) {
        this.toWhom = toWhom;
    }
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmailAddress() {
        return mEmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        mEmailAddress = emailAddress;
    }

    public Drawable getProfilePicture() {
        return mProfilePicture;
    }

    public void setProfilePicture(Drawable profilePicture) {
        mProfilePicture = profilePicture;
    }

    public String getTCSLocation() {
        return mTCSLocation;
    }

    public void setTCSLocation(String TCSLocation) {
        mTCSLocation = TCSLocation;
    }

    public String getRole() {
        return mRole;
    }

    public void setRole(String role) {
        mRole = role;
    }

    public String getMeetingNotes() {
        return meetingNotes;
    }

    public void setMeetingNotes(String meetingNotes) {
        this.meetingNotes = meetingNotes;
    }

    /**
     * Builds a new {@link Message} object using a unique identifier.
     */
    public Message newNearbyMessage() {

        Message message = new Message(gson.toJson(this).getBytes(Charset.forName("UTF-8")));
        return message;

    }

    /**
     * Creates a {@code Participant} object from the string used to construct the payload to a
     * {@code Nearby} {@code Message}.
     */
    public static Participant fromNearbyMessage(Message message) {
        String nearbyMessageString = new String(message.getContent()).trim();
        return gson.fromJson(
                (new String(nearbyMessageString.getBytes(Charset.forName("UTF-8")))),
                Participant.class);
    }

    public String getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(String imageBytes) {
        this.imageBytes = imageBytes;
    }
}
