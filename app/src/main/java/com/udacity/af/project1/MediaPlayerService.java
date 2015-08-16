package com.udacity.af.project1;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

    static final String ACTION_START = "com.example.action.START";
    static final String ACTION_PLAY_PAUSE = "com.example.action.PLAY_PAUSE";
    static final String ACTION_SEEK = "com.example.action.SEEK";
    static final String ACTION_PREVIOUS = "com.example.action.PREVIOUS";
    static final String ACTION_NEXT = "com.example.action.NEXT";
    static final String ACTION_STATS = "com.example.action.STATS";
    static final String ACTION_NOTIFICATION = "com.example.action.NOTIFICATION";

    private ArrayList<Track> mTracks;
    private int mPosition;
    private MediaPlayer mPlayer = null;
    private boolean mIsPrepared = false;
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
        if (mTracks == null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        }
        if (intent == null || intent.getAction() == null) {
            return START_STICKY;
        }
        if (intent.getAction().equals(ACTION_START)) {
            mTracks = intent.getParcelableArrayListExtra("tracks");
            mPosition = intent.getIntExtra("position", 0);
            start();
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
                    updateNotification();
                } else {
                    mPlayer.start();
                    mHandler.removeCallbacks(mUpdateRunnable);
                    mHandler.post(mUpdateRunnable);
                    Intent newIntent = new Intent("PlayerUpdate");
                    newIntent.putExtra("paused", false);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(newIntent);
                    updateNotification();
                }
            }
        } else if (intent.getAction().equals(ACTION_STATS)) {
            sendStats();
        } else if (intent.getAction().equals(ACTION_NOTIFICATION)) {
            updateNotification();
        } else if (intent.getAction().equals(ACTION_PREVIOUS) && mTracks != null) {
            if (mTracks != null) {
                mPosition = mPosition - 1;
                if (mPosition < 0) {
                    mPosition = mTracks.size() - 1;
                }
                start();
                sendStats();
            }
        } else if (intent.getAction().equals(ACTION_NEXT)) {
            if (mTracks != null) {
                mPosition = mPosition + 1;
                if (mPosition >= mTracks.size()) {
                    mPosition = 0;
                }
                start();
                sendStats();
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void start() {
        mIsPrepared = false;
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
        }
        mHandler.removeCallbacks(mUpdateRunnable);
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnSeekCompleteListener(this);
        mPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        String url = mTracks.get(mPosition).getPreviewUrl();
        try {
            mPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.prepareAsync();
        updateNotification();
    }

    public void sendStats() {
        if (mPlayer != null) {
            Intent newIntent = new Intent("PlayerUpdate");
            newIntent.putExtra("track", mTracks.get(mPosition));
            if (mIsPrepared) {
                newIntent.putExtra("duration", mPlayer.getDuration());
            } else {
                newIntent.putExtra("duration", -1);
            }
            newIntent.putExtra("paused", !mPlayer.isPlaying());
            newIntent.putExtra("seek_position", mPlayer.getCurrentPosition());
            LocalBroadcastManager.getInstance(this).sendBroadcast(newIntent);
        }
    }

    public void onPrepared(MediaPlayer player) {
        mIsPrepared = true;
        player.start();
        mHandler.removeCallbacks(mUpdateRunnable);
        sendStats();
        updateNotification();
        mHandler.post(mUpdateRunnable);
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
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        mPosition = mPosition + 1;
        if (mPosition >= mTracks.size()) {
            mPosition = 0;
        }
        start();
        sendStats();
    }

    public void updateNotification() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notificationPref = sharedPref.getBoolean("pref_notification", true);
        if (!notificationPref || mPlayer == null) {
            return;
        }

        final Intent serviceIntent = new Intent(this, MediaPlayerService.class);
        final PendingIntent prevPendingIntent = PendingIntent.getService(this, 0, serviceIntent.setAction(MediaPlayerService.ACTION_PREVIOUS),
                PendingIntent.FLAG_UPDATE_CURRENT);
        final PendingIntent playPausePendingIntent = PendingIntent.getService(this, 0, serviceIntent.setAction(MediaPlayerService.ACTION_PLAY_PAUSE),
                PendingIntent.FLAG_UPDATE_CURRENT);
        final PendingIntent nextPendingIntent = PendingIntent.getService(this, 0, serviceIntent.setAction(MediaPlayerService.ACTION_NEXT),
                PendingIntent.FLAG_UPDATE_CURRENT);

        final HandlerThread thread = new HandlerThread("thread");
        thread.start();
        Handler handler = new Handler(thread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MediaPlayerService.this);
                    notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setSmallIcon(android.R.drawable.ic_media_play)
                            .addAction(android.R.drawable.ic_media_previous, "Previous", prevPendingIntent);
                    if (mPlayer.isPlaying()) {
                        notificationBuilder.addAction(android.R.drawable.ic_media_pause, "Pause", playPausePendingIntent);
                    } else {
                        notificationBuilder.addAction(android.R.drawable.ic_media_play, "Play", playPausePendingIntent);
                    }
                    notificationBuilder.addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent)
                            .setStyle(new NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2))
                            .setContentTitle(mTracks.get(mPosition).getTrackName())
                            .setContentText(mTracks.get(mPosition).getArtistName());
                    Bitmap cover = Picasso.with(MediaPlayerService.this).load(mTracks.get(mPosition).getImageUrlLarge()).get();
                    notificationBuilder.setLargeIcon(cover);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1, notificationBuilder.build());
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    thread.quit();
                }
            }
        });
    }
}