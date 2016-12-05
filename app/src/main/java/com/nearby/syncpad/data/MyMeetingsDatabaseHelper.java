package com.nearby.syncpad.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyMeetingsDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "syncpad.db";
    private static final int DATABASE_VERSION = 1;

    public MyMeetingsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + NotesProvider.Tables.MEETINGS + " ("
                + ItemsContract.ItemsColumns.MEETING_ID + " TEXT PRIMARY KEY,"
                + ItemsContract.ItemsColumns.MEETING_NAME + " TEXT NOT NULL,"
                + ItemsContract.ItemsColumns.MEETING_DATE + " TEXT NOT NULL,"
                + ItemsContract.ItemsColumns.MEETING_TIME + " TEXT NOT NULL,"
                + ItemsContract.ItemsColumns.MEETING_VENUE + " TEXT NOT NULL,"
                + ItemsContract.ItemsColumns.MEETING_AGENDA + " TEXT NOT NULL,"
                + ItemsContract.ItemsColumns.MEETING_NOTES + " TEXT NOT NULL,"
                + ItemsContract.ItemsColumns.MEETING_PARTICIPANTS + " TEXT NOT NULL,"
                + ItemsContract.ItemsColumns.MEETING_TIMESTAMP + " INTEGER"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NotesProvider.Tables.MEETINGS);
        onCreate(db);
    }
}
