package com.example.admin.materialtimer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Created by admin on 3/5/18.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    String themeLight;
    String themeDark;
    String themeBlack;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        themeLight = getString(R.string.LightTheme);
        themeDark = getString(R.string.DarkTheme);
        themeBlack = getString(R.string.BlackTheme);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key){

        String value = sharedPreferences.getString(key,"");

        if(value.equals(themeLight)){

        }
    }
}