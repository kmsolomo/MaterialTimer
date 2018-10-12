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

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.kristoffersol.materialtimer.util.NotificationHelper;
import com.kristoffersol.materialtimer.util.ThemeUtil;

public abstract class BaseActivity extends AppCompatActivity{

    protected final int THEME_REQUEST_CODE = 1;


    protected void addFragmentToActivity(FragmentManager fragManager,
                                      int fragContainer,
                                      Fragment fragment){
        fragManager.beginTransaction()
                .add(fragContainer,fragment)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ThemeUtil.themeCheck(this);
        getLifecycle().addObserver(new NotificationHelper(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == THEME_REQUEST_CODE){
            recreate();
        }
    }
}
