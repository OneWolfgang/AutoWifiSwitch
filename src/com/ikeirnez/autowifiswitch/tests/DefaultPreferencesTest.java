package com.ikeirnez.autowifiswitch.tests;

import android.test.ActivityInstrumentationTestCase2;
import com.ikeirnez.autowifiswitch.Main;
import com.ikeirnez.autowifiswitch.R;
import com.ikeirnez.autowifiswitch.enums.NotificationType;
import com.ikeirnez.autowifiswitch.preferences.ConfigFragment;

import java.util.Arrays;

/**
 * Checks various hard-coded default settings and checks they are valid
 */
public class DefaultPreferencesTest extends ActivityInstrumentationTestCase2<Main> {

    public DefaultPreferencesTest() {
        super(Main.class);
    }

    // Checks that the default notification type (defined manually in an xml file) actually exists
    public void testNotificationType() throws Exception {
        String defaultNotificationType = getActivity().getResources().getString(R.string.default_notification_type);
        assertNotNull(NotificationType.valueOf(defaultNotificationType)); // this will never actually be null but rather throw an exception, either way we are alerted to an issue
    }

    // Check the difference required integer is actually contained in the DIFFERENCE_REQUIRED array
    public void testDifferenceRequired() throws Exception {
        int defaultDifferenceRequired = getActivity().getResources().getInteger(R.integer.default_difference_required);
        ConfigFragment configFragment = (ConfigFragment) getActivity().getFragmentManager().findFragmentById(android.R.id.content);
        assertTrue("DIFFERENCE_ENTRIES does not contain the default value \"" + defaultDifferenceRequired + "\"", Arrays.asList(configFragment.DIFFERENCE_ENTRIES).contains(String.valueOf(defaultDifferenceRequired)));
    }
}
