package com.udacity.af.project1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class PlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private ArrayList<Track> mTracks;
    private int mPosition;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("duration")) {
                SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.US);
                mTrackDuration.setText(sdf.format(new Date(intent.getIntExtra("duration", 0))));
                mTrackSeekBar.setMax(intent.getIntExtra("duration", 0));
                mPlayPauseButton.setImageResource(android.R.drawable.ic_media_pause);
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
            if (intent.hasExtra("finish")) {
                nextTrack();
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

        boolean start = true;
        if (savedInstanceState != null) {
            mTracks = savedInstanceState.getParcelableArrayList("tracks");
            mPosition = savedInstanceState.getInt("position");
            start = false;
        } else {
            mTracks = getActivity().getIntent().getParcelableArrayListExtra("tracks");
            mPosition = getActivity().getIntent().getIntExtra("position", 0);
        }

        View view = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.inject(this, view);

        changeTrack(start);

        return view;
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

    public void changeTrack() {
        changeTrack(true);
    }

    public void changeTrack(boolean start) {
        mArtist.setText(mTracks.get(mPosition).getArtistName());
        mAlbumName.setText(mTracks.get(mPosition).getAlbumName());
        Picasso.with(getActivity()).load(mTracks.get(mPosition).getImageUrlLarge()).into(mAlbumCover);
        mTrackName.setText(mTracks.get(mPosition).getTrackName());

        mTrackSeekBar.setOnSeekBarChangeListener(this);

        if (start) {
            mTrackSeekBar.setProgress(0);
            mTrackProgress.setText("00:00");
            mTrackDuration.setText("");
            String url = mTracks.get(mPosition).getPreviewUrl();
            Intent serviceIntent = new Intent(getActivity(), MediaPlayerService.class);
            serviceIntent.setAction(MediaPlayerService.ACTION_START);
            serviceIntent.putExtra("url", url);
            getActivity().startService(serviceIntent);
        } else {
            Intent serviceIntent = new Intent(getActivity(), MediaPlayerService.class);
            serviceIntent.setAction(MediaPlayerService.ACTION_STATS);
            getActivity().startService(serviceIntent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (mTracks != null) {
            savedInstanceState.putParcelableArrayList("tracks", mTracks);
            savedInstanceState.putInt("position", mPosition);
            savedInstanceState.putInt("position", mPosition);
        }
    }

    @OnClick(R.id.play_pause)
    public void playPause() {
        Intent serviceIntent = new Intent(getActivity(), MediaPlayerService.class);
        serviceIntent.setAction(MediaPlayerService.ACTION_PLAY_PAUSE);
        getActivity().startService(serviceIntent);
    }

    @OnClick(R.id.previous_track)
    public void previousTrack() {
        mPosition = mPosition - 1;
        if (mPosition < 0) {
            mPosition = mTracks.size() - 1;
        }
        changeTrack();
    }

    @OnClick(R.id.next_track)
    public void nextTrack() {
        mPosition = mPosition + 1;
        if (mPosition >= mTracks.size()) {
            mPosition = 0;
        }
        changeTrack();
    }

    @Override
    public void onPause() {
        super.onStop();
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
}
