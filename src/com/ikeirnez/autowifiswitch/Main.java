package com.ikeirnez.autowifiswitch;

import android.os.Bundle;
import android.preference.*;
import com.ikeirnez.autowifiswitch.preferences.ConfigFragment;

public class Main extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // the below code opens the option fragment
        // this can be removed if we ever add more menus
        getFragmentManager().beginTransaction().replace(android.R.id.content, new ConfigFragment()).commit();
    }

}
