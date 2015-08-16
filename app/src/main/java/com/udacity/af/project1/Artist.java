package com.udacity.af.project1;

import android.os.Parcel;
import android.os.Parcelable;

public class Artist implements Parcelable {

    private final String mSpotifyId;
    private final String mName;
    private final String mImageUrl;

    Artist(String spotifyId, String name, String imageUrl) {
        mSpotifyId = spotifyId;
        mName = name;
        mImageUrl = imageUrl;
    }

    String getSpotifyId() {
        return mSpotifyId;
    }

    String getName() {
        return mName;
    }

    String getImageUrl() {
        return mImageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {mSpotifyId, mName, mImageUrl});
    }

    private Artist(Parcel in) {
        String[] data = new String[3];
        in.readStringArray(data);
        mSpotifyId = data[0];
        mName = data[1];
        mImageUrl = data[2];
    }

    public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}
