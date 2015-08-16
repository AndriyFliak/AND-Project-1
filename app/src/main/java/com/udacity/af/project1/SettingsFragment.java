package com.udacity.af.project1;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        ListPreference countryPref = (ListPreference) findPreference("pref_country");
        countryPref.setSummary(countryPref.getEntry());
        countryPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (preference instanceof ListPreference) {
                    ListPreference listPref = (ListPreference) preference;
                    preference.setSummary(listPref.getEntries()[listPref.findIndexOfValue((String) newValue)]);
                }
                return true;
            }
        });

        SwitchPreference notificationPref = (SwitchPreference) findPreference("pref_notification");
        notificationPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (preference.getKey().equals("pref_notification")) {
                    if ((boolean)newValue) {
                        Intent serviceIntent = new Intent(getActivity(), MediaPlayerService.class);
                        serviceIntent.setAction(MediaPlayerService.ACTION_NOTIFICATION);
                        getActivity().startService(serviceIntent);
                    } else {
                        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancelAll();
                    }
                }
                return true;
            }
        });
    }
}
