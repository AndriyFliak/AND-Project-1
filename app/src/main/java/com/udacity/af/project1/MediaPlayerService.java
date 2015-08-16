package com.udacity.af.project1;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;

public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

    static final String ACTION_START = "com.example.action.START";
    static final String ACTION_SEEK = "com.example.action.SEEK";
    static final String ACTION_PLAY_PAUSE = "com.example.action.PLAY_PAUSE";
    static final String ACTION_STATS = "com.example.action.STATS";
    static final String ACTION_STOP = "com.example.action.STOP";

    private MediaPlayer mPlayer = null;
    private Handler mHandler = new Handler();
    private Runnable mUpdateRunnable = new Runnable() {

        @Override
        public void run() {
            if (mPlayer != null && mPlayer.isPlaying()) {
                Intent intent = new Intent("PlayerUpdate");
                intent.putExtra("seek_position", mPlayer.getCurrentPosition());
                LocalBroadcastManager.getInstance(MediaPlayerService.this).sendBroadcast(intent);
            }
            mHandler.postDelayed(this, 100);
        }
    };

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }
        if (intent.getAction().equals(ACTION_START)) {
            String url = intent.getStringExtra("url");
            if (mPlayer != null) {
                mPlayer.stop();
                mPlayer.release();
            }
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnSeekCompleteListener(this);
            try {
                mPlayer.setDataSource(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayer.prepareAsync();
        } else if (intent.getAction().equals(ACTION_SEEK)) {
            if (mPlayer != null) {
                mHandler.removeCallbacks(mUpdateRunnable);
                mPlayer.pause();
                mPlayer.seekTo(intent.getIntExtra("progress", 0));
            }
        } else if (intent.getAction().equals(ACTION_PLAY_PAUSE)) {
            if (mPlayer != null) {
                if (mPlayer.isPlaying()) {
                    mHandler.removeCallbacks(mUpdateRunnable);
                    mPlayer.pause();
                    Intent newIntent = new Intent("PlayerUpdate");
                    newIntent.putExtra("paused", true);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(newIntent);
                } else {
                    mPlayer.start();
                    mHandler.removeCallbacks(mUpdateRunnable);
                    mHandler.post(mUpdateRunnable);
                    Intent newIntent = new Intent("PlayerUpdate");
                    newIntent.putExtra("paused", false);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(newIntent);
                }
            }
        } else if (intent.getAction().equals(ACTION_STATS)) {
            if (mPlayer != null) {
                Intent newIntent = new Intent("PlayerUpdate");
                newIntent.putExtra("duration", mPlayer.getDuration());
                newIntent.putExtra("paused", !mPlayer.isPlaying());
                newIntent.putExtra("seek_position", mPlayer.getCurrentPosition());
                LocalBroadcastManager.getInstance(this).sendBroadcast(newIntent);
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onPrepared(MediaPlayer player) {
        player.start();
        mHandler.removeCallbacks(mUpdateRunnable);
        mHandler.post(mUpdateRunnable);
        Intent intent = new Intent("PlayerUpdate");
        intent.putExtra("duration", mPlayer.getDuration());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (!mPlayer.isPlaying()) {
            mPlayer.start();
            mHandler.removeCallbacks(mUpdateRunnable);
            mHandler.post(mUpdateRunnable);
            Intent newIntent = new Intent("PlayerUpdate");
            newIntent.putExtra("paused", false);
            LocalBroadcastManager.getInstance(this).sendBroadcast(newIntent);
        }
    }

    @Override
    public void onCompletion(MediaPlayer player) {
        Intent intent = new Intent("PlayerUpdate");
        intent.putExtra("seek_position", mPlayer.getCurrentPosition());
        intent.putExtra("finish", true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}