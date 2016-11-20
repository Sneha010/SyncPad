package com.nearby.syncpad.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.nearby.syncpad.MainActivity;
import com.nearby.syncpad.MeetingDetailsActivity;
import com.nearby.syncpad.R;

/**
 * Created by Sneha Khadatare : 587823
 * on 11/18/2016.
 */

public class MyAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId :  appWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.rlHeader, pendingIntent);

            setRemoteAdapter(context, remoteViews);

            Intent clickIntentTemplate = new Intent(context, MeetingDetailsActivity.class);

            appWidgetManager.updateAppWidget(appWidgetId , remoteViews);
        }
    }

    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
       /* views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, DetailWidgetRemoteViewsService.class));*/
    }
}
