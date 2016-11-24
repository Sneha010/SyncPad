package com.nearby.syncpad.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Sneha Khadatare : 587823
 * on 11/21/2016.
 */

public class WidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataProvider(getApplicationContext());
    }
}
