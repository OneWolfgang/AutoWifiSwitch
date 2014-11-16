package com.ikeirnez.autowifiswitch.listeners;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import com.ikeirnez.autowifiswitch.background.ServiceManager;
import com.ikeirnez.autowifiswitch.enums.SoftwareType;

/**
 * Created by iKeirNez on 02/08/2014.
 */
public class LegacyPowerSaverListener extends ContentObserver {

    private Context context;

    public LegacyPowerSaverListener(Context context) {
        super(null);
        this.context = context;
    }

    @SuppressLint("NewApi") // I know this will be fine, trust me
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("power_saver_disables", false)){ // only run if power saver setting active
            SoftwareType softwareType = SoftwareType.getRunningSoftwareType(context);
            if (softwareType != null && uri.equals(softwareType.getPowerSaverUri())){
                Log.i("Power Saver", "Detected power saver change, updating service accordingly");
                ServiceManager.updateScanningService(context);
            }
        }
    }
}
