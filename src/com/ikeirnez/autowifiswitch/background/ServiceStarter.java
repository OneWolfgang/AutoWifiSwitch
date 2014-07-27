package com.ikeirnez.autowifiswitch.background;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by iKeirNez on 26/07/2014.
 */
public class ServiceStarter extends BroadcastReceiver {

    private static AlarmManager alarmManager;
    private static PendingIntent pendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            startService(context);
        }
    }

    public static void startService(Context context){
        if (alarmManager == null){
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }

        if (pendingIntent != null){
            alarmManager.cancel(pendingIntent);
        }

        //PreferenceManager.setDefaultValues(context, R.xml.preferences, false); // work around for preferences potentially not being loaded
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (preferences.getBoolean("enabled", false)){
            Intent serviceIntent = new Intent(context, WifiScanService.class);
            long millis = TimeUnit.SECONDS.toMillis(Integer.parseInt(preferences.getString("update_interval", "10")));
            pendingIntent = PendingIntent.getService(context, 0, serviceIntent, 0);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), millis, pendingIntent);
        }
    }
}
