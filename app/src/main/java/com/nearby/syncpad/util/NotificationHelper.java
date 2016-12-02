package com.nearby.syncpad.util;


import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.nearby.syncpad.ActiveMeetingActivity;
import com.nearby.syncpad.R;

import static android.content.Context.NOTIFICATION_SERVICE;


public class NotificationHelper {

    public static final int NOTIFICATION_ID = 123;
    private  Application mApplication;

    public NotificationHelper(Application application) {

        mApplication = application;
    }

    public void showOnGoingNotification(String title, String message) {

        Drawable myDrawable = ContextCompat.getDrawable(mApplication,
                R.drawable.notification_icon);
        Bitmap largeIconBitmap = ((BitmapDrawable) myDrawable).getBitmap();

        NotificationManager mNotifyMgr =
                (NotificationManager) mApplication.getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();

        bigTextStyle.bigText(message);

        Intent meetingIntent = new Intent(mApplication, ActiveMeetingActivity.class);

        meetingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(mApplication,
                0, meetingIntent, 0);


        Notification notification = new NotificationCompat.Builder(mApplication)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.status_bar_notification_icon)
                .setLargeIcon(largeIconBitmap)
                .setContentIntent(pendingIntent)
                .setStyle(bigTextStyle)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(mApplication ,R.color.colorPrimary))
                .setOngoing(true)
                .build();
        notification.flags =  Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;


        mNotifyMgr.notify(NOTIFICATION_ID, notification);

    }

    public void cancelMeetingOngingNotification() {

        NotificationManager mNotifyMgr =
                (NotificationManager) mApplication.getSystemService(NOTIFICATION_SERVICE);


        mNotifyMgr.cancel(NOTIFICATION_ID);

    }

}
