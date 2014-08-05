package com.ikeirnez.autowifiswitch.listeners;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import com.ikeirnez.autowifiswitch.background.ServiceManager;
import com.ikeirnez.autowifiswitch.enums.SoftwareType;

/**
 * Created by iKeirNez on 02/08/2014.
 */
public class PowerSaverListener extends ContentObserver {

    private Context context;

    public PowerSaverListener(Context context) {
        super(null);
        this.context = context;
    }

    @SuppressLint("NewApi") // I know this will be fine, trust me
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        SoftwareType softwareType = SoftwareType.getRunningSoftwareType(context);

        if (softwareType != null && uri.equals(softwareType.getPowerSaverUri())){
            if (softwareType.getPowerSaverStatus(context)){
                ServiceManager.cancelService(context);
            } else {
                ServiceManager.startService(context);
            }
        }
    }
}
