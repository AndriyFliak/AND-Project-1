package com.udacity.af.project1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TracksActivity extends AppCompatActivity {

    @InjectView(R.id.artist_name) TextView artistName;

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
        setContentView(R.layout.activity_tracks);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.fragment_tracks_container, new TracksFragment()).commit();
        }

        Artist artist = getIntent().getParcelableExtra("artist");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            View actionBarView = getLayoutInflater().inflate(R.layout.tracks_fragment_actionbar, null);
            ButterKnife.inject(this, actionBarView);
            artistName.setText(artist.getName());
            actionBar.setCustomView(actionBarView);
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
