package com.nearby.syncpad.data;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

/**
 * Helper for loading a list of articles or a single article.
 */
public class MeetingNotesLoader extends CursorLoader {
    //Constructor
    public static MeetingNotesLoader newAllNotesInstance(Context context) {
        return new MeetingNotesLoader(context, ItemsContract.Items.buildDirUri());
    }

    public static MeetingNotesLoader newInstanceForItemId(Context context, long itemId) {
        return new MeetingNotesLoader(context, ItemsContract.Items.buildItemUri(itemId));
    }

    private MeetingNotesLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, ItemsContract.Items.DEFAULT_SORT);
    }

    public interface Query {
        String[] PROJECTION = {
                ItemsContract.Items.MEETING_ID,
                ItemsContract.Items.MEETING_NAME,
                ItemsContract.Items.MEETING_DATE,
                ItemsContract.Items.MEETING_TIME,
                ItemsContract.Items.MEETING_VENUE,
                ItemsContract.Items.MEETING_AGENDA,
                ItemsContract.Items.MEETING_NOTES,
                ItemsContract.Items.MEETING_PARTICIPANTS,
        };

        int MEETING_ID = 0;
        int MEETING_NAME = 1;
        int MEETING_DATE = 2;
        int MEETING_TIME = 3;
        int MEETING_VENUE = 4;
        int MEETING_AGENDA = 5;
        int MEETING_NOTES = 6;
        int MEETING_PARTICIPANTS = 7;
    }
}
