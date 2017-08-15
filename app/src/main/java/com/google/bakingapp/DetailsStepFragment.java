package com.google.bakingapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.bakingapp.Model.Steps;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsStepFragment extends Fragment implements ExoPlayer.EventListener {
    private Steps steps;
    private ArrayList<Steps> mlist;
    private TextView txtDescription, txtNoVideo;
    private SimpleExoPlayerView mStepsPlayerView;
    private SimpleExoPlayer mStepsExoPlayer;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private long currentPlayingPosition;


    private static final String TAG = DetailsStepFragment.class.getSimpleName();

    public DetailsStepFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details_step, container, false);

        steps = getArguments().getParcelable("step");

        txtDescription = (TextView) view.findViewById(R.id.step_description);
        txtNoVideo = (TextView) view.findViewById(R.id.step_no_video);
        txtDescription.setText(steps.getDescription());
        mStepsPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.step_video);
        initializeMediaSession();

        if (steps.getVideoURL() != null && !steps.getVideoURL().isEmpty()) {
            txtNoVideo.setVisibility(View.INVISIBLE);
            Uri uri = Uri.parse(steps.getVideoURL());
            initializeMediaPlayer(uri);
        } else {
            txtNoVideo.setVisibility(View.VISIBLE);
        }
        return view;
    }

    public static DetailsStepFragment newInstance(Steps step) {
        Bundle arguments = new Bundle();
        arguments.putParcelable("step", step);
        DetailsStepFragment fragment = new DetailsStepFragment();
        fragment.setArguments(arguments);
        return fragment;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mStepsExoPlayer != null) {
            currentPlayingPosition = mStepsExoPlayer.getCurrentPosition();
            outState.putLong("land_play", currentPlayingPosition);
        }
    }

    public void initializeMediaPlayer(Uri mediaUri) {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        // Create an instance of the ExoPlayer.
        if (mStepsExoPlayer == null) {
            // If player is null or Uri just doesn't tally with previous ones

            LoadControl loadControl = new DefaultLoadControl();

            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
            //  Track Selector
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            mStepsExoPlayer =
                    ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mStepsPlayerView.setPlayer(mStepsExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mStepsExoPlayer.addListener(this);
            // Prepare the MediaSource.

            String userAgent = Util.getUserAgent(getActivity(), "BakingApp");

            MediaSource videoSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);


            mStepsExoPlayer.prepare(videoSource);
            mStepsExoPlayer.setPlayWhenReady(true);

        }
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getActivity(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }


    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mStepsExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mStepsExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mStepsExoPlayer.seekTo(0);
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (mStepsExoPlayer != null)
            if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
                mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                        mStepsExoPlayer.getCurrentPosition(), 1f);
            } else if ((playbackState == ExoPlayer.STATE_READY)) {
                mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                        mStepsExoPlayer.getCurrentPosition(), 1f);
            }
        mMediaSession.setPlaybackState(mStateBuilder.build());


    }

    private void releasePlayer() {
        if (mStepsExoPlayer != null) {
            mStepsExoPlayer.stop();
            mStepsExoPlayer.release();
            mStepsExoPlayer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mStepsPlayerView.getOverlayFrameLayout().removeAllViews();
        mMediaSession.setActive(false);

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

}
