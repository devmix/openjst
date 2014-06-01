/*
 * Copyright (C) 2013 OpenJST Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openjst.client.android.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import org.openjst.client.android.Constants;
import org.openjst.client.android.R;
import org.openjst.client.android.activity.generic.AbstractPreferenceActivity;
import org.openjst.client.android.commons.inject.annotations.Inject;
import org.openjst.client.android.commons.inject.annotations.android.APreferences;
import org.openjst.client.android.commons.managers.LocaleManager;

/**
 * @author Sergey Grachev
 */
@APreferences(R.xml.settings)
public final class SettingsActivity extends AbstractPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    private LocaleManager localeManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        fillSummary(getPreferenceScreen());
    }

    private void fillSummary(final Preference preference) {
        if (PreferenceGroup.class.isAssignableFrom(preference.getClass())) {
            final PreferenceGroup group = (PreferenceGroup) preference;
            for (int i = 0; i < group.getPreferenceCount(); i++) {
                fillSummary(group.getPreference(i));
            }
        } else {
            if (preference instanceof ListPreference) {
                final ListPreference listPreference = (ListPreference) preference;
                if (Constants.Settings.LOCALE_CODE.equals(listPreference.getKey())) {
                    listPreference.setEntries(listOfAvailableLocales());
                }
                listPreference.setSummary(listPreference.getEntry());
            } else if (preference instanceof EditTextPreference) {
                final EditTextPreference editTextPreference = (EditTextPreference) preference;
                editTextPreference.setSummary(editTextPreference.getText());
            }
        }
    }

    private String[] listOfAvailableLocales() {
        final String[] codes = getResources().getStringArray(R.array.array_country_codes);
        final String[] result = new String[codes.length];
        for (int i = 0; i < codes.length; i++) {
            result[i] = localeManager.getDisplayName(codes[i]);
        }
        return result;
    }

    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        final Preference preference = findPreference(key);
        final String value = sharedPreferences.getString(key, "");
        if (Constants.Settings.LOCALE_CODE.equals(key)) {
            localeManager.changeLocale(value);
            preference.setSummary(localeManager.getDisplayName(value));
        } else {
            preference.setSummary(value);
        }
    }
}
