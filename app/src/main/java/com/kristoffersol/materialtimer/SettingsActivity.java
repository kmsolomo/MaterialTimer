/*
 * Copyright 2018 Kristoffer Solomon
 *
 * This file is part of MaterialTimer.
 *
 * MaterialTimer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialTimer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MaterialTimer.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kristoffersol.materialtimer;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toolbar;

import com.kristoffersol.materialtimer.util.ThemeUtil;

public class SettingsActivity extends Activity implements SettingsFragment.OnThemeChangeListener{

    private Toolbar bar;
    private FragmentManager fragManager;
    private FragmentTransaction fragTransaction;
    private SharedPreferences sharedPref;
    private String themeKey = "pref_theme_value";
    private String defaultTheme = "Dark";

    @Override
    protected void onCreate(Bundle onSaveInstanceState){

        super.onCreate(onSaveInstanceState);
        ThemeUtil.themeCheck(this);
        setContentView(R.layout.settings);

        fragManager = getFragmentManager();
        fragTransaction = fragManager.beginTransaction();
        fragTransaction.replace(R.id.fragment_container, new SettingsFragment()).commit();

        updateUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Triggered on list preference change in SettingsFragment
     */
    @Override
    public void onThemeChange(){
        recreate();
    }

    /**
     * Handles UI of toolbar and status bar during theme change
     */
    private void updateUI(){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String appTheme = sharedPref.getString(themeKey,defaultTheme);

        if(appTheme.equals("Light")){
            bar = findViewById(R.id.toolbar);
            bar.setTitleTextColor(getResources().getColor(R.color.colorLightPrimary));
            bar.setBackground(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
            setActionBar(bar);
            getActionBar().setDisplayHomeAsUpEnabled(true);

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccentDark));
        } else {
            bar = findViewById(R.id.toolbar);
            setActionBar(bar);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
