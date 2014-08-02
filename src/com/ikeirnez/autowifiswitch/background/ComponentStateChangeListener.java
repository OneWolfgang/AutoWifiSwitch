package com.ikeirnez.autowifiswitch.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

/**
 * Created by iKeirNez on 02/08/2014.
 */
public class ComponentStateChangeListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            default: break;
            case Intent.ACTION_SCREEN_ON:
                ServiceStarter.rescheduleService(context, true);
                break;
            case Intent.ACTION_SCREEN_OFF:
                ServiceStarter.rescheduleService(context, false);
                break;
            case WifiManager.WIFI_STATE_CHANGED_ACTION:
                switch (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1)){
                    default: break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        ServiceStarter.rescheduleService(context);
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        ServiceStarter.cancelService(context);
                        break;
                }

                break;
        }
    }
}
