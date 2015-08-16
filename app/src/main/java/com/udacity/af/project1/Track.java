package com.udacity.af.project1;

import android.os.Parcel;
import android.os.Parcelable;

public class Track implements Parcelable {

    private final String mArtistName;
    private final String mTrackName;
    private final String mAlbumName;
    private final String mImageUrlLarge;
    private final String mImageUrlSmall;
    private final String mPreviewUrl;
    private final String mId;

    Track(String artist, String trackName, String albumName, String imageUrlLarge, String imageUrlSmall, String previewUrl, String id) {
        mArtistName = artist;
        mTrackName = trackName;
        mAlbumName = albumName;
        mImageUrlLarge = imageUrlLarge;
        mImageUrlSmall = imageUrlSmall;
        mPreviewUrl = previewUrl;
        mId = id;
    }

    public String getArtistName() {
        return mArtistName;
    }

    public String getTrackName() {
        return mTrackName;
    }

    public String getAlbumName() {
        return mAlbumName;
    }

    public String getImageUrlLarge() {
        return mImageUrlLarge;
    }

    public String getImageUrlSmall() {
        return mImageUrlSmall;
    }

    public String getPreviewUrl() {
        return mPreviewUrl;
    }

    public String getUrl() {
        return String.format("http://open.spotify.com/track/%s", mId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{mArtistName, mTrackName, mAlbumName, mImageUrlLarge, mImageUrlSmall, mPreviewUrl, mId});
    }

    private Track(Parcel in) {
        String[] data = new String[7];
        in.readStringArray(data);
        mArtistName = data[0];
        mTrackName = data[1];
        mAlbumName = data[2];
        mImageUrlLarge = data[3];
        mImageUrlSmall = data[4];
        mPreviewUrl = data[5];
        mId = data[6];
    }

    public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

}
