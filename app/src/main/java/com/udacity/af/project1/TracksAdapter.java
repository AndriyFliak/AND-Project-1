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
        ViewHolder holder;
        if (convertView == null) {
            convertView = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_track, null);
            holder = new ViewHolder();
            holder.trackName = (TextView)convertView.findViewById(R.id.track_name);
            holder.albumName = (TextView)convertView.findViewById(R.id.album_name);
            holder.image = (ImageView)convertView.findViewById(R.id.artist_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.trackName.setText(mTracks.get(position).getTrackName());
        holder.albumName.setText(mTracks.get(position).getAlbumName());
        Picasso.with(getContext()).load(mTracks.get(position).getImageUrlSmall()).into(holder.image);
        return convertView;
    }

    static class ViewHolder {
        TextView trackName;
        TextView albumName;
        ImageView image;
    }

    public ArrayList<Track> getTracks() {
        return mTracks;
    }
}
