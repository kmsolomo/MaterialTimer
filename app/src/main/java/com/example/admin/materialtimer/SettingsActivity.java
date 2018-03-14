package com.example.admin.materialtimer;

import android.os.Bundle;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Activity;
import android.view.MenuItem;
import android.widget.Toolbar;


/**
 * Created by admin on 2/14/18.
 */

public class SettingsActivity extends Activity {

    Toolbar bar;
    FragmentManager fragManager;
    FragmentTransaction fragTransaction;

    @Override
    protected void onCreate(Bundle onSaveInstanceState){

        super.onCreate(onSaveInstanceState);
        setContentView(R.layout.settings);

        fragManager = getFragmentManager();
        fragTransaction = fragManager.beginTransaction();
        fragTransaction.replace(R.id.fragment_container, new SettingsFragment()).commit();

        bar = findViewById(R.id.toolbar);
        setActionBar(bar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
}
