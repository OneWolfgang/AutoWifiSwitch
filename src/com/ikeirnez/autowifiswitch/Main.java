package com.ikeirnez.autowifiswitch;

import android.app.ActivityManager;
import android.app.Service;
import android.content.*;
import android.os.Bundle;
import android.preference.*;
import android.text.method.DigitsKeyListener;
import com.ikeirnez.autowifiswitch.background.ServiceStarter;
import com.ikeirnez.autowifiswitch.background.WifiScanService;

public class Main extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_DIFFERENCE_REQUIRED = "differenceRequired", KEY_UPDATE_INTERVAL = "updateInterval", KEY_TOAST_NOTIFICATION = "toastNotification";
    public static final int DEFAULT_DIFFERENCE_REQUIRED = 2, DEFAULT_UPDATE_INTERVAL = 10;
    public static final boolean DEFAULT_TOAST_NOTIFICATION = true;
    private static final String[] differenceOptions = new String[10];

    static {
        for (int i = 0; i < 10; i++){
            differenceOptions[i] = String.valueOf(i);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        if (!isServiceRunning(WifiScanService.class)){
            ServiceStarter.startService(this);
        }

        PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(this);

        ListPreference differenceRequiredPreference = new ListPreference(this);
        differenceRequiredPreference.setKey(KEY_DIFFERENCE_REQUIRED);
        differenceRequiredPreference.setTitle("Select required difference");
        differenceRequiredPreference.setSummary("The difference in signal strengths required for the wifi network to be switched");
        differenceRequiredPreference.setEntries(differenceOptions);
        differenceRequiredPreference.setEntryValues(differenceOptions);
        differenceRequiredPreference.setDefaultValue(String.valueOf(DEFAULT_DIFFERENCE_REQUIRED));
        preferenceScreen.addPreference(differenceRequiredPreference);

        EditTextPreference updateIntervalPreference = new EditTextPreference(this);
        updateIntervalPreference.setKey(KEY_UPDATE_INTERVAL);
        updateIntervalPreference.setTitle("Update Interval");
        updateIntervalPreference.setSummary("The time (in seconds) which we should check for new networks to connect to");
        updateIntervalPreference.setDefaultValue(String.valueOf(DEFAULT_UPDATE_INTERVAL));
        updateIntervalPreference.getEditText().setKeyListener(new DigitsKeyListener());
        preferenceScreen.addPreference(updateIntervalPreference);

        CheckBoxPreference toastNotification = new CheckBoxPreference(this);
        toastNotification.setKey(KEY_TOAST_NOTIFICATION);
        toastNotification.setTitle("Toast Notification");
        toastNotification.setSummary("Show a toast notification when this app connects you to a better wifi connection");
        toastNotification.setDefaultValue(true);
        preferenceScreen.addPreference(toastNotification);
        setPreferenceScreen(preferenceScreen);

        CheckBoxPreference powerSaverDisables = new CheckBoxPreference(this);
        powerSaverDisables.setKey("powerSaverDisables");
        powerSaverDisables.setTitle("Power Saver Disables");
        powerSaverDisables.setSummary("Waiting on Android L. Disables automatic wifi joining when in power saver mode");
        powerSaverDisables.setDefaultValue(true);
        powerSaverDisables.setEnabled(false);
        preferenceScreen.addPreference(powerSaverDisables);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_DIFFERENCE_REQUIRED) || key.equals(KEY_UPDATE_INTERVAL)){
            ServiceStarter.startService(this); // restart service
        }
    }

    public boolean isServiceRunning(Class<? extends Service> serviceClass){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
            if (serviceClass.getName().equals(service.service.getClassName())){
                return true;
            }
        }

        return false;
    }

}
