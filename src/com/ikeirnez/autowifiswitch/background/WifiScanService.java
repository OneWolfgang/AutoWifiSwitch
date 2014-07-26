package com.ikeirnez.autowifiswitch.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by iKeirNez on 26/07/2014.
 */
public class WifiScanService extends Service {

    private SharedPreferences preferences;
    private WifiManager wifiManager;

    private WifiScanResultsListener wifiScanResultsListener;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        registerReceiver(wifiScanResultsListener = new WifiScanResultsListener(this, wifiManager, preferences), new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(wifiScanResultsListener);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (wifiManager.isWifiEnabled()){
            wifiManager.startScan();
        }

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
