package com.ikeirnez.autowifiswitch.fragments;

import android.app.Fragment;

/**
 * Created by iKeirNez on 19/11/2014.
 */
public class MainMenuItem {

    private String typeName;
    private int resId;
    private Fragment fragment;

    public MainMenuItem(String typeName, int resId, Fragment fragment){
        this.typeName = typeName;
        this.resId = resId;
        this.fragment = fragment;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getResId() {
        return resId;
    }

    public Fragment getFragment() {
        return fragment;
    }
}
