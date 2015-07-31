package com.udacity.af.project1;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class SpotifyContract {

    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SEARCH_RESULTS = "search_results";
    public static final String PATH_ARTIST = "artist";
    public static final String PATH_TOP_TRACKS = "top_tracks";
    public static final String PATH_TRACK = "track";

    public static final class SearchResultsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEARCH_RESULTS).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SEARCH_RESULTS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SEARCH_RESULTS;

        public static final String TABLE_NAME = "search_results";

        public static final String COLUMN_SEARCH_TERM = "search_term";
        public static final String COLUMN_ARTIST_KEY = "artist_id";
        public static final String COLUMN_POSITION = "position";

        public static Uri buildSearchTermUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWithSearchTerm(String searchTerm) {
            return CONTENT_URI.buildUpon().appendPath(searchTerm).build();
        }

        public static String getSearchTermFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class ArtistEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTIST).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;

        public static final String TABLE_NAME = "artists";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SPOTIFY_ID = "spotify_id";
        public static final String COLUMN_IMAGE_URL = "image_url";

        public static Uri buildArtistUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class TopTracksEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_TRACKS).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_TRACKS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_TRACKS;

        public static final String TABLE_NAME = "top_tracks";

        public static final String COLUMN_ARTIST_KEY = "artist_id";
        public static final String COLUMN_COUNTRY_CODE = "country_code";
        public static final String COLUMN_TRACK_KEY = "track_id";
        public static final String COLUMN_POSITION = "position";

        public static Uri buildTopTracksUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTopTracksWithArtistAndCountry(long artistId, String countryCode) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(artistId)).appendPath(countryCode).build();
        }

        public static String getArtistIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getCountryCodeFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }

    public static final class TrackEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRACK).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACK;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACK;

        public static final String TABLE_NAME = "tracks";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SPOTIFY_ID = "spotify_id";
        public static final String COLUMN_ALBUM_NAME = "album_id";
        public static final String COLUMN_LARGE_IMAGE_URL = "large_image_url";
        public static final String COLUMN_SMALL_IMAGE_URL = "small_image_url";
        public static final String COLUMN_PREVIEW_URL = "preview_url";

        public static Uri buildTrackUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
