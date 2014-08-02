package com.ikeirnez.autowifiswitch.background;

import android.app.*;
import android.content.*;
import android.os.PowerManager;
import android.preference.PreferenceManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by iKeirNez on 26/07/2014.
 */
public class ServiceStarter extends BroadcastReceiver {

    private static AlarmManager alarmManager;
    private static PowerManager powerManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            rescheduleService(context);
        }
    }

    public static void initManagers(Context context){
        if (alarmManager == null){
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }

        if (powerManager == null){
            powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        }
    }

    public static void rescheduleService(Context context){
        initManagers(context);
        rescheduleService(context, powerManager.isScreenOn());
    }

    public static void rescheduleService(Context context, boolean screenStatus){
        initManagers(context);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (preferences.getBoolean("enabled", true)){
            Intent serviceIntent = new Intent(context, WifiScanService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            alarmManager.cancel(pendingIntent); // cancel old timer

            long millis;
            if (screenStatus){
                millis = TimeUnit.SECONDS.toMillis(Integer.parseInt(preferences.getString("update_interval", "10")));
            } else {
                millis = TimeUnit.SECONDS.toMillis(Integer.parseInt(preferences.getString("update_interval_display_off", "30")));

                if (millis == 0){
                    return;
                }
            }

            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), millis, pendingIntent);
        }
    }

    public static void cancelService(Context context){
        cancelService(context, PendingIntent.getService(context, 0, new Intent(context, WifiScanService.class), PendingIntent.FLAG_CANCEL_CURRENT));
    }

    public static void cancelService(Context context, PendingIntent pendingIntent){
        initManagers(context);
        alarmManager.cancel(pendingIntent);
    }
}
