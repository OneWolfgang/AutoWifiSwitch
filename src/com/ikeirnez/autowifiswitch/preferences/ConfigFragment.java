package com.ikeirnez.autowifiswitch.preferences;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.*;
import com.ikeirnez.autowifiswitch.background.ServiceManager;
import com.ikeirnez.autowifiswitch.enums.NotificationType;
import com.ikeirnez.autowifiswitch.R;
import com.ikeirnez.autowifiswitch.enums.SoftwareType;

/**
 * Created by iKeirNez on 27/07/2014.
 */
public class ConfigFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    // preferences stuff
    private final String[] DIFFERENCE_ENTRIES = new String[10];
    private final int NOTIFICATION_TYPE_AMOUNT = NotificationType.values().length;
    private final String[] NOTIFICATION_ENTRIES = new String[NOTIFICATION_TYPE_AMOUNT], NOTIFICATION_ENTRY_VALUES = new String[NOTIFICATION_TYPE_AMOUNT];

    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for (int i = 0; i < 10; i++){
            DIFFERENCE_ENTRIES[i] = String.valueOf(i);
        }

        for (int i = 0; i < NOTIFICATION_TYPE_AMOUNT; i++){
            NotificationType notType = NotificationType.values()[i];
            NOTIFICATION_ENTRIES[i] = notType.getFriendlyName(getActivity());
            NOTIFICATION_ENTRY_VALUES[i] = notType.name();
        }

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

        CheckBoxPreference powerSaverDisables = (CheckBoxPreference) findPreference("power_saver_disables");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
            powerSaverDisables.setChecked(false);
            powerSaverDisables.setEnabled(false);
            powerSaverDisables.setSummary(R.string.power_saver_android_version);
        } else if (SoftwareType.getRunningSoftwareType(getActivity()) == null){
            powerSaverDisables.setChecked(false);
            powerSaverDisables.setEnabled(false);
            powerSaverDisables.setSummary(R.string.power_saver_not_supported);
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("enabled") || key.equals("update_interval") || key.equals("power_saver_disables")){
            if (isAdded()){ // avoid NPE on getActivity
                ServiceManager.updateScanningService(getActivity()); // restart service
            }
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("donate_button")){
            new AlertDialog.Builder(getActivity())
                    .setTitle("Donations Unavailable")
                    .setMessage("PayPal have terminated my account for now, will update this when my account is active again")
                    .create().show();
            /*new AlertDialog.Builder(getActivity()) todo
                    .setTitle(R.string.donate_popup_header)
                    .setMessage(R.string.donate_popup_text)
                    .setCancelable(true).setNegativeButton(R.string.donate_popup_negative, null)
                    .setPositiveButton(R.string.donate_popup_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.donate_popup_url))));
                        }
                    }).create().show();*/
        }

        return true;
    }
}
