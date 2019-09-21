package com.example.localmirza.v5_fgo_team_analyzer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class NotificationActivity extends BroadcastReceiver {
    /**
     * Creates the notification to be displayed to the user every day to ensure the user does not
     * forget. The pendingIntent is used to direct the user to the main screen of the app, when they
     * click on the notification .
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent notifIntent = new Intent(context, MainActivity.class);
        notifIntent.putExtra("from_notification", 1);

        PendingIntent contentIntent = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(notifIntent)
                .getPendingIntent(MainActivity.NOTIFICATION_TAPPED, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification builder = new Notification.Builder(context)
                .setTicker("Notifications")
                .setSmallIcon(R.drawable.baseline_check_circle_outline_white_24)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setContentTitle("FGO Analyzer")
                .setContentText("Come and Play!")
                .getNotification();

        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(0, builder);
    }

}
