package com.ikeirnez.autowifiswitch.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.ikeirnez.autowifiswitch.background.ServiceManager;

/**
 * Listens for events which might affect scanning service, such as screen, wifi or power saver status
 */
public class EventListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Event Listener", "Detected change, refreshing service accordingly");
        ServiceManager.updateScanningService(context);
    }
}
