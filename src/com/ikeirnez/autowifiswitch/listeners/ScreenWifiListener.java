package com.ikeirnez.autowifiswitch.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import com.ikeirnez.autowifiswitch.background.ServiceManager;
import com.ikeirnez.autowifiswitch.background.WifiService;

/**
 * Created by iKeirNez on 02/08/2014.
 */
public class ScreenWifiListener extends BroadcastReceiver {

    private WifiService wifiService;

    public ScreenWifiListener(WifiService wifiService){
        this.wifiService = wifiService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            default: break;
            case Intent.ACTION_SCREEN_ON:
                ServiceManager.startService(context, true);
                break;
            case Intent.ACTION_SCREEN_OFF:
                ServiceManager.startService(context, false);
                break;
            case WifiManager.NETWORK_STATE_CHANGED_ACTION: // wifi scanning only needs to be active when we are connected to a wifi network
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                if (networkInfo != null){
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnectedOrConnecting()){ // have wifi connection
                        ServiceManager.startIfNotActive(context);
                        wifiService.switchingNetwork = false;
                    } else if (!wifiService.switchingNetwork) { // not connected to wifi
                        ServiceManager.cancelIfRunning(context);
                    }
                }
        }
    }
}
