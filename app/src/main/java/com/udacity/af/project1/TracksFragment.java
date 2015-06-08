package com.udacity.af.project1;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class TracksFragment extends Fragment implements Preference.OnPreferenceChangeListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Artist artist = getActivity().getIntent().getParcelableExtra("artist");
        View view = inflater.inflate(R.layout.fragment_tracks, container, false);
        ListView tracksList = (ListView)view.findViewById(R.id.tracks_list_view);
        if (savedInstanceState != null) {
            ArrayList<Track> tracks = savedInstanceState.getParcelableArrayList("tracks");
            if (tracks != null) {
                TracksAdapter adapter = new TracksAdapter(getActivity(), R.layout.list_item_track, tracks);
                tracksList.setAdapter(adapter);
            }
        } else {
            new TracksTask(getActivity(), artist.getSpotifyId()).execute();
        }

        tracksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Playing song", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        ListView tracksList = (ListView)getActivity().findViewById(R.id.tracks_list_view);
        if (tracksList != null && tracksList.getAdapter() != null) {
            savedInstanceState.putParcelableArrayList("tracks", ((TracksAdapter) tracksList.getAdapter()).getTracks());
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof ListPreference) {
            ListPreference listPref = (ListPreference) preference;
            preference.setSummary(listPref.getEntries()[listPref.findIndexOfValue((String)newValue)]);
        }
        return true;
    }
}
