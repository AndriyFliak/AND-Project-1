package com.udacity.af.project1;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;

class ArtistsTask extends AsyncTask<Void, Void, ArrayList<Artist>> {

    interface Callbacks {
        void onPostExecute(ArrayList<Artist> artistsList);
    }

    private final Activity mActivity;
    private final Callbacks mCallbacks;
    private final String mQuery;

    ArtistsTask(Activity activity, Callbacks callbacks, String query) {
        mActivity = activity;
        mCallbacks = callbacks;
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
        mCallbacks.onPostExecute(artistsList);
    }
}
