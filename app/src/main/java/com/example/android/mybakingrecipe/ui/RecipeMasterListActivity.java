package com.example.android.mybakingrecipe.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.android.mybakingrecipe.R;
import com.example.android.mybakingrecipe.data.StepItem;
import com.example.android.mybakingrecipe.utilities.JsonUtils;

import java.util.ArrayList;


public class RecipeMasterListActivity extends AppCompatActivity implements RecipeMasterListFragment.OnItemClickListener, StepFragment.OnItemClickListener {


    //Final Strings to store state information about the Recipe
    public static final String IS_STEP_SHOWING = "is_step_chosen";
    public static final String SAVED_STATE_STEP_NUMBER = "step_number";
    private static RecipeMasterListAdapter mAdapter;
    private static ArrayList<StepItem> mStepsData;
    private static int mClickedStepPosition;
    private static String mStepsJsonData;
    FragmentManager fragmentManager;
    RecipeMasterListFragment mMasterListFragment;
    IngredientsFragment mIngredientsFragment;
    StepFragment mStepFragment;
    private boolean isStepShowing;
    private String[] mReceivedDataStringArray;
    private ProgressBar mLoadingIndicator;
    private boolean mTwoPane;
    private boolean needToDisplayError;
    private FrameLayout ingredientsFrameLayout;
    private FrameLayout stepsFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get data from intent
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {

            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

                mReceivedDataStringArray =
                        intentThatStartedThisActivity.getStringArrayExtra(Intent.EXTRA_TEXT);
                mStepsJsonData = mReceivedDataStringArray[3];
            }
        } else {
            needToDisplayError = true;
        }

        setContentView(R.layout.activity_master_list);

        // Determine if you're creating a two-pane or single-pane display
        if (findViewById(R.id.master_list_container_two_pane) != null) {
            // This LinearLayout will only initially exist in the two-pane tablet case
            mTwoPane = true;
            ingredientsFrameLayout = findViewById(R.id.ingredients_fragment_container);
            stepsFrameLayout = findViewById(R.id.steps_fragment_container);

        } else {
            // We're in single-pane mode and displaying fragments on a phone in separate activities
            mTwoPane = false;
        }

        //Two pane device - tablet
        if (mTwoPane) {
            /*
             * The ProgressBar that will indicate to the user that we are loading data. It will be
             * hidden when no data is loading.
             */
            mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

            new RecipeMasterListActivity.FetchStepsDataTask().execute();


            // Create two-pane interaction
            fragmentManager = getSupportFragmentManager();

            //new Master List Fragment
            mMasterListFragment = new RecipeMasterListFragment();

            //new IngredientsDbHelper Fragment
            mIngredientsFragment = new IngredientsFragment();

            // new Step Fragment
            mStepFragment = new StepFragment();


            //Load saved state if there is one
            boolean mShowStep = false;
            int mStepNumber = 0;
            if (savedInstanceState != null) {

                mShowStep = savedInstanceState.getBoolean(IS_STEP_SHOWING);
                mStepNumber = savedInstanceState.getInt(SAVED_STATE_STEP_NUMBER);
            }

            //In case there is  Saved State of chosen step
            if (mShowStep) {

                ingredientsFrameLayout.setVisibility(View.INVISIBLE);
                stepsFrameLayout.setVisibility(View.VISIBLE);

                String[] mStepArgs = new String[]{
                        //Step Id
                        String.valueOf(mStepNumber),
                        //Step Name
                        mStepsData.get(mStepNumber).getStepName(),
                        // Step Description
                        mStepsData.get(mStepNumber).getDescription(),
                        //Step Video Link
                        mStepsData.get(mStepNumber).getVideoLink()

                };

                mStepFragment = new StepFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                mStepFragment.setStepData(mStepArgs);

                transaction
                        .replace(R.id.steps_fragment_container, mStepFragment)
                        .commit();

                isStepShowing = true;
                mClickedStepPosition = mStepNumber;

            }
            //In case there is no Saved State of chosen step - showing IngredientsDbHelper
            else {

                ingredientsFrameLayout.setVisibility(View.VISIBLE);
                stepsFrameLayout.setVisibility(View.INVISIBLE);
                fragmentManager.beginTransaction()
                        .replace(R.id.ingredients_fragment_container, mIngredientsFragment)
                        .commit();
            }


        }


        // Customized toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle(mReceivedDataStringArray[0] + " Recipe");

        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }


        if (needToDisplayError) RecipeMasterListFragment.setErrorMessageDisplay();
    }

    @Override
    public void onItemSelected(int position, boolean isStepSelected) {


        //IngredientsDbHelper selected
        if (!isStepSelected) {
            //Two-pane device
            if (mTwoPane) {
                isStepShowing = false;
                ingredientsFrameLayout.setVisibility(View.VISIBLE);
                stepsFrameLayout.setVisibility(View.INVISIBLE);
                fragmentManager.beginTransaction().remove(mStepFragment).commit();
                fragmentManager.beginTransaction()
                        .replace(R.id.ingredients_fragment_container, mIngredientsFragment)
                        .commit();

            }

            //Single-pane phone
            else {
                Context context = RecipeMasterListActivity.this;
                Class destinationClass = IngredientsActivity.class;
                Intent intentToStartIngredientsActivity = new Intent(context, destinationClass);
                String[] stringArrayIngredients = RecipeMasterListFragment.getDataStringForIngredients();
                intentToStartIngredientsActivity.putExtra(Intent.EXTRA_TEXT,
                        stringArrayIngredients);
                startActivity(intentToStartIngredientsActivity);
            }

        }
        //One of the steps is selected
        else {
            //Two-pane device
            if (mTwoPane) {
                isStepShowing = true;
                mClickedStepPosition = position;

                ingredientsFrameLayout.setVisibility(View.INVISIBLE);
                stepsFrameLayout.setVisibility(View.VISIBLE);

                String[] mStepArgs;

                mStepArgs = new String[]{
                        //Step Id
                        String.valueOf(position),
                        //Step Name
                        mStepsData.get(position).getStepName(),
                        // Step Description
                        mStepsData.get(position).getDescription(),
                        //Step Video Link
                        mStepsData.get(position).getVideoLink()

                };

                mStepFragment = new StepFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                mStepFragment.setStepData(mStepArgs);

                transaction
                        .replace(R.id.steps_fragment_container, mStepFragment)
                        .commit();


            }

            //Single-pane phone
            else {
                Context context = RecipeMasterListActivity.this;
                Class destinationClass = StepActivity.class;
                Intent intentToStartStepActivity = new Intent(context, destinationClass);
                String[] stringArraySteps = RecipeMasterListFragment.getDataStringForSteps();
                intentToStartStepActivity.putExtra(Intent.EXTRA_TEXT,
                        stringArraySteps);
                startActivity(intentToStartStepActivity);
            }

        }

    }

    public String[] putDataToMasterListFragment() {
        return mReceivedDataStringArray;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        final int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        RecipeMasterListActivity.this.finish();
        startActivity(i);

    }

    @Override
    public void onItemSelected(int positionPrevNext) {

    }

    //Save the current state of this fragment
    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);

        currentState.putBoolean(IS_STEP_SHOWING, isStepShowing);
        currentState.putInt(SAVED_STATE_STEP_NUMBER, mClickedStepPosition);

    }

    public class FetchStepsDataTask extends AsyncTask<Void, Void, ArrayList<StepItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<StepItem> doInBackground(Void... params) {

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
            mStepsData = stepsData;

        }
    }
}
