package com.ikeirnez.autowifiswitch.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.ikeirnez.autowifiswitch.enums.NotificationType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by iKeirNez on 25/07/2014.
 */
public class WifiScanResultsListener extends BroadcastReceiver {

    private WifiManager wifiManager;
    private SharedPreferences preferences;

    public WifiScanResultsListener(WifiManager wifiManager, SharedPreferences preferences){
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

            if (best != null){
                if (current != null){
                    String currentSSID = current.getSSID();
                    currentSSID = currentSSID.length() >= 2 ? currentSSID.substring(1, currentSSID.length() - 1) : currentSSID; // remove "s

                    // don't connect to a network we're already connected to
                    // only connect if the new network's strength meets the difference requirement
                    if (!best.SSID.equals(currentSSID) && WifiManager.compareSignalLevel(best.level, current.getRssi()) >= differenceRequirement){
                        wifiManager.enableNetwork(allowedAttemptConnect.get(best.SSID).networkId, true);
                        NotificationType.valueOf(preferences.getString("notification_type", NotificationType.TOAST.name())).doNotification(context, best.SSID);
                    }
                }
            }
        }
    }
}
