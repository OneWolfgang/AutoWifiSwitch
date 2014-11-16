package com.ikeirnez.autowifiswitch.tests;

import android.test.ActivityInstrumentationTestCase2;
import com.ikeirnez.autowifiswitch.Main;
import com.ikeirnez.autowifiswitch.R;
import com.ikeirnez.autowifiswitch.enums.NotificationType;

/**
 * Created by iKeirNez on 14/11/2014.
 */
public class NotificationTypeTest extends ActivityInstrumentationTestCase2<Main> {

    public NotificationTypeTest() {
        super(Main.class);
    }

    // Check's that the default notification type (defined manually in an xml file) actually exists
    public void testNotificationType() throws Exception {
        String defaultNotificationType = getActivity().getResources().getString(R.string.default_notification_type);
        assertNotNull(NotificationType.valueOf(defaultNotificationType)); // this will never actually be null but rather throw an exception, either way we are alerted to an issue
    }
}
