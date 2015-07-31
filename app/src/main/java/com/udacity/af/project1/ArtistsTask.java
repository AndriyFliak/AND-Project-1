package com.udacity.af.project1;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;

import com.udacity.af.project1.SpotifyContract.ArtistEntry;
import com.udacity.af.project1.SpotifyContract.SearchResultsEntry;
import com.udacity.af.project1.SpotifyContract.TrackEntry;
import com.udacity.af.project1.SpotifyContract.TopTracksEntry;

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

        Cursor searchResultsCursor = mActivity.getContentResolver().query(
                SearchResultsEntry.CONTENT_URI,
                new String[]{SearchResultsEntry._ID},
                SearchResultsEntry.COLUMN_SEARCH_TERM + " = ?",
                new String[]{mQuery},
                null);

        if (searchResultsCursor.moveToFirst()) {
            int artistIdIndex = searchResultsCursor.getColumnIndex(SearchResultsEntry.COLUMN_ARTIST_KEY);
            artistId = artistCursor.getLong(artistIdIndex);
            artistCursor.close();
        } else {

        }

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
        Vector<ContentValues> cVVector = new Vector<ContentValues>(artists.size());

        for (int i = 0; i < artists.size(); i++) {
            kaaes.spotify.webapi.android.models.Artist artist = artists.get(i);
            long artistId;
            Cursor artistCursor = mActivity.getContentResolver().query(
                    ArtistEntry.CONTENT_URI,
                    new String[]{ArtistEntry._ID},
                    ArtistEntry.COLUMN_SPOTIFY_ID + " = ?",
                    new String[]{artist.id},
                    null);

            if (artistCursor.moveToFirst()) {
                int artistIdIndex = artistCursor.getColumnIndex(ArtistEntry._ID);
                artistId = artistCursor.getLong(artistIdIndex);
                artistCursor.close();
            } else {
                ContentValues artistValues = new ContentValues();
                String imageUrl = artist.images.isEmpty() ? null : artist.images.get(artist.images.size() - 1).url;
                artistValues.put(ArtistEntry.COLUMN_SPOTIFY_ID, artist.id);
                artistValues.put(ArtistEntry.COLUMN_NAME, artist.name);
                artistValues.put(ArtistEntry.COLUMN_IMAGE_URL, imageUrl);
                Uri insertedUri = mActivity.getContentResolver().insert(ArtistEntry.CONTENT_URI, artistValues);
                artistId = ContentUris.parseId(insertedUri);
            }

            ContentValues searchResultsValues = new ContentValues();
            searchResultsValues.put(SearchResultsEntry.COLUMN_SEARCH_TERM, mQuery);
            searchResultsValues.put(SearchResultsEntry.COLUMN_ARTIST_KEY, artistId);
            searchResultsValues.put(SearchResultsEntry.COLUMN_POSITION, i);
            cVVector.add(searchResultsValues);

            String imageUrl = artist.images.isEmpty() ? null : artist.images.get(artist.images.size() - 1).url;
            artistsList.add(new Artist(artist.id, artist.name, imageUrl));
        }

        int inserted = 0;
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mActivity.getContentResolver().bulkInsert(SearchResultsEntry.CONTENT_URI, cvArray);
        }

        Log.d("Appodeal", "ArtistsTask Complete. " + inserted + " Inserted");

        return artistsList;
    }

    @Override
    protected void onPostExecute(ArrayList<Artist> artistsList) {
        super.onPostExecute(artistsList);
        mCallbacks.onPostExecute(artistsList);
    }
}
