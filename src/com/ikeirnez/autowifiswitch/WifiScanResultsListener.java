package com.ikeirnez.autowifiswitch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by iKeirNez on 25/07/2014.
 */
public class WifiScanResultsListener extends BroadcastReceiver {

    private Main main;
    private WifiManager wifiManager;
    private SharedPreferences preferences;

    public WifiScanResultsListener(Main main, WifiManager wifiManager, SharedPreferences preferences){
        this.main = main;
        this.wifiManager = wifiManager;
        this.preferences = preferences;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();

        if (configuredNetworks != null){
            Map<String, WifiConfiguration> allowedAttemptConnect = new HashMap<>();
            WifiInfo current = wifiManager.getConnectionInfo();
            ScanResult best = null;
            int differenceRequirement = Integer.parseInt(preferences.getString(Main.KEY_DIFFERENCE_REQUIRED, String.valueOf(Main.DEFAULT_DIFFERENCE_REQUIRED))); // todo vv ugly

            for (WifiConfiguration wifiConfiguration : configuredNetworks){ // cache connections we can attempt to connect to
                allowedAttemptConnect.put(wifiConfiguration.SSID, wifiConfiguration);
            }

            for (ScanResult scanResult : wifiManager.getScanResults()){ // loop through scan results
                // ignore if we aren't allowed to attempt to connect, don't compare if we have nothing to compare to, only set as best if the difference in signal is bigger or equal to the configured value
                if (allowedAttemptConnect.containsKey(scanResult.SSID) && (best == null || (scanResult.level > best.level && WifiManager.compareSignalLevel(current.getRssi(), best.level) >= differenceRequirement))){
                    best = scanResult;
                }
            }

            if (best != null && (current == null || (!current.getSSID().equals(best.SSID)))){ // attempt to connect if we have something to connect to, and don't attempt to connect to a network we're already connected to
                wifiManager.addNetwork(allowedAttemptConnect.get(best.SSID));

                if (preferences.getBoolean(Main.KEY_TOAST_NOTIFICATION, Main.DEFAULT_TOAST_NOTIFICATION)){
                    Toast.makeText(main, "AutoWiFiSwitch: Connected to " + best.SSID, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
