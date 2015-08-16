package com.udacity.af.project1;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PlayerFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener {

    private ArrayList<Track> mTracks;
    private int mPosition;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("track")) {
                Track track = intent.getParcelableExtra("track");
                mArtist.setText(track.getArtistName());
                mAlbumName.setText(track.getAlbumName());
                Picasso.with(getActivity()).load(track.getImageUrlLarge()).into(mAlbumCover);
                mTrackName.setText(track.getTrackName());
                mTrackSeekBar.setOnSeekBarChangeListener(PlayerFragment.this);
            }
            if (intent.hasExtra("duration")) {
                int duration = intent.getIntExtra("duration", 0);
                if (duration == -1) {
                    mTrackDuration.setText("");
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.US);
                    mTrackDuration.setText(sdf.format(new Date(intent.getIntExtra("duration", 0))));
                    mTrackSeekBar.setMax(intent.getIntExtra("duration", 0));
                    mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
            if (intent.hasExtra("paused")) {
                if (intent.getBooleanExtra("paused", true)) {
                    mPlayPauseButton.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
            if (intent.hasExtra("seek_position")) {
                SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.US);
                int progress = intent.getIntExtra("seek_position", 0);
                mTrackSeekBar.setProgress(progress);
                mTrackProgress.setText(sdf.format(new Date(progress)));
            }
        }
    };

    @InjectView(R.id.artist) TextView mArtist;
    @InjectView(R.id.album) TextView mAlbumName;
    @InjectView(R.id.album_cover) ImageView mAlbumCover;
    @InjectView(R.id.track_name) TextView mTrackName;
    @InjectView(R.id.track_seek_bar) SeekBar mTrackSeekBar;
    @InjectView(R.id.track_progress) TextView mTrackProgress;
    @InjectView(R.id.track_duration) TextView mTrackDuration;
    @InjectView(R.id.play_pause) ImageButton mPlayPauseButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

        View view = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.inject(this, view);

        if (savedInstanceState != null) {
            Intent serviceIntent = new Intent(getActivity(), MediaPlayerService.class);
            serviceIntent.setAction(MediaPlayerService.ACTION_STATS);
            getActivity().startService(serviceIntent);
        } else if (mTracks != null) {
            setTrack(mTracks, mPosition);
        } else {
            ArrayList<Track> tracks = getActivity().getIntent().getParcelableArrayListExtra("tracks");
            int position = getActivity().getIntent().getIntExtra("position", 0);
            setTrack(tracks, position);
        }

        return view;
    }

    public void onTrackSelected(ArrayList<Track> tracks, int position) {
        mTracks = tracks;
        mPosition = position;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mMessageReceiver, new IntentFilter("PlayerUpdate"));
        Intent serviceIntent = new Intent(getActivity(), MediaPlayerService.class);
        serviceIntent.setAction(MediaPlayerService.ACTION_STATS);
        getActivity().startService(serviceIntent);
    }

    public void setTrack(ArrayList<Track> tracks, int position) {
        Intent serviceIntent = new Intent(getActivity(), MediaPlayerService.class);
        serviceIntent.setAction(MediaPlayerService.ACTION_START);
        serviceIntent.putParcelableArrayListExtra("tracks", tracks);
        serviceIntent.putExtra("position", position);
        getActivity().startService(serviceIntent);
    }

    @OnClick(R.id.play_pause)
    public void playPause() {
        Intent serviceIntent = new Intent(getActivity(), MediaPlayerService.class);
        serviceIntent.setAction(MediaPlayerService.ACTION_PLAY_PAUSE);
        getActivity().startService(serviceIntent);
    }

    @OnClick(R.id.previous_track)
    public void previousTrack() {
        Intent serviceIntent = new Intent(getActivity(), MediaPlayerService.class);
        serviceIntent.setAction(MediaPlayerService.ACTION_PREVIOUS);
        getActivity().startService(serviceIntent);
    }

    @OnClick(R.id.next_track)
    public void nextTrack() {
        Intent serviceIntent = new Intent(getActivity(), MediaPlayerService.class);
        serviceIntent.setAction(MediaPlayerService.ACTION_NEXT);
        getActivity().startService(serviceIntent);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            Intent serviceIntent = new Intent(getActivity(), MediaPlayerService.class);
            serviceIntent.setAction(MediaPlayerService.ACTION_SEEK);
            serviceIntent.putExtra("progress", progress);
            getActivity().startService(serviceIntent);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setOnDismissListener(null);
        }
        super.onDestroyView();
    }
}
