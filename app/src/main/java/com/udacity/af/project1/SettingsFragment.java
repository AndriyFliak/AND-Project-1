package com.udacity.af.project1;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        ListPreference countryPref = (ListPreference)findPreference("pref_country");
        countryPref.setSummary(countryPref.getEntry());
        countryPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (preference instanceof ListPreference) {
                    ListPreference listPref = (ListPreference) preference;
                    preference.setSummary(listPref.getEntries()[listPref.findIndexOfValue((String)newValue)]);
                }
                return true;
            }
        });
    }
}
