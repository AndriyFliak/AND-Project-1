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

public class ArtistsAdapter extends ArrayAdapter<Artist> {

    private final ArrayList<Artist> mArtists;

    public ArtistsAdapter(Context context, int resource, ArrayList<Artist> objects) {
        super(context, resource, objects);
        mArtists = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_artist, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(mArtists.get(position).getName());
        Picasso.with(getContext()).load(mArtists.get(position).getImageUrl()).into(holder.image);
        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.artist_name) TextView name;
        @InjectView(R.id.artist_image) ImageView image;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public ArrayList<Artist> getArtists() {
        return mArtists;
    }
}
