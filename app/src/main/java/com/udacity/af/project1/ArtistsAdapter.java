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

public class ArtistsAdapter extends ArrayAdapter<Artist> {

    private final ArrayList<Artist> mArtists;

    public ArtistsAdapter(Context context, int resource, ArrayList<Artist> objects) {
        super(context, resource, objects);
        mArtists = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_artist, null);
        }

        TextView name = (TextView)view.findViewById(R.id.artist_name);
        name.setText(mArtists.get(position).getName());

        ImageView image = (ImageView)view.findViewById(R.id.artist_image);
        Picasso.with(getContext()).load(mArtists.get(position).getImageUrl()).into(image);
        return view;
    }

    public ArrayList<Artist> getArtists() {
        return mArtists;
    }
}
