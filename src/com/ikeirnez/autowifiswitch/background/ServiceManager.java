package com.ikeirnez.autowifiswitch.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import com.ikeirnez.autowifiswitch.enums.SoftwareType;
import com.ikeirnez.autowifiswitch.listeners.PowerSaverListener;

import java.util.concurrent.TimeUnit;

/**
 * Created by iKeirNez on 14/08/2014.
 */
public class ServiceManager extends BroadcastReceiver {

    private static AlarmManager alarmManager;
    private static PowerManager powerManager;
    private static WifiManager wifiManager;

    public static boolean initialized = false;
    public static boolean serviceRunning = false;

    private static PowerSaverListener powerSaverListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!serviceRunning) {
            updateScanningService(context);
        }
    }

    public static void updateScanningService(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (!initialized) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            SoftwareType softwareType = SoftwareType.getRunningSoftwareType(context);
            if (softwareType != null) {
                context.getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, powerSaverListener = new PowerSaverListener(context));
            }

            initialized = true;
        }

        if (serviceRunning){
            cancelService(context);
        }

        if (!preferences.getBoolean("enabled", true) || !wifiManager.isWifiEnabled()){
            Log.i("Service Manager", "Service not started, scanning disabled or wifi disabled.");
            return; // don't continue
        }

        SoftwareType softwareType = SoftwareType.getRunningSoftwareType(context);
        if (softwareType != null && softwareType.getPowerSaverStatus(context)) {
            Log.i("Service Manager", "Service not started, power saver mode active");
            return; // don't continue initializing if in power saver
        }

        modifyScanningInterval(context);
    }

    public static void cancelService(Context context) {
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, new Intent(context, WifiService.class), PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    public static void modifyScanningInterval(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        Intent serviceIntent = new Intent(context, WifiService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);

        if (preferences.getBoolean("enabled", true)) {
            long millis;
            if (powerManager.isScreenOn()) {
                millis = TimeUnit.SECONDS.toMillis(Integer.parseInt(preferences.getString("update_interval", "10")));
                Log.i("Service Manager", "Scanning every " + millis + "ms (screen on)");
            } else {
                millis = TimeUnit.SECONDS.toMillis(Integer.parseInt(preferences.getString("update_interval_display_off", "30")));

                if (millis == 0) {
                    Log.i("Service Manager", "Disabling scanning service for now (screen off)");
                    return;
                }

                Log.i("Service Manager", "Scanning every " + millis + "ms (screen off)");
            }

            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + millis, millis, pendingIntent);
        }

    }
}
