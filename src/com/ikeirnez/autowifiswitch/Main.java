package com.ikeirnez.autowifiswitch;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.preference.*;
import com.ikeirnez.autowifiswitch.background.ServiceStarter;
import com.ikeirnez.autowifiswitch.background.WifiScanService;
import com.ikeirnez.autowifiswitch.preferences.ConfigFragment;

public class Main extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (!isServiceRunning(WifiScanService.class)){
            ServiceStarter.startService(this);
        }

        // the below code opens the option fragment
        // this can be removed if we ever add more menus
        getFragmentManager().beginTransaction().replace(android.R.id.content, new ConfigFragment()).commit();
    }

    public boolean isServiceRunning(Class<? extends Service> serviceClass){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
            if (serviceClass.getName().equals(service.service.getClassName())){
                return true;
            }
        }

        return false;
    }


}
