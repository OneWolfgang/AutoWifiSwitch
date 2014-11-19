package com.ikeirnez.autowifiswitch;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.ikeirnez.autowifiswitch.fragments.MainMenuFragment;

/**
 * Main activity, responsible for swapping in the MainMenuFragment
 */
public class Main extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new MainMenuFragment()).commit();
    }

}
