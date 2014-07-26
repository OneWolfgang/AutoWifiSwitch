package com.ikeirnez.autowifiswitch;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Service;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.preference.*;
import com.ikeirnez.autowifiswitch.background.ServiceStarter;
import com.ikeirnez.autowifiswitch.background.WifiScanService;

public class Main extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String DONATE_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=BJQTQKAPZT6VU";

    // cached preferences stuff
    private static final String[] DIFFERENCE_ENTRIES = new String[10];
    private static final int NOTIFICATION_TYPE_AMOUNT = NotificationType.values().length;
    private static final String[] NOTIFICATION_ENTRIES = new String[NOTIFICATION_TYPE_AMOUNT], NOTIFICATION_ENTRY_VALUES = new String[NOTIFICATION_TYPE_AMOUNT];;

    static {
        for (int i = 0; i < 10; i++){
            DIFFERENCE_ENTRIES[i] = String.valueOf(i);
        }

        for (int i = 0; i < NOTIFICATION_TYPE_AMOUNT; i++){
            NotificationType notType = NotificationType.values()[i];
            NOTIFICATION_ENTRIES[i] = notType.getFriendlyName();
            NOTIFICATION_ENTRY_VALUES[i] = notType.name();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        if (!isServiceRunning(WifiScanService.class)){
            ServiceStarter.startService(this);
        }

        addPreferencesFromResource(R.xml.preferences);

        ListPreference differenceRequired = (ListPreference) findPreference("difference_required");
        differenceRequired.setEntries(DIFFERENCE_ENTRIES);
        differenceRequired.setEntryValues(DIFFERENCE_ENTRIES);
        differenceRequired.setDefaultValue("2");
        differenceRequired.setPersistent(true);

        ListPreference notificationType = (ListPreference) findPreference("notification_type");
        notificationType.setEntries(NOTIFICATION_ENTRIES);
        notificationType.setEntryValues(NOTIFICATION_ENTRY_VALUES);
        notificationType.setDefaultValue(NotificationType.TOAST.name());
        notificationType.setPersistent(true);

        Preference donateButton = findPreference("donate_button");
        donateButton.setOnPreferenceClickListener(this);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("enabled") || key.equals("difference_required") || key.equals("update_interval")){
            ServiceStarter.startService(this); // restart service
        }
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

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("donate_button")){
            new AlertDialog.Builder(this)
                    .setTitle("Thanks!")
                    .setMessage("Thanks for considering to make a donation, all donations are GREATLY appreciated and go a long way. Donations will help support the development of this app and new apps to come. :)")
                    .setCancelable(true).setNegativeButton("I don't want to donate", null)
                    .setPositiveButton("Continue to PayPal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(DONATE_URL)));
                }
            }).create().show();
        }

        return true;
    }
}
