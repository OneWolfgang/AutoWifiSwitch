package com.ikeirnez.autowifiswitch.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

/**
 * Created by iKeirNez on 02/08/2014.
 */
public class WifiStateChangeListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1)){
            default: break;
            case WifiManager.WIFI_STATE_ENABLED:
                ServiceStarter.rescheduleService(context);
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                ServiceStarter.cancelService(context);
                break;
        }
    }
}
