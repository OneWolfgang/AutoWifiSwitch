package com.ikeirnez.autowifiswitch.background;

import android.app.*;
import android.content.*;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import com.ikeirnez.autowifiswitch.constants.SoftwareType;
import com.ikeirnez.autowifiswitch.listeners.PowerSaverListener;

import java.util.concurrent.TimeUnit;

/**
 * Created by iKeirNez on 26/07/2014.
 */
public class ServiceManager extends BroadcastReceiver {

    private static AlarmManager alarmManager;
    private static PowerManager powerManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            startService(context);
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

    public static void startService(Context context){
        SoftwareType softwareType = SoftwareType.getRunningSoftwareType(context);

        if (softwareType != null && PreferenceManager.getDefaultSharedPreferences(context).getBoolean("power_saver_disables", false)){
            ServiceManager.enablePowerSaverMonitor(context);

            if (softwareType.getPowerSaverStatus(context)){
                return; // prevent service starting if power saver on
            }
        }

        initManagers(context);
        startService(context, powerManager.isScreenOn());
    }

    public static void startService(Context context, boolean screenStatus){
        initManagers(context);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (preferences.getBoolean("enabled", true)){
            Intent serviceIntent = new Intent(context, WifiService.class);
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
        cancelService(context, PendingIntent.getService(context, 0, new Intent(context, WifiService.class), PendingIntent.FLAG_CANCEL_CURRENT));
    }

    public static void cancelService(Context context, PendingIntent pendingIntent){
        initManagers(context);
        alarmManager.cancel(pendingIntent);
    }

    private static PowerSaverListener powerSaverListener;

    public static void enablePowerSaverMonitor(Context context){
        context.getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, powerSaverListener = new PowerSaverListener(context));

        if (SoftwareType.getRunningSoftwareType(context).getPowerSaverStatus(context)){
            cancelService(context);
        }
    }

    public static void disablePowerSaverMonitor(Context context){
        if (powerSaverListener != null){
            context.getContentResolver().unregisterContentObserver(powerSaverListener);
            powerSaverListener = null;
        }

        startService(context);
    }
}
