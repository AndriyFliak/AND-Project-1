package com.udacity.af.project1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.udacity.af.project1.SpotifyContract.ArtistEntry;
import com.udacity.af.project1.SpotifyContract.SearchResultsEntry;
import com.udacity.af.project1.SpotifyContract.TrackEntry;
import com.udacity.af.project1.SpotifyContract.TopTracksEntry;

public class SpotifyDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "spotify.db";

    public static final int DATABASE_VERSION = 1;

    public SpotifyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ARTISTS_TABLE = "CREATE TABLE " + ArtistEntry.TABLE_NAME + " (" +
                ArtistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ArtistEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ArtistEntry.COLUMN_SPOTIFY_ID + " TEXT NOT NULL, " +
                ArtistEntry.COLUMN_IMAGE_URL + " TEXT, " +
                " UNIQUE (" + ArtistEntry.COLUMN_SPOTIFY_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_SEARCH_RESULTS_TABLE = "CREATE TABLE " + SearchResultsEntry.TABLE_NAME + " (" +
                SearchResultsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SearchResultsEntry.COLUMN_SEARCH_TERM + " TEXT NOT NULL, " +
                SearchResultsEntry.COLUMN_ARTIST_KEY + " INTEGER NOT NULL, " +
                SearchResultsEntry.COLUMN_POSITION + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + SearchResultsEntry.COLUMN_ARTIST_KEY + ") REFERENCES " +
                ArtistEntry.TABLE_NAME + " (" + ArtistEntry._ID + ")," +
                " UNIQUE (" + SearchResultsEntry.COLUMN_SEARCH_TERM + ", "  + SearchResultsEntry.COLUMN_ARTIST_KEY + ", "  + SearchResultsEntry.COLUMN_POSITION + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_TRACKS_TABLE = "CREATE TABLE " + TrackEntry.TABLE_NAME + " (" +
                TrackEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TrackEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                TrackEntry.COLUMN_SPOTIFY_ID + " TEXT NOT NULL, " +
                TrackEntry.COLUMN_ALBUM_NAME + " TEXT NOT NULL, " +
                TrackEntry.COLUMN_LARGE_IMAGE_URL + " TEXT, " +
                TrackEntry.COLUMN_SMALL_IMAGE_URL + " TEXT, " +
                TrackEntry.COLUMN_PREVIEW_URL + " TEXT);";

        final String SQL_CREATE_TOP_TRACKS_TABLE = "CREATE TABLE " + TopTracksEntry.TABLE_NAME + " (" +
                TopTracksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TopTracksEntry.COLUMN_ARTIST_KEY + " INTEGER NOT NULL, " +
                TopTracksEntry.COLUMN_COUNTRY_CODE + " TEXT NOT NULL, " +
                TopTracksEntry.COLUMN_TRACK_KEY + " INTEGER NOT NULL, " +
                TopTracksEntry.COLUMN_POSITION + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + TopTracksEntry.COLUMN_ARTIST_KEY + ") REFERENCES " +
                ArtistEntry.TABLE_NAME + " (" + ArtistEntry._ID + "), " +
                " FOREIGN KEY (" + TopTracksEntry.COLUMN_TRACK_KEY + ") REFERENCES " +
                TrackEntry.TABLE_NAME + " (" + TrackEntry._ID + "), " +
                " UNIQUE (" + TopTracksEntry.COLUMN_ARTIST_KEY + ", "  + TopTracksEntry.COLUMN_COUNTRY_CODE + ", "  + TopTracksEntry.COLUMN_TRACK_KEY + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_ARTISTS_TABLE);
        db.execSQL(SQL_CREATE_SEARCH_RESULTS_TABLE);
        db.execSQL(SQL_CREATE_TRACKS_TABLE);
        db.execSQL(SQL_CREATE_TOP_TRACKS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TopTracksEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrackEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SearchResultsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ArtistEntry.TABLE_NAME);
        onCreate(db);
    }
}
