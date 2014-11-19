package com.ikeirnez.autowifiswitch.enums;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;
import com.ikeirnez.autowifiswitch.R;

import java.util.concurrent.TimeUnit;

/**
 * Handles various methods of notifying players to a switch of network
 */
public enum NotificationType {

    NONE(R.string.notification_type_none) {
        @Override
        public void doNotification(Context context, String network) {}
    }, TOAST(R.string.notification_type_toast) { // note, corresponding default in preferences.xml
        @Override
        public void doNotification(Context context, String network) {
            Toast.makeText(context, context.getString(R.string.connected_notification, network), Toast.LENGTH_SHORT).show();
        }
    }, NOTIFICATION(R.string.notification_type_quick_notification) {
        @Override
        public void doNotification(Context context, String network) {
            if (notificationManager == null){
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            }

            if (handler == null){
                handler = new Handler();
            }

            String text = context.getString(R.string.connected_notification, network);
            notificationManager.notify(1, new Notification.Builder(context)
                    .setSmallIcon(R.drawable.notification_icon).setContentTitle(context.getString(R.string.app_name)).setContentText(text).setTicker(text).getNotification());
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

    private final int friendlyNameResId;
    private String friendlyName;

    private NotificationType(int friendlyNameResId){
        this.friendlyNameResId = friendlyNameResId;
    }

    public String getFriendlyName(Context context) {
        if (friendlyName == null){
            friendlyName = context.getString(friendlyNameResId);
        }

        return friendlyName;
    }

    public abstract void doNotification(Context context, String network);

}
