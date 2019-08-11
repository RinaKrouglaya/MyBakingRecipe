package com.example.android.mybakingrecipe.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.mybakingrecipe.R;
import com.example.android.mybakingrecipe.data.RecipeItem;
import com.example.android.mybakingrecipe.utilities.JsonUtils;
import com.example.android.mybakingrecipe.utilities.NetworkUtils;
import com.example.android.mybakingrecipe.utilities.SimpleIdlingResource;

import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MainBakingAdapter.ListItemClickListener {

    static ArrayList<RecipeItem> globalBakingData;
    private static int mainRecipesNumberListItems;
    private static MainBakingAdapter mAdapter;
    private static RecyclerView mMainBakingRV;
    private static ProgressBar mLoadingIndicator;
    private static TextView mErrorMessageDisplay;
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    //Tablet or phone size
    private boolean mTabletSize;

    /**
     * This method will make the View for the baking data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private static void showBakingDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the baking data is visible */
        mMainBakingRV.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the baking
     * View.
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private static void showErrorMessage() {
        mMainBakingRV.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Tablet or phone size
        mTabletSize = findViewById(R.id.main_image_tablet) != null;

        /* Customized toolbar */
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         */
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);




        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */

        mMainBakingRV = findViewById(R.id.rv_baking_main);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = findViewById(R.id.empty_view);


        // Two pane device - tablet
        if (mTabletSize) {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 3);
            mMainBakingRV.setLayoutManager(layoutManager);

        }

        // Single pane device - phone
        else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            mMainBakingRV.setLayoutManager(layoutManager);
        }


        mMainBakingRV.setHasFixedSize(false);

        /*
        Fetching the quantity of baking recipes from JSON, initializing and setting the adapter
        using that quantity
        Non static class
         */
        //Set resources not idle as fetching will start
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }
        new FetchRecipesQuantityTask().execute();


        // Get Recipe Quantity from JSON
        new FetchRecipeTitlesTask().execute();
    }

    /**
     * This callback is invoked when you click on an item in the list
     *
     * @param clickedItemIndex Index in the list of the item that was clicked.
     */
    @Override
    public void onListItemClick(int clickedItemIndex) {

        Context context = this;
        Class destinationClass = RecipeMasterListActivity.class;
        Intent intentToStartRecipeMasterListActivity = new Intent(context, destinationClass);


        if (globalBakingData != null) {
            String[] stringArray = {

                    //0
                    globalBakingData.get(clickedItemIndex).getRecipeName(),
                    //1
                    globalBakingData.get(clickedItemIndex).getIngredientsJsonData(),
                    //2
                    String.valueOf(globalBakingData.get(clickedItemIndex).getIngredientsQuantity()),
                    //3
                    globalBakingData.get(clickedItemIndex).getStepsJsonData(),
                    //4
                    String.valueOf(globalBakingData.get(clickedItemIndex).getStepsQuantity()),
                    //5
                    String.valueOf(globalBakingData.get(clickedItemIndex).getServingsNumber())};

            intentToStartRecipeMasterListActivity.putExtra(Intent.EXTRA_TEXT, stringArray);
            startActivity(intentToStartRecipeMasterListActivity);
        } else showErrorMessage();

    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    public class FetchRecipesQuantityTask extends AsyncTask<Void, Void, Integer> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            URL recipeRequestUrl = NetworkUtils.buildUrl();

            try {
                String jsonRecipeResponse = NetworkUtils
                        .getResponseFromHttpUrl(recipeRequestUrl);


                int jsonRecipeQuantity = JsonUtils
                        .getRecipeQuantityFromJson(jsonRecipeResponse);

                return jsonRecipeQuantity;

            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }

        @Override
        protected void onPostExecute(Integer recipeQuantity) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            //Set resources as idle at the end of the operations
            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(true);
            }


            if (recipeQuantity == 0) {
                showErrorMessage();

            } else {

                mainRecipesNumberListItems = recipeQuantity;
                mAdapter = new MainBakingAdapter(mainRecipesNumberListItems, MainActivity.this);
                mMainBakingRV.setAdapter(mAdapter);
            }
        }

    }


// Testing

    public class FetchRecipeTitlesTask extends AsyncTask<Void, Void, ArrayList<RecipeItem>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<RecipeItem> doInBackground(Void... voids) {
            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();


            URL recipeRequestUrl = NetworkUtils.buildUrl();

            try {
                String jsonRecipeResponse = NetworkUtils
                        .getResponseFromHttpUrl(recipeRequestUrl);

                ArrayList<RecipeItem> jsonRecipeData = JsonUtils
                        .getListOfRecipeItemsFromJson(jsonRecipeResponse);

                return jsonRecipeData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<RecipeItem> bakingData) {

            mLoadingIndicator.setVisibility(View.INVISIBLE);

            //Set resources as idle at the end of the operations
            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(true);
            }

            if (bakingData == null) {
                showErrorMessage();

            } else {

                showBakingDataView();
                mAdapter.setBakingData(bakingData);
                globalBakingData = bakingData;
            }
        }

    }
}
