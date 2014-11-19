package com.ikeirnez.autowifiswitch;

import android.app.Activity;
import android.os.Bundle;
import com.ikeirnez.autowifiswitch.fragments.MainMenuFragment;

/**
 * Main activity, responsible for swapping in the MainMenuFragment
 */
public class Main extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainMenuFragment()).commit();
    }

}
