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
import androidx.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.kristoffersol.materialtimer.databinding.SettingsBinding;
import com.kristoffersol.materialtimer.util.ThemeUtil;


public class SettingsActivity extends AppCompatActivity implements SettingsFragment.OnThemeChangeListener{

    private SettingsBinding sBinding;

    @Override
    public void onCreate(Bundle onSaveInstanceState){
        super.onCreate(onSaveInstanceState);
        ThemeUtil.themeCheck(this);
        sBinding = DataBindingUtil.setContentView(this, R.layout.settings);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_fragment_container, SettingsFragment.getInstance())
                .commit();

        setSupportActionBar(sBinding.toolBar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String appTheme = sharedPref.getString(getResources().getString(R.string.THEME_KEY),"Dark");

        if(appTheme.equals("Light")){
            sBinding.toolBar.setTitleTextColor(getResources().getColor(R.color.colorLightPrimary));
            sBinding.toolBar.setBackground(new ColorDrawable(getResources().getColor(R.color.colorAccent)));

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccentDark));

        }
    }
}
