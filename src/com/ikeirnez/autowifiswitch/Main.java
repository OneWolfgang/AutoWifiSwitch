package com.ikeirnez.autowifiswitch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.ikeirnez.autowifiswitch.background.ServiceManager;
import com.ikeirnez.autowifiswitch.fragments.MainMenuFragment;

/**
 * Main activity, responsible for swapping in the MainMenuFragment
 */
public class Main extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        fixEmptyStringPreferences();

        if (!ServiceManager.serviceRunning){ // start scanning service if not already started
            ServiceManager.updateScanningService(this);
        }

        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new MainMenuFragment()).commit();
    }

    // previous versions had a bug whereby some numeric values could be set to "" causing crashes, this corrects that
    // todo remove in future version?
    public void fixEmptyStringPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        for (String preference : new String[]{"update_interval", "update_interval_display_off"}){
            if (sharedPreferences.getString(preference, null).equals("")){
                int resId = getResources().getIdentifier("default_" + preference, "integer", getPackageName()); // work out resource name programmatically
                sharedPreferences.edit().putString(preference, String.valueOf(getResources().getInteger(resId))).apply();

                Log.i("Main", "Fixed preference with empty string \"" + preference + "\", set to default value");
            }
        }
    }

}
