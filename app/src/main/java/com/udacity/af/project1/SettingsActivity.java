package com.udacity.af.project1;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof ListPreference) {
            ListPreference listPref = (ListPreference) preference;
            preference.setSummary(listPref.getEntries()[listPref.findIndexOfValue((String) newValue)]);
        }
        return true;
    }
}