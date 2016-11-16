package com.nearby.syncpad.data;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.net.Uri;

import com.nearby.syncpad.util.GeneralUtils;

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
    }
}
