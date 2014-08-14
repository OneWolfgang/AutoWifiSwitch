package com.ikeirnez.autowifiswitch.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.ikeirnez.autowifiswitch.background.ServiceManager;

/**
 * Created by iKeirNez on 02/08/2014.
 */
public class ScreenWifiListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Screen Listener", "Detected screen mode change, updating service accordingly");
        ServiceManager.updateScanningService(context);
    }
}
