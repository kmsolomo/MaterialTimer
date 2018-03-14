package com.example.admin.materialtimer;

import android.app.Activity
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toolbar;


/**
 * Created by admin on 2/14/18.
 */

public class SettingsActivity extends Activity {

    Toolbar bar;

    @Override
    protected void onCreate(Bundle onSaveInstanceState){

        super.onCreate(onSaveInstanceState);

        FragmentManager fragManager = getFragmentManager();
        FragmentTransaction fragTransaction = fragManager.beginTransaction();
        fragTransaction.replace(android.R.id.content,new SettingsFragment()).commit();

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
