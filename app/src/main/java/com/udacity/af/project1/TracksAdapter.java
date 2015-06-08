package com.udacity.af.project1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TracksAdapter extends ArrayAdapter<Track> {

    private final ArrayList<Track> mTracks;

    public TracksAdapter(Context context, int resource, ArrayList<Track> objects) {
        super(context, resource, objects);
        mTracks = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_track, null);
        }

        TextView trackName = (TextView)view.findViewById(R.id.track_name);
        trackName.setText(mTracks.get(position).getTrackName());

        TextView albumName = (TextView)view.findViewById(R.id.album_name);
        albumName.setText(mTracks.get(position).getAlbumName());

        ImageView image = (ImageView)view.findViewById(R.id.artist_image);
        Picasso.with(getContext()).load(mTracks.get(position).getImageUrlSmall()).into(image);
        return view;
    }

    public ArrayList<Track> getTracks() {
        return mTracks;
    }
}
