package com.nearby.syncpad.data;

import android.net.Uri;

public class ItemsContract {

	public static final String CONTENT_AUTHORITY = "com.nearby.syncpad.contentprovider";
	public static final Uri BASE_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

	interface ItemsColumns {
		/** Type: PRIMARY KEY */
		String MEETING_ID = "meeting_id";
		/** Type: TEXT NOT NULL */
		String MEETING_NAME = "meeting_name";
		/** Type: TEXT NOT NULL */
		String MEETING_DATE = "meeting_date";
		/** Type: TEXT NOT NULL */
		String MEETING_TIME = "meeting_time";
		/** Type: TEXT NOT NULL */
		String MEETING_VENUE = "meeting_venue";
        /** Type: TEXT NOT NULL */
        String MEETING_AGENDA = "meeting_agenda";
		/** Type: TEXT NOT NULL */
		String MEETING_NOTES = "meeting_notes";
		/** Type: TEXT NOT NULL */
		String MEETING_PARTICIPANTS = "meeting_participants";
	}

	public static class Items implements ItemsColumns {
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.nearby.syncpad.contentprovider.meetings";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.nearby.syncpad.contentprovider.meetings";

        public static final String DEFAULT_SORT = MEETING_DATE + " DESC";

		/** Matches: /meetings/ */
		public static Uri buildDirUri() {
			return BASE_URI.buildUpon().appendPath("meetings").build();
		}

		/** Matches: /meetings/[_id]/ */
		public static Uri buildItemUri(String _id) {
			return BASE_URI.buildUpon().appendPath("meetings").appendPath(_id).build();
		}

        /** Read meetings ID item detail URI. */
        public static String getItemId(Uri itemUri) {
            return itemUri.getPathSegments().get(1);
        }
	}

	private ItemsContract() {
	}
}
