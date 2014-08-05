package com.ikeirnez.autowifiswitch.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import com.ikeirnez.autowifiswitch.listeners.ScreenWifiListener;
import com.ikeirnez.autowifiswitch.listeners.WifiScanResultsListener;

/**
 * Created by iKeirNez on 26/07/2014.
 */
public class WifiService extends Service {

    private SharedPreferences preferences;
    private WifiManager wifiManager;
    private ConnectivityManager connectivityManager;

    private WifiScanResultsListener wifiScanResultsListener;
    private ScreenWifiListener screenWifiListener;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        registerReceiver(wifiScanResultsListener = new WifiScanResultsListener(this, wifiManager, preferences), new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        IntentFilter intentFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenWifiListener = new ScreenWifiListener(), intentFilter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(wifiScanResultsListener);
        unregisterReceiver(screenWifiListener);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected()){
            wifiManager.startScan();
        }

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
