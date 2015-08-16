package com.udacity.af.project1;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

class TracksTask extends AsyncTask<Void, Void, ArrayList<Track>> {

    interface Callbacks {
        void onPostExecute(ArrayList<Track> tracksList);
    }

    private final Activity mActivity;
    private final Callbacks mCallbacks;
    private final Artist mArtist;

    TracksTask(Activity activity, Callbacks callbacks, Artist artist) {
        mActivity = activity;
        mCallbacks = callbacks;
        mArtist = artist;
    }

    @Override
    protected ArrayList<Track> doInBackground(Void... params) {
        if (!Utils.isNetworkAvailable(mActivity)) {
            return null;
        }
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mActivity);
        String countryPref = sharedPref.getString("pref_country", "US");
        if (countryPref.equals("0")) {
            countryPref = "US";
        }
        Tracks results;
        try {
            results = new SpotifyApi().getService().getArtistTopTrack(mArtist.getSpotifyId(), countryPref);
        } catch (RetrofitError error) {
            return null;
        }
        List<kaaes.spotify.webapi.android.models.Track> tracks = results.tracks;

        if (tracks.isEmpty()) {
            return null;
        }

        ArrayList<Track> tracksList = new ArrayList<>(tracks.size());
        for (kaaes.spotify.webapi.android.models.Track track : tracks) {
            String imageUrlLarge = null;
            String imageUrlSmall = null;
            if (!track.album.images.isEmpty()) {
                for (Image image : track.album.images) {
                    if (image.width == 200) {
                        imageUrlSmall = image.url;
                    } else if (image.width == 640) {
                        imageUrlLarge = image.url;
                    }
                }
                if (imageUrlLarge == null) {
                    imageUrlLarge = track.album.images.get(0).url;
                }
                if (imageUrlSmall == null) {
                    imageUrlSmall = track.album.images.get(track.album.images.size() - 1).url;
                }
            }
            tracksList.add(new Track(mArtist.getName(), track.name, track.album.name, imageUrlLarge, imageUrlSmall, track.preview_url));
        }

        return tracksList;
    }

    @Override
    protected void onPostExecute(ArrayList<Track> tracksList) {
        super.onPostExecute(tracksList);
        mCallbacks.onPostExecute(tracksList);
    }
}
