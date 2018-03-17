package com.example.admin.materialtimer;

import android.content.Context;
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
    OnThemeChangeListener themeChangeListener;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        themeLight = getString(R.string.LightTheme);
        themeDark = getString(R.string.DarkTheme);
        themeBlack = getString(R.string.BlackTheme);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            themeChangeListener = (OnThemeChangeListener) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(e.toString() + "implement OnThemeChangeListener");
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key){

        String value = sharedPreferences.getString(key,"");

        if(value.equals(themeLight) || value.equals(themeDark) || value.equals(themeBlack)){
            themeChangeListener.onThemeChange();
        }
    }

    public interface OnThemeChangeListener{
        void onThemeChange();
    }
}