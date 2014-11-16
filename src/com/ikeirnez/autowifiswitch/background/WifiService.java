package com.ikeirnez.autowifiswitch.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import com.ikeirnez.autowifiswitch.enums.SoftwareType;
import com.ikeirnez.autowifiswitch.listeners.EventListener;
import com.ikeirnez.autowifiswitch.listeners.LegacyPowerSaverListener;
import com.ikeirnez.autowifiswitch.listeners.WifiScanResultsListener;

/**
 * Created by iKeirNez on 26/07/2014.
 */
public class WifiService extends Service {

    private SharedPreferences preferences;
    private WifiManager wifiManager;
    private ConnectivityManager connectivityManager;

    private WifiScanResultsListener wifiScanResultsListener;
    private EventListener eventListener;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        registerReceiver(wifiScanResultsListener = new WifiScanResultsListener(wifiManager, preferences), new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        IntentFilter intentFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){ // power saver api only available in Lollipop and above
            intentFilter.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);
        } else { // if below version Lollipop, use legacy power saver detection
            SoftwareType softwareType = SoftwareType.getRunningSoftwareType(this);
            if (softwareType != null) {
                getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, new LegacyPowerSaverListener(this));
            }
        }

        registerReceiver(eventListener = new EventListener(), intentFilter);
        ServiceManager.serviceRunning = true;
    }

    @Override
    public void onDestroy() {
        ServiceManager.serviceRunning = false;
        unregisterReceiver(wifiScanResultsListener);
        unregisterReceiver(eventListener);

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
