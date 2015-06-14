package com.udacity.af.project1;

import android.os.Parcel;
import android.os.Parcelable;

public class Track implements Parcelable {

    private final String mTrackName;
    private final String mAlbumName;
    private final String mImageUrlLarge;
    private final String mImageUrlSmall;
    private final String mPreviewUrl;

    Track(String trackName, String albumName, String imageUrlLarge, String imageUrlSmall, String previewUrl) {
        mTrackName = trackName;
        mAlbumName = albumName;
        mImageUrlLarge = imageUrlLarge;
        mImageUrlSmall = imageUrlSmall;
        mPreviewUrl = previewUrl;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] { mTrackName, mAlbumName, mImageUrlLarge, mImageUrlSmall, mPreviewUrl } );
    }

    private Track(Parcel in){
        String[] data = new String[5];
        in.readStringArray(data);
        mTrackName = data[0];
        mAlbumName = data[1];
        mImageUrlLarge = data[2];
        mImageUrlSmall = data[3];
        mPreviewUrl = data[4];
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
