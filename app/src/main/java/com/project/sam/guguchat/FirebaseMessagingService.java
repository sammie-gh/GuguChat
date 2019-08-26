package com.project.sam.guguchat;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by A.Richard on 14/07/2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notification_title = remoteMessage.getNotification().getTitle();
        String notification_message =  remoteMessage.getNotification().getBody();
        String click_action = remoteMessage.getNotification().getClickAction();
        String from_user_id = remoteMessage.getData().get("from_user_id");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(  this)
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent))
                        .setContentTitle(notification_title)
                        .setContentText(notification_message)
                        //experimenting
                        .setAutoCancel(true)
                ;
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ico_3);
        mBuilder.setLargeIcon(largeIcon);
// trying styles
        NotificationCompat.InboxStyle notification =
                new NotificationCompat.InboxStyle(mBuilder);
        notification
                .setBigContentTitle(notification_title)
                .setSummaryText(notification_message);

        Intent resultIntent = new Intent(click_action);
        resultIntent.putExtra( "user_id", from_user_id);



        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT

                );
        mBuilder.setContentIntent(resultPendingIntent);


        //set an ID  for the  notification
        int mNotificationID = (int ) System.currentTimeMillis();

        NotificationManager mNotifyMGR =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMGR.notify(mNotificationID, mBuilder.build());


    }
}
