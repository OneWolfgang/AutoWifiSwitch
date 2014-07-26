package com.ikeirnez.autowifiswitch;

import android.app.AlertDialog;
import android.content.*;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.*;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Main extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    public static final String KEY_DIFFERENCE_REQUIRED = "differenceRequired", KEY_UPDATE_INTERVAL = "updateInterval", KEY_TOAST_NOTIFICATION = "toastNotification";
    public static final int DEFAULT_DIFFERENCE_REQUIRED = 2, DEFAULT_UPDATE_INTERVAL = 10;
    public static final boolean DEFAULT_TOAST_NOTIFICATION = true;
    private static final String[] differenceOptions = new String[11];

    static {
        for (int i = 0; i <= 10; i++){
            differenceOptions[i] = String.valueOf(i);
        }
    }

    private SharedPreferences preferences;

    private WifiManager wifiManager;

    private ScheduledExecutorService executorService;
    private ScheduledFuture wifiTask;

    @Override
    protected void onPause() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        executorService = Executors.newSingleThreadScheduledExecutor();

        PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(this);
        preferenceScreen.setOnPreferenceChangeListener(this);

        ListPreference differenceRequiredPreference = new ListPreference(this);
        differenceRequiredPreference.setKey(KEY_DIFFERENCE_REQUIRED);
        differenceRequiredPreference.setTitle("Select required difference");
        differenceRequiredPreference.setSummary("The difference in signal strengths required for the wifi network to be switched");
        differenceRequiredPreference.setEntries(differenceOptions);
        differenceRequiredPreference.setEntryValues(differenceOptions);
        differenceRequiredPreference.setDefaultValue(String.valueOf(DEFAULT_DIFFERENCE_REQUIRED));
        differenceRequiredPreference.setOnPreferenceChangeListener(this);
        preferenceScreen.addPreference(differenceRequiredPreference);

        EditTextPreference updateIntervalPreference = new EditTextPreference(this);
        updateIntervalPreference.setKey(KEY_UPDATE_INTERVAL);
        updateIntervalPreference.setTitle("Update Interval");
        updateIntervalPreference.setSummary("The time (in seconds) which we should check for new networks to connect to");
        updateIntervalPreference.setDefaultValue(String.valueOf(DEFAULT_UPDATE_INTERVAL));
        preferenceScreen.addPreference(updateIntervalPreference);

        CheckBoxPreference toastNotification = new CheckBoxPreference(this);
        toastNotification.setKey(KEY_TOAST_NOTIFICATION);
        toastNotification.setTitle("Toast Notification");
        toastNotification.setSummary("Show a toast notification when this app connects you to a better wifi connection");
        toastNotification.setDefaultValue(true);
        toastNotification.setOnPreferenceChangeListener(this);
        preferenceScreen.addPreference(toastNotification);

        setPreferenceScreen(preferenceScreen);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        registerReceiver(new WifiScanResultsListener(this, wifiManager, preferences), new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(new WifiStateListener(this, wifiManager), new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String prefKey = preference.getKey();

        if (prefKey.equals(KEY_DIFFERENCE_REQUIRED)){
            startWifiTask();
        } else if (prefKey.equals(KEY_UPDATE_INTERVAL)){
            String stringValue = (String) value;

            try {
                Integer.parseInt(stringValue);
            } catch (NumberFormatException e){
                new AlertDialog.Builder(this).setTitle("Not number").setMessage("The input \"" + stringValue + "\" is not a number").setCancelable(false).setPositiveButton("Dismiss", null).create().show();
                return false;
            }

            startWifiTask();
        }

        return true;
    }

    public void startWifiTask(){
        cancelWifiTask();

        if (wifiManager.isWifiEnabled()){
            int period;

            try {
                period = Integer.parseInt(preferences.getString(KEY_UPDATE_INTERVAL, String.valueOf(DEFAULT_UPDATE_INTERVAL))); // todo vv ugly
            } catch (NumberFormatException e){
                Toast.makeText(this, "Unexpected update interval value, resetting to default", Toast.LENGTH_LONG).show();
                period = DEFAULT_UPDATE_INTERVAL;
            }

            wifiTask = executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    wifiManager.startScan();
                }
            }, period, period, TimeUnit.SECONDS);
        }
    }

    public void cancelWifiTask(){
        if (wifiTask != null){
            wifiTask.cancel(true);
            wifiTask = null;
        }
    }
}
