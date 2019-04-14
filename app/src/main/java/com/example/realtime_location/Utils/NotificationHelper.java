package com.example.realtime_location.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.realtime_location.R;


public class NotificationHelper extends ContextWrapper {
    private static final String NAARI_CHANNEL_ID="com.example.realtime_location";
    private static final String NAARI_CHANNEL_NAME ="Realtime";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            createChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel naariChannel = new NotificationChannel(NAARI_CHANNEL_ID,NAARI_CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
    naariChannel.enableLights(false);
    naariChannel.enableVibration(true);
    naariChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

    getManager().createNotificationChannel(naariChannel);

    }

    public NotificationManager getManager() {
        if (manager == null)
            manager =(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return  manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getRealtimeTrackingNotification(String title, String content, Uri defaultSound) {

        return new Notification.Builder(getApplicationContext(),NAARI_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(content)
                .setSound(defaultSound)
                .setAutoCancel(false);
    }
}
