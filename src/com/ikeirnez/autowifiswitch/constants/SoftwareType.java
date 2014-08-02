package com.ikeirnez.autowifiswitch.constants;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;

/**
 * Created by iKeirNez on 02/08/2014.
 */
public enum SoftwareType {

    SENSE("com.htc.launcher.Launcher", "user_powersaver_enable"),
    TOUCHWIZ("com.sec.android.app.easylauncher.Launcher", "psm_switch");

    private String uiLauncherName;

    private String powerSaverKey;
    private Uri powerSaverUri;

    private Boolean running;

    private SoftwareType(String uiLauncherName, String powerSaverKey){
        this.uiLauncherName = uiLauncherName;
        this.powerSaverKey = powerSaverKey;
        this.powerSaverUri = Settings.System.CONTENT_URI.buildUpon().appendPath(powerSaverKey).build();
    }

    public String getUiLauncherName() {
        return uiLauncherName;
    }

    public String getPowerSaverKey() {
        return powerSaverKey;
    }

    public Uri getPowerSaverUri() {
        return powerSaverUri;
    }

    public boolean getPowerSaverStatus(Context context){
        return powerSaverKey != null && Settings.System.getString(context.getContentResolver(), getPowerSaverKey()).equals("1");
    }

    public boolean isRunning(Context context){
        if (running == null){
            running = false;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);

            for (ResolveInfo resolveInfo : context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)){
                if (resolveInfo.activityInfo != null && resolveInfo.activityInfo.name.equals(getUiLauncherName())){
                    running = true;
                    break;
                }
            }
        }

        return running;
    }

    private static SoftwareType runningSoftwareType = null;
    private static boolean cachedRunningSoftwareType = false;

    public static SoftwareType getRunningSoftwareType(Context context){
        if (!cachedRunningSoftwareType){
            for (SoftwareType softwareType : values()){
                if (softwareType.isRunning(context)){
                    runningSoftwareType = softwareType;
                    break;
                }
            }

            cachedRunningSoftwareType = true;
        }

        return runningSoftwareType;
    }

}
