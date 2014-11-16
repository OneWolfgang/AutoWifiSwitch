import android.test.ActivityTestCase;
import com.ikeirnez.autowifiswitch.R;
import com.ikeirnez.autowifiswitch.enums.NotificationType;

/**
 * Created by iKeirNez on 14/11/2014.
 */
public class NotificationTypeTest extends ActivityTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testNotificationType() throws Exception {
        String defaultNotificationType = getActivity().getResources().getString(R.string.default_notification_type);
        assertNotNull(NotificationType.valueOf(defaultNotificationType));
    }
}
