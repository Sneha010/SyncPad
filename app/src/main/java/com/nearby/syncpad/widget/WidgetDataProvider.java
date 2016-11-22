package com.nearby.syncpad.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.nearby.syncpad.MainActivity;
import com.nearby.syncpad.MeetingDetailsActivity;
import com.nearby.syncpad.R;
import com.nearby.syncpad.data.ItemsContract;
import com.nearby.syncpad.data.MeetingNotesLoader;
import com.nearby.syncpad.data.NotesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sneha Khadatare : 587823
 * on 11/21/2016.
 */

public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory{

    private Context mContext;
    private Cursor mCursor;

    public WidgetDataProvider(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        fetchData();
    }

    @Override
    public void onDataSetChanged() {
        fetchData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if(mCursor!=null){
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if(mCursor != null && mCursor.moveToPosition(position) ){
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName() , R.layout.meetings_list_item);
            remoteViews.setTextViewText(R.id.tvMeetingTitle , mCursor.getString(MeetingNotesLoader.Query.MEETING_NAME));
            remoteViews.setTextViewText(R.id.tvNotes ,mCursor.getString(MeetingNotesLoader.Query.MEETING_NOTES));

            Intent intent = new Intent(mContext , MeetingDetailsActivity.class);
            intent.putExtra("item_id" , mCursor.getString(MeetingNotesLoader.Query.MEETING_ID));
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext , 0 ,intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.rlMainContentView , pendingIntent);
            return remoteViews;
        }else{
            return null;
        }

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void fetchData(){
        final long identityToken = Binder.clearCallingIdentity();
        mCursor = mContext.getContentResolver().query(
                ItemsContract.Items.buildDirUri(),
                MeetingNotesLoader.Query.PROJECTION,
               null,
                null,
                null);
        Binder.restoreCallingIdentity(identityToken);
    }
}
