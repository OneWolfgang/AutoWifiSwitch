package com.ikeirnez.autowifiswitch;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

/**
 * Created by iKeirNez on 26/07/2014.
 */
public enum NotificationType {

    NONE("None") {
        @Override
        public void doNotification(Context context, String network) {}
    }, TOAST("Toast") { // note, corresponding default in preferences.xml
        @Override
        public void doNotification(Context context, String network) {
            Toast.makeText(context, "AutoWifiSwitch: Connected to " + network, Toast.LENGTH_SHORT).show();
        }
    }, NOTIFICATION("Quick Notification") {
        @Override
        public void doNotification(Context context, String network) {
            if (notificationManager == null){
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            }

            if (handler == null){
                handler = new Handler();
            }

            String text = "AutoWifiSwitch: Connected to " + network;
            notificationManager.notify(1, new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher).setContentTitle("AutoWifiSwitch").setContentText(text).setTicker(text).getNotification());
            handler.postDelayed(new Runnable() { // todo better way to do this?
                @Override
                public void run() {
                    notificationManager.cancel(1);
                }
            }, TimeUnit.SECONDS.toMillis(2));
        }
    };

    private static NotificationManager notificationManager; // used for creating quick notifications
    private static Handler handler; // used to remove notifications quickly

    private final String friendlyName;

    private NotificationType(String friendlyName){
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public abstract void doNotification(Context context, String network);

}
