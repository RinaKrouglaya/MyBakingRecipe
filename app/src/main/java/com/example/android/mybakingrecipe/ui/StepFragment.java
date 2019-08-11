package com.example.android.mybakingrecipe.ui;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mybakingrecipe.R;
import com.example.android.mybakingrecipe.data.StepItem;
import com.example.android.mybakingrecipe.utilities.JsonUtils;
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
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class StepFragment extends Fragment implements View.OnClickListener, ExoPlayer.EventListener {

    //Final Strings to store state information about the Step
    public static final String SAVED_STEP_DATA = "step_data";
    private static final String TAG = StepActivity.class.getSimpleName();
    private static ProgressBar mLoadingIndicator;
    private static MediaSessionCompat mMediaSession;
    private static ArrayList<StepItem> mButtonData;
    private static int anotherStepId;
    private static String mStepsJsonData;

    //Listener that triggers a callback in the host activity
    // It contains information about which position on the list the user has clicked
    StepFragment.OnItemClickListener mFragmentCallback;
    private String[] mReceivedDataStringArray;
    private SimpleExoPlayerView mPlayerView;
    private PlaybackStateCompat.Builder mStateBuilder;
    private SimpleExoPlayer mExoPlayer;
    private TextView mNoVideoAvailableTextView;
    private NotificationManager mNotificationManager;
    private boolean mExoPlayerIsInitialized;
    private String mRecipeNumber;
    private int mStepId;
    private String mStepName;
    private String mStepDescription;
    private String mVideoLink;
    private String mRecipeName;
    private String mIngredientsJsonData;
    private String mIngredientsQuantityString;
    private String mStepsQuantityString;
    private String mServingsQuantityString;
    private boolean mIsSinglePane;
    private String[] mStepData;

    //Constructor
    public StepFragment() {
        super();
    }

    //Override OnAttach to make sure that the container activity has implemented the callback

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //This makes sure that the host activity is implementing the callback
        //If not - throws an exception
        try {
            mFragmentCallback = (StepFragment.OnItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement OnItemClickListener");
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_step, container, false);

        // Error Message - "No video available" Text View
        mNoVideoAvailableTextView = rootView.findViewById(R.id.no_video_available);


        //Get data from intent

        Intent intentThatStartedThisActivity = getActivity().getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                mReceivedDataStringArray =
                        intentThatStartedThisActivity.getStringArrayExtra(Intent.EXTRA_TEXT);

                //0 Recipe name
                mRecipeName = mReceivedDataStringArray[0];
                //1 IngredientsDbHelper Json data
                mIngredientsJsonData = mReceivedDataStringArray[1];
                //2 IngredientsDbHelper Quantity String
                mIngredientsQuantityString = mReceivedDataStringArray[2];
                //3 Steps Json Data
                mStepsJsonData = mReceivedDataStringArray[3];
                //4 Steps Quantity String
                mStepsQuantityString = mReceivedDataStringArray[4];
                //5 Servings Quantity String
                mServingsQuantityString = mReceivedDataStringArray[5];

                //Single pane
                if (mReceivedDataStringArray.length > 6) {

                    mIsSinglePane = true;

                    //6 Step ID
                    mStepId = Integer.parseInt(mReceivedDataStringArray[6]);
                    //7 Step name
                    mStepName = mReceivedDataStringArray[7];
                    //8 Step description
                    mStepDescription = mReceivedDataStringArray[8];
                    //9  Video link
                    mVideoLink = mReceivedDataStringArray[9];
                } else
                //Two-pane
                {

                    mIsSinglePane = false;

                    //Load saved state if there is one (Step Data)
                    if (savedInstanceState != null) {
                        mStepData = savedInstanceState.getStringArray(SAVED_STEP_DATA);

                    }


                    //6 Step ID
                    if (mStepData != null) mStepId = Integer.parseInt(mStepData[0]);
                    //7 Step name
                    mStepName = mStepData[1];
                    //8 Step description
                    mStepDescription = mStepData[2];
                    //9  Video link
                    mVideoLink = mStepData[3];
                }
            }
        } else showErrorMessage();

        /*
         * Initializing
         */

        // Progress Bar
        mLoadingIndicator = rootView.findViewById(R.id.pb_loading_indicator);
        // Media Player View
        mPlayerView = rootView.findViewById(R.id.video_player_view);

        // Instructions Text View
        TextView mInstructionsTextView = rootView.findViewById(R.id.step_instruction);
        if (mInstructionsTextView != null) mInstructionsTextView.setText(mStepDescription);

        NestedScrollView mInstructionScrollView = rootView.findViewById(R.id.text_container);
        if (mInstructionScrollView != null) mInstructionScrollView.setScrollbarFadingEnabled(false);


        // Button to previous step
        Button mPrevButton = rootView.findViewById(R.id.button_previous_step);
        if (mIsSinglePane) {
            clickedButton(mPrevButton, "previous");
        } else mPrevButton.setVisibility(View.INVISIBLE);

        // Button to next step
        Button mNextButton = rootView.findViewById(R.id.button_next_step);
        if (mIsSinglePane) {
            clickedButton(mNextButton, "next");
        } else mNextButton.setVisibility(View.INVISIBLE);

        // Media Session
        initializeMediaSession();
        // The Player
        if (mVideoLink.isEmpty()) {
            mExoPlayerIsInitialized = false;
            showErrorMessage();
        } else {
            mExoPlayerIsInitialized = true;
            initializePlayer(Uri.parse(mVideoLink));
        }


        return rootView;
    }

    void clickedButton(Button button, final String key) {

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (key.equals("previous")) {
                    anotherStepId = mStepId - 1;
                    mFragmentCallback.onItemSelected(1);
                }
                if (key.equals("next")) {
                    anotherStepId = mStepId + 1;
                    mFragmentCallback.onItemSelected(2);
                }
                if ((anotherStepId == (-1)) || (anotherStepId == Integer.parseInt(mStepsQuantityString))) {

                    Toast.makeText(getContext(), getString(R.string.no_more_steps),
                            Toast.LENGTH_SHORT).show();
                } else {
                    StepFragment.FetchStepsDataTask myAsyncTask = new StepFragment.FetchStepsDataTask();
                    myAsyncTask.execute();

                }
            }
        });

    }

    private void openingAnotherStep() {

        Context context = getContext();
        Class destinationClass = StepActivity.class;
        Intent intentToStartAnotherStepActivity = new Intent(context, destinationClass);

        if (!(mButtonData == null)) {
            String[] buttonStringArray = {

                    //0
                    mRecipeName,
                    //1
                    mIngredientsJsonData,
                    //2
                    mIngredientsQuantityString,
                    //3
                    mStepsJsonData,
                    //4
                    mStepsQuantityString,
                    //5
                    mServingsQuantityString,
                    //6
                    String.valueOf(mButtonData.get(anotherStepId).getStepId()),
                    //7
                    mButtonData.get(anotherStepId).getStepName(),
                    //8
                    mButtonData.get(anotherStepId).getDescription(),
                    //9
                    mButtonData.get(anotherStepId).getVideoLink()

            };

            if (mExoPlayerIsInitialized) {
                releasePlayer();
                mMediaSession.setActive(false);
                mExoPlayerIsInitialized = false;
            }
            intentToStartAnotherStepActivity.putExtra(Intent.EXTRA_TEXT, buttonStringArray);
            startActivity(intentToStartAnotherStepActivity);

        } else Toast.makeText(getContext(), getString(R.string.not_available),
                Toast.LENGTH_SHORT).show();


    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), TAG);

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
        mMediaSession.setCallback(new StepFragment.MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();

            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.

            mExoPlayer.addListener(StepFragment.this);

            // Prepare the MediaSource.

            String userAgent = Util.getUserAgent(getContext(), "MyBaking");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {

        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    /**
     * Release the player when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mExoPlayerIsInitialized) {
            releasePlayer();
            mMediaSession.setActive(false);
            mExoPlayerIsInitialized = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExoPlayerIsInitialized) {
            releasePlayer();
            mMediaSession.setActive(false);
            mExoPlayerIsInitialized = false;
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }


    // ExoPlayer Event Listeners

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    /**
     * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
     * PlayBackState to keep in sync, and post the media notification.
     *
     * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
     * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
     *                      STATE_BUFFERING, or STATE_ENDED.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity() {
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * This method will make the View for the Media Player visible and
     * hide the error message.
     */
    private void showMediaPlayerView() {
        /* First, make sure the error is invisible */
        mNoVideoAvailableTextView.setVisibility(View.INVISIBLE);
        /* Then, make sure the baking data is visible */
        mPlayerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the Media Player view.
     */
    private void showErrorMessage() {

        mPlayerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mNoVideoAvailableTextView.setVisibility(View.VISIBLE);
    }

    //Setter method
    public void setStepData(String[] data) {
        mStepData = data;
    }

    //Save the current state of this fragment
    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putStringArray(SAVED_STEP_DATA, mStepData);

    }

    //OnImageClickListener interface, calls a method in the host activity named onItemSelected
    public interface OnItemClickListener {
        void onItemSelected(int positionPrevNext);

    }

    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

    public class FetchStepsDataTask extends AsyncTask<Integer, Void, ArrayList<StepItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<StepItem> doInBackground(Integer... params) {

            try {
                ArrayList<StepItem> jsonStepsData = JsonUtils
                        .getListOfStepItemsFromJson(mStepsJsonData);

                return jsonStepsData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<StepItem> stepsData) {

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mButtonData = stepsData;
            openingAnotherStep();
        }
    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }
}
