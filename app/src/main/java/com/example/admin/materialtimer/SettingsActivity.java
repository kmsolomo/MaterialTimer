package com.example.admin.materialtimer;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Created by admin on 2/14/18.
 */

public class SettingsActivity extends PreferenceActivity{

    @Override
    protected void onCreate(Bundle onSaveInstanceState){
        super.onCreate(onSaveInstanceState);



    }

    public static class SettingsFragment extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
