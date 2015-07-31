package com.udacity.af.project1;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.udacity.af.project1.SpotifyContract.ArtistEntry;
import com.udacity.af.project1.SpotifyContract.SearchResultsEntry;
import com.udacity.af.project1.SpotifyContract.TrackEntry;
import com.udacity.af.project1.SpotifyContract.TopTracksEntry;

public class SpotifyProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SpotifyDbHelper mOpenHelper;

    static final int SEARCH_RESULTS = 100;
    static final int SEARCH_RESULTS_WITH_SEARCH_TERM = 101;
    static final int ARTIST = 200;
    static final int TOP_TRACKS = 300;
    static final int TOP_TRACKS_WITH_ARTIST_AND_COUNTRY = 301;
    static final int TRACK = 400;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SpotifyContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, SpotifyContract.PATH_SEARCH_RESULTS, SEARCH_RESULTS);
        matcher.addURI(authority, SpotifyContract.PATH_SEARCH_RESULTS + "/*", SEARCH_RESULTS_WITH_SEARCH_TERM);

        matcher.addURI(authority, SpotifyContract.PATH_ARTIST, ARTIST);

        matcher.addURI(authority, SpotifyContract.PATH_TOP_TRACKS, TOP_TRACKS);
        matcher.addURI(authority, SpotifyContract.PATH_TOP_TRACKS + "/#/#", TOP_TRACKS_WITH_ARTIST_AND_COUNTRY);

        matcher.addURI(authority, SpotifyContract.PATH_TRACK, TRACK);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new SpotifyDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case SEARCH_RESULTS:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SearchResultsEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case SEARCH_RESULTS_WITH_SEARCH_TERM:
                String searchTerm = SearchResultsEntry.getSearchTermFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SearchResultsEntry.TABLE_NAME, projection,
                        SearchResultsEntry.TABLE_NAME + "." + SearchResultsEntry.COLUMN_SEARCH_TERM + " = ? ",
                        new String[] {searchTerm},
                        null, null, sortOrder);
                break;
            case ARTIST:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ArtistEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case TOP_TRACKS:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TopTracksEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case TOP_TRACKS_WITH_ARTIST_AND_COUNTRY:
                String artistId = TopTracksEntry.getArtistIdFromUri(uri);
                String countryCode = TopTracksEntry.getCountryCodeFromUri(uri);
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TopTracksEntry.TABLE_NAME, projection,
                        TopTracksEntry.TABLE_NAME + "." + TopTracksEntry.COLUMN_ARTIST_KEY + " = ? AND " +
                                TopTracksEntry.TABLE_NAME + "." + TopTracksEntry.COLUMN_COUNTRY_CODE + " = ? ",
                        new String[] {artistId, countryCode},
                        null, null, sortOrder);
                break;
            case TRACK:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TrackEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case SEARCH_RESULTS:
                return SearchResultsEntry.CONTENT_TYPE;
            case SEARCH_RESULTS_WITH_SEARCH_TERM:
                return SearchResultsEntry.CONTENT_TYPE;
            case ARTIST:
                return ArtistEntry.CONTENT_TYPE;
            case TOP_TRACKS:
                return TopTracksEntry.CONTENT_TYPE;
            case TOP_TRACKS_WITH_ARTIST_AND_COUNTRY:
                return TopTracksEntry.CONTENT_TYPE;
            case TRACK:
                return TrackEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case SEARCH_RESULTS: {
                long _id = db.insert(SearchResultsEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = SearchResultsEntry.buildSearchTermUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case ARTIST: {
                long _id = db.insert(ArtistEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ArtistEntry.buildArtistUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TOP_TRACKS: {
                long _id = db.insert(TopTracksEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = TopTracksEntry.buildTopTracksUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRACK: {
                long _id = db.insert(TrackEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = TrackEntry.buildTrackUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount;
        switch (match) {
            case SEARCH_RESULTS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SearchResultsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case ARTIST:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ArtistEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case TOP_TRACKS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TopTracksEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case TRACK:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TrackEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                returnCount = super.bulkInsert(uri, values);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case SEARCH_RESULTS:
                rowsDeleted = db.delete(SearchResultsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ARTIST:
                rowsDeleted = db.delete(ArtistEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TOP_TRACKS:
                rowsDeleted = db.delete(TopTracksEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRACK:
                rowsDeleted = db.delete(TrackEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case SEARCH_RESULTS:
                rowsUpdated = db.update(SearchResultsEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case ARTIST:
                rowsUpdated = db.update(ArtistEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TOP_TRACKS:
                rowsUpdated = db.update(TopTracksEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TRACK:
                rowsUpdated = db.update(TrackEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
