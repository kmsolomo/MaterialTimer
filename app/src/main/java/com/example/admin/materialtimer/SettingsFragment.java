package com.example.admin.materialtimer;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by admin on 3/5/18.
 */

public class SettingsFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}