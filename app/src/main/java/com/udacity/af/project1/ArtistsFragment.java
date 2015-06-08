package com.udacity.af.project1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ArtistsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artists, container, false);

        EditText artistName = (EditText)view.findViewById(R.id.artist_name_edit_text);
        artistName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    new ArtistsTask(getActivity(), v.getText().toString()).execute();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    v.clearFocus();
                    handled = true;
                }
                return handled;
            }
        });

        ListView artistsList = (ListView)view.findViewById(R.id.artists_list_view);
        if (savedInstanceState != null) {
            ArrayList<Artist> artists = savedInstanceState.getParcelableArrayList("artists");
            if (artists != null) {
                ArtistsAdapter adapter = new ArtistsAdapter(getActivity(), R.layout.list_item_artist, artists);
                artistsList.setAdapter(adapter);
            }
        }

        artistsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), TracksActivity.class);
                ListView artistsList = (ListView)getActivity().findViewById(R.id.artists_list_view);
                intent.putExtra("artist", ((ArtistsAdapter) artistsList.getAdapter()).getArtists().get(position));
                getActivity().startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        ListView artistsList = (ListView)getActivity().findViewById(R.id.artists_list_view);
        if (artistsList != null && artistsList.getAdapter() != null) {
            savedInstanceState.putParcelableArrayList("artists", ((ArtistsAdapter) artistsList.getAdapter()).getArtists());
        }
    }
}
