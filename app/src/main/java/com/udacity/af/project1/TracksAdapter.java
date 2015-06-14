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

import butterknife.ButterKnife;
import butterknife.InjectView;

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
            holder = new ViewHolder(convertView);
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
        @InjectView(R.id.track_name) TextView trackName;
        @InjectView(R.id.album_name) TextView albumName;
        @InjectView(R.id.artist_image) ImageView image;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public ArrayList<Track> getTracks() {
        return mTracks;
    }
}
