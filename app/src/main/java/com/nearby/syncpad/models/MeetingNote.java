package com.nearby.syncpad.models;

/**
 * Created by Sneha on 11/12/2016.
 */

public class MeetingNote {

    private String mMeetingNote;
    private String mNotesBy;

    public MeetingNote(String mMeetingNote, String mNotesBy) {
        this.mMeetingNote = mMeetingNote;
        this.mNotesBy = mNotesBy;
    }

    public String getMeetingNote() {
        return mMeetingNote;
    }

    public void setMeetingNote(String mMeetingNote) {
        this.mMeetingNote = mMeetingNote;
    }

    public String getNotesBy() {
        return mNotesBy;
    }

    public void setNotesBy(String mNotesBy) {
        this.mNotesBy = mNotesBy;
    }
}
