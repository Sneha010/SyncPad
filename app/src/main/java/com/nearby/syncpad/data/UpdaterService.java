package com.nearby.syncpad.data;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nearby.syncpad.R;
import com.nearby.syncpad.models.Meeting;
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

public class UpdaterService extends IntentService {

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

        if (!GeneralUtils.isOnline(this)) {
            Toast.makeText(this, getString(R.string.no_internet_connectivity), Toast.LENGTH_SHORT).show();
            sendBroadcast(
                    new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
            return;
        }
        sendBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));


        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

        Uri dirUri = ItemsContract.Items.buildDirUri();

        try {
            JSONArray array = RemoteEndpointUtil.fetchJsonArray(GeneralUtils.getUid());
            if (array != null) {

                // Delete all items
                cpo.add(ContentProviderOperation.newDelete(dirUri).build());

                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);

                    Meeting meeting = new Gson().fromJson(object.toString(), Meeting.class);

                    ContentValues values = GeneralUtils.getContentValues(meeting);

                    cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());

                    getContentResolver().applyBatch(ItemsContract.CONTENT_AUTHORITY, cpo);
                }
            }

        } catch (JSONException | RemoteException | OperationApplicationException e) {
            Log.e(TAG, "Error updating content.", e);
        }

        sendBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
    }
}
