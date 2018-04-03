package com.example.admin.materialtimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by admin on 4/3/18.
 */

public class ThemeUtility {

    private static final String themeKey = "pref_theme_value";

    public static void themeCheck(Context activity){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        String appTheme = sharedPref.getString(themeKey,"Dark");

        switch(appTheme){
            case "Light":
                activity.setTheme(R.style.LightAppTheme);
                break;
            case "Dark":
                activity.setTheme(R.style.DarkAppTheme);
                break;
            case "Black":
                activity.setTheme(R.style.BlackAppTheme);
                break;
            default:
                break;
        }
    }
}
