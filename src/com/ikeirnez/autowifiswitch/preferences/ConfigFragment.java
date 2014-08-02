package com.ikeirnez.autowifiswitch.preferences;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import com.ikeirnez.autowifiswitch.NotificationType;
import com.ikeirnez.autowifiswitch.R;
import com.ikeirnez.autowifiswitch.background.ServiceStarter;

/**
 * Created by iKeirNez on 27/07/2014.
 */
public class ConfigFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private static final String DONATE_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=BJQTQKAPZT6VU";

    // cached preferences stuff
    private static final String[] DIFFERENCE_ENTRIES = new String[10];
    private static final int NOTIFICATION_TYPE_AMOUNT = NotificationType.values().length;
    private static final String[] NOTIFICATION_ENTRIES = new String[NOTIFICATION_TYPE_AMOUNT], NOTIFICATION_ENTRY_VALUES = new String[NOTIFICATION_TYPE_AMOUNT];

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
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);

        ListPreference differenceRequired = (ListPreference) findPreference("difference_required");
        differenceRequired.setEntries(DIFFERENCE_ENTRIES);
        differenceRequired.setEntryValues(DIFFERENCE_ENTRIES);
        differenceRequired.setPersistent(true);

        ListPreference notificationType = (ListPreference) findPreference("notification_type");
        notificationType.setEntries(NOTIFICATION_ENTRIES);
        notificationType.setEntryValues(NOTIFICATION_ENTRY_VALUES);
        notificationType.setPersistent(true);

        Preference donateButton = findPreference("donate_button");
        donateButton.setOnPreferenceClickListener(this);

        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("enabled") || key.equals("difference_required") || key.equals("update_interval")){
            ServiceStarter.rescheduleService(getActivity()); // restart service
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("donate_button")){
            new AlertDialog.Builder(getActivity())
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
