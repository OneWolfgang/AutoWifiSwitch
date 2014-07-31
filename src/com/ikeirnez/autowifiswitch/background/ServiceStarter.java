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

    private static ServiceStarter instance;
    private static boolean registered = false;

    {
        instance = this;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            default: break;
            case Intent.ACTION_BOOT_COMPLETED:
            case Intent.ACTION_SCREEN_ON:
            case Intent.ACTION_SCREEN_OFF:
                startService(context);
                break;
        }
    }

    public static void startService(Context context){
        if (!registered){
            // (re-)register but this time for screen on/off, no need for BOOT_COMPLETED
            // for some reason this has to be set pragmatically and can't be done in the manifest
            if (instance != null){
                context.unregisterReceiver(instance);
            }

            instance = new ServiceStarter();
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
            context.registerReceiver(instance, intentFilter);
            registered = true;
        }

        if (alarmManager == null){
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }

        if (powerManager == null){
            powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        }

        //PreferenceManager.setDefaultValues(context, R.xml.preferences, false); // work around for preferences potentially not being loaded
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (preferences.getBoolean("enabled", true)){
            Intent serviceIntent = new Intent(context, WifiScanService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            alarmManager.cancel(pendingIntent); // cancel old timer

            long millis;
            if (powerManager.isScreenOn()){
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
}
