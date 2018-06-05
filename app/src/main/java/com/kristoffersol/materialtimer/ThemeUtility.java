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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ThemeUtility {

    private static final String themeKey = "pref_theme_value";

    public static void themeCheck(Context activity){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        String appTheme = sharedPref.getString(themeKey,"Dark");

        switch(appTheme){
            case "Light":
                activity.setTheme(R.style.LightAppTheme);
                break;
            case "Dark":
                activity.setTheme(R.style.DarkAppTheme);
                break;
            case "Black":
                activity.setTheme(R.style.BlackAppTheme);
                break;
            default:
                break;
        }
    }
}
