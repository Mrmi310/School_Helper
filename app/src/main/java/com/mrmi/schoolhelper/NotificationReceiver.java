package com.mrmi.schoolhelper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotificationReceiver extends BroadcastReceiver
{
    private String channelID = "com.mrmi.schoolhelper";
    private String description = "School helper notifications";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        System.out.println("[MRMI]: RECEIVED CALL IN NOTIFICATION RECEIVER");
        String assignmentName = intent.getStringExtra("Assignment name");
        showNotification(context, assignmentName);
    }

    private void showNotification(Context context, String assignmentName)
    {
        Intent myIntent = new Intent(context, Homework.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 310310, myIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel; //Used in android O and above
        Notification.Builder builder;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            //Using notificationChannel in android versions after and including Oreo
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                notificationChannel = new NotificationChannel(channelID, description, NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableVibration(false);
                notificationManager.createNotificationChannel(notificationChannel);

                //Build notification
                builder = new Notification.Builder(context, channelID)
                        .setAutoCancel(true)
                        .setContentText(assignmentName + " is due very soon!")
                        .setContentTitle("School Helper")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentIntent(pendingIntent)
                        .setVisibility(Notification.VISIBILITY_PRIVATE);
            }
            else
            {
                //Build notification
                builder = new Notification.Builder(context)
                        .setAutoCancel(true)
                        .setContentText(assignmentName + " is due very soon!")
                        .setContentTitle("School Helper")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentIntent(pendingIntent)
                        .setVisibility(Notification.VISIBILITY_PRIVATE)
                        .setPriority(Notification.PRIORITY_MAX);
            }

            //Notify user with built notification
            notificationManager.notify(310310, builder.build());
        }

    }
}