package com.udacity.af.project1;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnEditorAction;
import butterknife.OnItemClick;

public class ArtistsFragment extends Fragment implements ArtistsTask.Callbacks {

    private boolean mTwoPane = false;

    @OnEditorAction(R.id.artist_name_edit_text)
    public boolean findArtists(TextView v, int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            new ArtistsTask(getActivity(), this, v.getText().toString()).execute();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            v.clearFocus();
            return true;
        }
        return false;
    }

    @InjectView(R.id.artists_list_view) ListView artistsList;
    @OnItemClick(R.id.artists_list_view)
    public void launchTracksActivity(ListView artistsList, int position) {
        Artist artist = ((ArtistsAdapter) artistsList.getAdapter()).getArtists().get(position);
        if (ArtistsActivity.mTwoPane) {
            ((TracksFragment)getActivity().getFragmentManager().findFragmentByTag(TracksFragment.TAG)).onArtistSelected(artist);
        } else {
            Intent intent = new Intent(getActivity(), TracksActivity.class);
            intent.putExtra("artist", artist);
            getActivity().startActivity(intent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

        View view = inflater.inflate(R.layout.fragment_artists, container, false);
        ButterKnife.inject(this, view);

        if (savedInstanceState != null) {
            ArrayList<Artist> artists = savedInstanceState.getParcelableArrayList("artists");
            if (artists != null) {
                ArtistsAdapter adapter = new ArtistsAdapter(getActivity(), artists);
                artistsList.setAdapter(adapter);
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (artistsList != null && artistsList.getAdapter() != null) {
            savedInstanceState.putParcelableArrayList("artists", ((ArtistsAdapter) artistsList.getAdapter()).getArtists());
        }
    }

    @Override
    public void onPostExecute(ArrayList<Artist> artistsList) {
        if (artistsList == null) {
            Toast.makeText(getActivity(), R.string.no_results, Toast.LENGTH_LONG).show();
        } else {
            this.artistsList.setAdapter(new ArtistsAdapter(getActivity(), artistsList));
        }
    }
}
