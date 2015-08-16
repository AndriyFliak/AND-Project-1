package com.udacity.af.project1;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public class TracksFragment extends Fragment implements TracksTask.Callbacks {

    static final String TAG = "TracksFragment";

    @InjectView(R.id.tracks_list_view) ListView tracksList;
    @OnItemClick(R.id.tracks_list_view)
    public void playSong(ListView tracksList, int position) {
        if (ArtistsActivity.mTwoPane) {
            FragmentManager fragmentManager = getActivity().getFragmentManager();
            PlayerFragment playerFragment = new PlayerFragment();
            playerFragment.onTrackSelected(((TracksAdapter) tracksList.getAdapter()).getTracks(), position);
            playerFragment.show(fragmentManager, "dialog");
        } else {
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.putParcelableArrayListExtra("tracks", ((TracksAdapter) tracksList.getAdapter()).getTracks());
            intent.putExtra("position", position);
            getActivity().startActivity(intent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

        View view = inflater.inflate(R.layout.fragment_tracks, container, false);
        ButterKnife.inject(this, view);

        if (savedInstanceState != null) {
            ArrayList<Track> tracks = savedInstanceState.getParcelableArrayList("tracks");
            if (tracks != null) {
                TracksAdapter adapter = new TracksAdapter(getActivity(), tracks);
                tracksList.setAdapter(adapter);
            }
        } else {
            if (!getActivity().getIntent().hasExtra("artist")) {
                return view;
            }
            Artist artist = getActivity().getIntent().getParcelableExtra("artist");
            new TracksTask(getActivity(), this, artist).execute();
        }

        return view;
    }

    public void onArtistSelected(Artist artist) {
        new TracksTask(getActivity(), this, artist).execute();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (tracksList != null && tracksList.getAdapter() != null) {
            savedInstanceState.putParcelableArrayList("tracks", ((TracksAdapter) tracksList.getAdapter()).getTracks());
        }
    }

    @Override
    public void onPostExecute(ArrayList<Track> tracksList) {
        if (tracksList == null) {
            Toast.makeText(getActivity(), R.string.no_results, Toast.LENGTH_LONG).show();
        } else {
            this.tracksList.setAdapter(new TracksAdapter(getActivity(), tracksList));
        }
    }
}
