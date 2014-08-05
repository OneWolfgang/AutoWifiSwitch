package com.ikeirnez.autowifiswitch.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import com.ikeirnez.autowifiswitch.background.ServiceManager;

/**
 * Created by iKeirNez on 02/08/2014.
 */
public class ScreenWifiListener extends BroadcastReceiver {

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
            case WifiManager.WIFI_STATE_CHANGED_ACTION:
                switch (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1)){
                    default: break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        ServiceManager.startService(context);
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        ServiceManager.cancelService(context);
                        break;
                }
        }
    }
}
