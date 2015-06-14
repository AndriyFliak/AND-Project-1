package com.udacity.af.project1;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;

public class ArtistsTask extends AsyncTask<Void, Void, ArrayList<Artist>> {

    private final Activity mActivity;
    private final ListView mArtistsList;
    private final String mQuery;

    ArtistsTask(Activity activity, ListView artistsList, String query) {
        mActivity = activity;
        mArtistsList = artistsList;
        mQuery = query;
    }

    @Override
    protected ArrayList<Artist> doInBackground(Void... params) {
        if (!Utils.isNetworkAvailable(mActivity)) {
            return null;
        }
        ArtistsPager results;
        try {
            results = new SpotifyApi().getService().searchArtists(mQuery);
        } catch (RetrofitError error) {
            return null;
        }
        List<kaaes.spotify.webapi.android.models.Artist> artists = results.artists.items;

        if (artists.isEmpty()) {
            return null;
        }

        ArrayList<Artist> artistsList = new ArrayList<>(artists.size());
        for (kaaes.spotify.webapi.android.models.Artist artist : artists) {
            String imageUrl = artist.images.isEmpty() ? null : artist.images.get(artist.images.size() - 1).url;
            artistsList.add(new Artist(artist.id, artist.name, imageUrl));
        }

        return artistsList;
    }

    @Override
    protected void onPostExecute(ArrayList<Artist> artistsList) {
        super.onPostExecute(artistsList);
        if (artistsList == null) {
            Toast.makeText(mActivity, R.string.no_results, Toast.LENGTH_LONG).show();
        } else {
            mArtistsList.setAdapter(new ArtistsAdapter(mActivity, R.layout.list_item_artist, artistsList));
        }
    }
}
