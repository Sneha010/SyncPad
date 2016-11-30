package com.nearby.syncpad.util;


import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.nearby.syncpad.ActiveMeetingActivity;
import com.nearby.syncpad.R;

import static android.content.Context.NOTIFICATION_SERVICE;


public class NotificationHelper {


    private  Application mApplication;

    public NotificationHelper(Application application) {

        mApplication = application;
    }

    public void showOnGoingNotification(String title, String message) {

        NotificationManager mNotifyMgr =
                (NotificationManager) mApplication.getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();

        bigTextStyle.bigText(message);

        Intent mainActivity = new Intent(mApplication, ActiveMeetingActivity.class);

        mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(mApplication,
                0, mainActivity, 0);

        //notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        Notification notification = new NotificationCompat.Builder(mApplication)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.app_icon_small)
                .setContentIntent(pendingIntent)
                .setStyle(bigTextStyle)
                .setAutoCancel(true)
                .setColor(mApplication.getResources().getColor(R.color.colorPrimary))
                .setOngoing(true) // Again, THIS is the important line
                .build();
        notification.flags =  Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;


        mNotifyMgr.notify(123, notification);

    }

    public void cancelMeetingOngingNotification() {

        NotificationManager mNotifyMgr =
                (NotificationManager) mApplication.getSystemService(NOTIFICATION_SERVICE);


        mNotifyMgr.cancel(123);

    }

}
