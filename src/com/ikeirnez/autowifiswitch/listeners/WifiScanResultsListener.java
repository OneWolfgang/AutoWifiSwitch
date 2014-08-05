package com.ikeirnez.autowifiswitch.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.ikeirnez.autowifiswitch.background.WifiService;
import com.ikeirnez.autowifiswitch.constants.NotificationType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by iKeirNez on 25/07/2014.
 */
public class WifiScanResultsListener extends BroadcastReceiver {

    private WifiService wifiService;
    private WifiManager wifiManager;
    private SharedPreferences preferences;

    public WifiScanResultsListener(WifiService wifiService, WifiManager wifiManager, SharedPreferences preferences){
        this.wifiService = wifiService;
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
            int differenceRequirement = Integer.parseInt(preferences.getString("differenceRequired", "2")) * 10; // todo vv ugly

            for (WifiConfiguration wifiConfiguration : configuredNetworks){ // cache connections we can attempt to connect to
                allowedAttemptConnect.put(wifiConfiguration.SSID.substring(1, wifiConfiguration.SSID.length() - 1), wifiConfiguration);
            }

            for (ScanResult scanResult : wifiManager.getScanResults()){ // loop through scan results
                // ignore if we aren't allowed to attempt to connect, don't compare if we have nothing to compare to, only set as best if the difference in signal is bigger or equal to the configured value
                if (allowedAttemptConnect.containsKey(scanResult.SSID) && (best == null || (current != null && WifiManager.compareSignalLevel(scanResult.level, best.level) > 0))){
                    best = scanResult;
                }
            }

            // best was found, will continue if not connected or if we aren't attempting to connect to a network we're already connected to and the difference is bigger or equal to the requirements
            if (best != null && (current == null || (!current.getSSID().substring(1, current.getSSID().length() - 1).equals(best.SSID) && WifiManager.compareSignalLevel(best.level, current.getRssi()) >= differenceRequirement))){ // attempt to connect if we have something to connect to, and don't attempt to connect to a network we're already connected to
                wifiManager.enableNetwork(allowedAttemptConnect.get(best.SSID).networkId, true);
                NotificationType.valueOf(preferences.getString("notification_type", NotificationType.TOAST.name())).doNotification(context, best.SSID);
            }
        }
    }
}
