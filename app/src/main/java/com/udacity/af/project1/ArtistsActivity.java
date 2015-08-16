package com.udacity.af.project1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

public class ArtistsActivity extends AppCompatActivity {

    static boolean mTwoPane = false;

    private boolean mNowPlayingVisible = false;
    private ShareActionProvider mShareActionProvider;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("paused")) {
                mNowPlayingVisible = !intent.getBooleanExtra("paused", true);
                invalidateOptionsMenu();
            }
            if (intent.hasExtra("track")) {
                if (mShareActionProvider != null) {
                    Track track = intent.getParcelableExtra("track");
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, String.format("I'm listening to %s by %s %s",
                            track.getTrackName(), track.getArtistName(), track.getUrl()));
                    sendIntent.setType("text/plain");
                    mShareActionProvider.setShareIntent(sendIntent);
                }
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setContentView(R.layout.activity_artists);

        if (findViewById(R.id.fragment_tracks_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_tracks_container, new TracksFragment(), TracksFragment.TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("PlayerUpdate"));
        Intent serviceIntent = new Intent(this, MediaPlayerService.class);
        serviceIntent.setAction(MediaPlayerService.ACTION_STATS);
        startService(serviceIntent);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_now_playing).setVisible(mNowPlayingVisible);
        menu.findItem(R.id.action_share).setVisible(mNowPlayingVisible);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.action_share));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_now_playing) {
            if (ArtistsActivity.mTwoPane) {
                PlayerFragment playerFragment = new PlayerFragment();
                playerFragment.show(getFragmentManager(), "dialog");
            } else {
                Intent intent = new Intent(this, PlayerActivity.class);
                startActivity(intent);
            }
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
