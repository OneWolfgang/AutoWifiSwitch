package com.ikeirnez.autowifiswitch.background;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.*;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import com.ikeirnez.autowifiswitch.R;
import com.ikeirnez.autowifiswitch.legacy.LegacySoftwareType;

import java.util.concurrent.TimeUnit;

/**
 * Created by iKeirNez on 14/08/2014.
 */
public class ServiceManager extends BroadcastReceiver {

    private static AlarmManager alarmManager;
    private static PowerManager powerManager;
    private static WifiManager wifiManager;
    private static Intent serviceIntent;

    public static boolean initialized = false;
    public static boolean serviceRunning = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);

        if (!serviceRunning) {
            updateScanningService(context);
        }
    }

    @SuppressLint("NewApi") // power saving apis giving lint errors
    public static void updateScanningService(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Log.i("Service Manager", "Updating service state");

        if (!initialized) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            serviceIntent = new Intent(context, WifiService.class);
            initialized = true;
        }

        if (serviceRunning){
            cancelService(context);
        }

        if (!preferences.getBoolean("enabled", true) || !wifiManager.isWifiEnabled()){
            Log.i("Service Manager", "Service not started, scanning disabled or wifi disabled.");
            return; // don't continue
        }

        boolean powerSaverStatus = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            powerSaverStatus = powerManager.isPowerSaveMode();
        } else { // legacy power saver detection
            LegacySoftwareType softwareType = LegacySoftwareType.getRunningSoftwareType(context);
            if (softwareType != null) {
                powerSaverStatus = softwareType.getPowerSaverStatus(context);
            }
        }

        if (powerSaverStatus){
            Log.i("Service Manager", "Service not started, power saver mode active");
            return; // don't continue initializing if in power saver
        }

        modifyScanningInterval(context);
    }

    public static void cancelService(Context context) {
        cancelService(PendingIntent.getService(context, 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT));
    }

    public static void cancelService(PendingIntent pendingIntent){
        alarmManager.cancel(pendingIntent);
    }

    public static void modifyScanningInterval(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        PendingIntent pendingIntent = PendingIntent.getService(context, 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        cancelService(pendingIntent);

        if (preferences.getBoolean("enabled", true)) {
            long millis;
            if (powerManager.isScreenOn()) {
                millis = TimeUnit.SECONDS.toMillis(Integer.parseInt(preferences.getString("update_interval", null)));
                Log.i("Service Manager", "Scanning every " + millis + "ms (screen on)");
            } else {
                millis = TimeUnit.SECONDS.toMillis(Integer.parseInt(preferences.getString("update_interval_display_off", null)));

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
