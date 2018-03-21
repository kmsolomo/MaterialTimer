package com.example.admin.materialtimer;

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


/**
 * Created by admin on 2/14/18.
 */

public class SettingsActivity extends Activity implements SettingsFragment.OnThemeChangeListener{

    Toolbar bar;
    FragmentManager fragManager;
    FragmentTransaction fragTransaction;
    SharedPreferences sharedPref;
    String appTheme;
    String themeKey = "pref_theme_value";
    String defaultTheme = "Dark";

    @Override
    protected void onCreate(Bundle onSaveInstanceState){

        super.onCreate(onSaveInstanceState);
        themeCheck();
        setContentView(R.layout.settings);

        fragManager = getFragmentManager();
        fragTransaction = fragManager.beginTransaction();
        fragTransaction.replace(R.id.fragment_container, new SettingsFragment()).commit();

        setupToolbar(appTheme);
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

    @Override
    public void onThemeChange(){
        recreate();
    }

    public void themeCheck(){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        appTheme = sharedPref.getString(themeKey,defaultTheme);

        switch(appTheme){
            case "Light":
                setTheme(R.style.LightAppTheme);
                break;
            case "Dark":
                setTheme(R.style.DarkAppTheme);
                break;
            case "Black":
                setTheme(R.style.BlackAppTheme);
                break;
            default:
                break;
        }
    }

    public void setupToolbar(String appTheme){
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
