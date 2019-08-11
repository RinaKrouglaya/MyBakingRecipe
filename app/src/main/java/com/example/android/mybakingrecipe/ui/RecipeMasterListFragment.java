package com.example.android.mybakingrecipe.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.mybakingrecipe.R;
import com.example.android.mybakingrecipe.data.StepItem;
import com.example.android.mybakingrecipe.utilities.JsonUtils;

import java.util.ArrayList;
import java.util.Objects;

import static android.support.v7.widget.RecyclerView.VERTICAL;

public class RecipeMasterListFragment extends Fragment implements RecipeMasterListAdapter.ListItemClickListener {


    private static RecipeMasterListAdapter mAdapter;
    private static RecyclerView mRecipeMasterListRecyclerVew;
    private static ArrayList<StepItem> mStepsData;
    private static String[] mReceivedDataStringArray;
    private static ProgressBar mLoadingIndicator;
    private static TextView mErrorMessageDisplay;
    private static String mRecipeName;
    private static String mIngredientsJsonData;
    private static String mIngredientsQuantityString;
    private static String mStepsJsonData;
    private static String mStepsQuantityString;
    private static String mServingsQuantityString;
    private static String[] stepsStringArray;
    private static LinearLayout ingredientsTextContainer;
    //Listener that triggers a callback in the host activity
    // It contains information about which position on the list the user has clicked
    OnItemClickListener mCallback;
    private int mStepsQuantity;
    private boolean isStepClicked;

    //Constructor
    public RecipeMasterListFragment() {
        super();

    }

    //Override OnAttach to make sure that the container activityhas implemented the callback

    public static String[] getDataStringForIngredients() {

        String[] stringArrayIngredients =
                //0
                {mRecipeName,
                        //1
                        mIngredientsJsonData,
                        //2
                        mIngredientsQuantityString,
                        //3
                        mStepsJsonData,
                        //4
                        mStepsQuantityString,
                        //5
                        mServingsQuantityString};

        return stringArrayIngredients;
    }

    public static String[] getDataStringForSteps() {


        return stepsStringArray;
    }

    // Setter Method to show Error message
    public static void setErrorMessageDisplay() {
        /* First, hide the currently visible data */
        mRecipeMasterListRecyclerVew.setVisibility(View.INVISIBLE);
        ingredientsTextContainer.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the View for the Recipe Master List data visible and
     * hide the error message.
     */
    private static void showRecipeMasterListDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the baking data is visible */
        mRecipeMasterListRecyclerVew.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //This makes sure that the host activity is implementing the callback
        //If not - throws an exception
        try {
            mCallback = (OnItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement OnItemClickListener");
        }
    }


    //Getter methods to send data further:   array of Strings

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_master_list, container, false);

        RecipeMasterListActivity hostActivity = (RecipeMasterListActivity) getActivity();
        if (hostActivity != null) {
            mReceivedDataStringArray = hostActivity.putDataToMasterListFragment();

        }

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         */
        mLoadingIndicator = rootView.findViewById(R.id.pb_loading_indicator);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = rootView.findViewById(R.id.no_connection_view);

        //RecyclerView
        mRecipeMasterListRecyclerVew = rootView.findViewById(R.id.rv_recipe_master_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecipeMasterListRecyclerVew.setLayoutManager(layoutManager);
        mRecipeMasterListRecyclerVew.setHasFixedSize(false);

        if (mReceivedDataStringArray != null) {

            //0 Recipe name
            mRecipeName = mReceivedDataStringArray[0];
            //1 IngredientsDbHelper Json Data
            mIngredientsJsonData = mReceivedDataStringArray[1];
            //2  IngredientsDbHelper quantity String
            mIngredientsQuantityString = mReceivedDataStringArray[2];
            //3 Steps Json data
            mStepsJsonData = mReceivedDataStringArray[3];
            //4 Steps quantity
            mStepsQuantity = Integer.valueOf(mReceivedDataStringArray[4]);
            mStepsQuantityString = mReceivedDataStringArray[4];
            //5 Number of servings String
            mServingsQuantityString = mReceivedDataStringArray[5];
        }

        //Customized Divider

        DividerItemDecoration itemDecor = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            itemDecor = new DividerItemDecoration(Objects.requireNonNull(getContext()), VERTICAL);
            Drawable mDivider = ContextCompat.getDrawable(getContext(), R.drawable.divider);

            itemDecor.setDrawable(Objects.requireNonNull(mDivider));
            mRecipeMasterListRecyclerVew.addItemDecoration(itemDecor);
        }

        mAdapter = new RecipeMasterListAdapter(mStepsQuantity, this);
        mRecipeMasterListRecyclerVew.setAdapter(mAdapter);

        // Get Steps Names from JSON
        new FetchStepsNamesTask().execute(mStepsJsonData);


        ingredientsTextContainer = rootView.findViewById(R.id.fragment_master_list_ingredients_container);
        ingredientsTextContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStepClicked = false;
                mCallback.onItemSelected(-1, isStepClicked);
            }
        });

        return rootView;
    }

    /**
     * This callback is invoked when you click on an item in the list .
     *
     * @param clickedItemIndex Index in the list of the item that was clicked.
     **/
    @Override
    public void onListItemClick(int clickedItemIndex) {
        isStepClicked = true;
        if (mStepsData != null) {
            stepsStringArray = new String[]{

                    //0
                    mRecipeName,
                    //1
                    mIngredientsJsonData,
                    //2
                    mIngredientsQuantityString,
                    //3
                    mStepsJsonData,
                    //4
                    String.valueOf(mStepsQuantity),
                    //5
                    mServingsQuantityString,
                    //6
                    String.valueOf(mStepsData.get(clickedItemIndex).getStepId()),
                    //7
                    mStepsData.get(clickedItemIndex).getStepName(),
                    //8
                    mStepsData.get(clickedItemIndex).getDescription(),
                    //9
                    mStepsData.get(clickedItemIndex).getVideoLink()
            };
            mCallback.onItemSelected(clickedItemIndex, isStepClicked);

        } else setErrorMessageDisplay();

    }

    //OnImageClickListener interface, calls a method in the host activity named onItemSelected
    public interface OnItemClickListener {
        void onItemSelected(int position, boolean isStepClicked);

    }

    public static class FetchStepsNamesTask extends AsyncTask<String, Void, ArrayList<StepItem>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<StepItem> doInBackground(String... params) {

            try {

                String jsonStepsString = params[0];

                ArrayList<StepItem> jsonStepsData = JsonUtils
                        .getListOfStepItemsFromJson(jsonStepsString);

                return jsonStepsData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<StepItem> stepsData) {

            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (stepsData == null) {
                setErrorMessageDisplay();

            } else {
                showRecipeMasterListDataView();
                mAdapter.setMasterListRecipeData(stepsData);
                mStepsData = stepsData;
            }
        }

    }
}
