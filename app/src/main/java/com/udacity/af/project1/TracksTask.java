package com.udacity.af.project1;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

public class TracksTask extends AsyncTask<Void, Void, ArrayList<Track>> {

    private final Activity mActivity;
    private final String mSpotifyId;

    TracksTask(Activity activity, String spotifyId) {
        mActivity = activity;
        mSpotifyId = spotifyId;
    }

    @Override
    protected ArrayList<Track> doInBackground(Void... params) {
        if (!Utils.isNetworkAvailable(mActivity)) {
            return null;
        }
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotify = api.getService();
        Tracks results;
        try {
            Map<String, Object> queryParams = new HashMap<>(1);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mActivity);
            String countryPref = sharedPref.getString("pref_country", "US");
            if (countryPref.equals("0")) {
                countryPref = "US";
            }
            queryParams.put("country", countryPref);
            results = spotify.getArtistTopTrack(mSpotifyId, queryParams);
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
            tracksList.add(new Track(track.name, track.album.name, imageUrlLarge, imageUrlSmall, track.preview_url));
        }

        return tracksList;
    }

    @Override
    protected void onPostExecute(ArrayList<Track> tracksList) {
        super.onPostExecute(tracksList);
        if (tracksList == null) {
            Toast.makeText(mActivity, "No results", Toast.LENGTH_LONG).show();
        } else {
            ListView tracksListView = (ListView) mActivity.findViewById(R.id.tracks_list_view);
            tracksListView.setAdapter(new TracksAdapter(mActivity, R.layout.list_item_artist, tracksList));
        }
    }
}
