package com.ikeirnez.autowifiswitch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

/**
 * Created by iKeirNez on 25/07/2014.
 */
public class WifiStateListener extends BroadcastReceiver {

    private Main main;
    private WifiManager wifiManager;

    public WifiStateListener(Main main, WifiManager wifiManager){
        this.main = main;
        this.wifiManager = wifiManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (wifiManager.isWifiEnabled()){
            main.startWifiTask();
        } else {
            main.cancelWifiTask();
        }
    }
}
