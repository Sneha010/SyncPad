package com.nearby.syncpad.data;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.nearby.syncpad.remote.RemoteEndpointUtil;
import com.nearby.syncpad.util.GeneralUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sneha Khadatare : 587823
 * on 11/16/2016.
 */

public class UpdaterService extends IntentService{

    private static final String TAG = "UpdaterService";

    public static final String BROADCAST_ACTION_STATE_CHANGE
            = "com.nearby.syncpad.intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING
            = "com.nearby.syncpad.intent.extra.REFRESHING";

    public UpdaterService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if(!GeneralUtils.isOnline(this)){
            return;
        }
        sendStickyBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));

        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

        Uri dirUri = ItemsContract.Items.buildDirUri();

        // Delete all items
        cpo.add(ContentProviderOperation.newDelete(dirUri).build());

        try {
            JSONArray array = RemoteEndpointUtil.fetchJsonArray(GeneralUtils.getUid());
            if (array == null) {
                throw new JSONException("Invalid parsed item array" );
            }

            for (int i = 0; i < array.length(); i++) {
                ContentValues values = new ContentValues();
                JSONObject object = array.getJSONObject(i);
                values.put(ItemsContract.Items.MEETING_ID, object.getString("meetingId" ));
                values.put(ItemsContract.Items.MEETING_NAME, object.getString("meetingName" ));
                values.put(ItemsContract.Items.MEETING_DATE, object.getString("meetingDate" ));
                values.put(ItemsContract.Items.MEETING_TIME, object.getString("meetingTime" ));
                values.put(ItemsContract.Items.MEETING_VENUE, object.getString("meetingVenue" ));
                values.put(ItemsContract.Items.MEETING_AGENDA, object.getString("meetingAgenda" ));
                values.put(ItemsContract.Items.MEETING_NOTES, object.getString("meetingNotes" ));
                values.put(ItemsContract.Items.MEETING_PARTICIPANTS, object.getString("meetingParticipants" ));
                cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());
            }

            getContentResolver().applyBatch(ItemsContract.CONTENT_AUTHORITY, cpo);

        } catch (JSONException | RemoteException | OperationApplicationException e) {
            Log.e(TAG, "Error updating content.", e);
        }

        sendStickyBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
    }
}
