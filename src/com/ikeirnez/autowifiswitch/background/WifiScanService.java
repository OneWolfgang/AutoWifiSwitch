package com.ikeirnez.autowifiswitch.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;

/**
 * Created by iKeirNez on 26/07/2014.
 */
public class WifiScanService extends Service {

    private SharedPreferences preferences;
    private WifiManager wifiManager;

    private WifiScanResultsListener wifiScanResultsListener;
    private WifiStateChangeListener wifiStateChangeListener;

    private PowerManager powerManager;
    private boolean lastScreenOnState = true;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        registerReceiver(wifiScanResultsListener = new WifiScanResultsListener(wifiManager, preferences), new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(wifiStateChangeListener = new WifiStateChangeListener(), new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(wifiScanResultsListener);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        wifiManager.startScan();

        boolean screenStatus = powerManager.isScreenOn();
        if (screenStatus != lastScreenOnState){
            ServiceStarter.rescheduleService(getBaseContext(), screenStatus);
            lastScreenOnState = screenStatus;
        }

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
