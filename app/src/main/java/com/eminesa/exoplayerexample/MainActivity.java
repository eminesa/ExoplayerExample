package com.eminesa.exoplayerexample;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.eminesa.exoplayerexample.databinding.ActivityMainBinding;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<VideoUrl> videoList;
    public SimpleExoPlayer simpleExoPlayer;
    ActivityMainBinding binding;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        //set activity full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getVideoList();
        getVideoWithUrl();

        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                //control playbackState
                if (playbackState == Player.STATE_BUFFERING) {
                    // when buffering show progress
                    binding.progressBar.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_READY) {
                    //when ready hide progress
                    binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

        clickEvent();
    }

    private void clickEvent() {
        binding.nextImageView.setOnClickListener(v -> {
            //for next video
            simpleExoPlayer.stop();
            if (videoList.size() - 1 == position + 1) {
                position = 0;
            } else {
                position = position + 1;
            }
            // releasePlayer();
            getVideoWithUrl();
        });

        binding.previousImageView.setOnClickListener(v -> {
            simpleExoPlayer.stop();
            if (position != 0) {
                position = position - 1;
            } else {
                position = videoList.size() - 1;
            }
            getVideoWithUrl();
        });

    }

    private void getVideoList() {
        videoList = new ArrayList<>();
       // just second video url have subtitle url
        videoList.add(new VideoUrl("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8", ""));
        videoList.add(new VideoUrl("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8", "http://www.storiesinflight.com/js_videosub/jellies.srt"));
        videoList.add(new VideoUrl("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8", "http://www.storiesinflight.com/js_videosub/jellies.srt"));
        videoList.add(new VideoUrl("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8", ""));
        videoList.add(new VideoUrl("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8", "http://www.storiesinflight.com/js_videosub/jellies.srt"));
        videoList.add(new VideoUrl("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8", ""));
    }


    private void getVideoWithUrl() {
        TrackSelection.Factory adaptiveTrackSelection = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());

        simpleExoPlayer = null;

        //initialize simple exo player
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(this),
                new DefaultTrackSelector(adaptiveTrackSelection),
                new DefaultLoadControl());

        //initialize band width meter
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();

        //initialize extractors factory
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "Exo2"), defaultBandwidthMeter);

        String hls_url = videoList.get(position).getStringUrl();
        String string_url = videoList.get(position).getStringSubtitleUrl();
        Uri uri = Uri.parse(hls_url);
        Uri subTitleUri = Uri.parse(string_url);
        Handler mainHandler = new Handler();

        playWithCaption(uri, subTitleUri, dataSourceFactory);
        // initialize media source
        MediaSource mediaSource = new HlsMediaSource(uri, dataSourceFactory, mainHandler, null);

        // keep screen on
        binding.playerView.setKeepScreenOn(true);
        //set player
        binding.playerView.setPlayer(simpleExoPlayer);
        // play video when ready
        simpleExoPlayer.setPlayWhenReady(true);
        // prepare media
        simpleExoPlayer.prepare(mediaSource);
        //  playWithCaption(uri, subTitleUri);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
    }

    private void releasePlayer() {
        simpleExoPlayer.release();
        simpleExoPlayer = null;
        binding.playerView.setPlayer(null);
    }

    private void playWithCaption(Uri videoUri, Uri subtitleUri, DataSource.Factory dataSourceFactory) {
        //  DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "Exo2"));
        MediaSource contentMediaSource = buildMediaSource(videoUri);
        MediaSource[] mediaSources = new MediaSource[2]; //The Size must change depending on the Uris
        mediaSources[0] = contentMediaSource; // uri

        //Add subtitles
        SingleSampleMediaSource subtitleSource = new SingleSampleMediaSource(subtitleUri, dataSourceFactory,
                Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP, Format.NO_VALUE, "en", null),
                C.TIME_UNSET);

        mediaSources[1] = subtitleSource;

        MediaSource mediaSource = new MergingMediaSource(mediaSources);

        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.setPlayWhenReady(true);
    }

    private MediaSource buildMediaSource(Uri parse) {
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "Exo2"));

        MediaSource mediaSource = new ExtractorMediaSource(parse, dataSourceFactory, new DefaultExtractorsFactory(), new Handler(), null);

        return mediaSource;
    }
}