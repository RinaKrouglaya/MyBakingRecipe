package com.example.android.mybakingrecipe.ui;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.mybakingrecipe.AppExecutors;
import com.example.android.mybakingrecipe.BakingWidgetService;
import com.example.android.mybakingrecipe.R;
import com.example.android.mybakingrecipe.data.IngredientItem;
import com.example.android.mybakingrecipe.utilities.JsonUtils;
import com.getbase.floatingactionbutton.AddFloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

import static android.support.v7.widget.RecyclerView.VERTICAL;
import static com.example.android.mybakingrecipe.providers.IngredientsContract.IngredientsEntry;

public class IngredientsFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {


    private static IngredientsAdapter mAdapter;
    private static ArrayList<IngredientItem> mIngredientsData;
    private static ProgressBar mLoadingIndicator;
    private static TextView mErrorMessageDisplay;
    private String mRecipeName;
    private String mIngredientsJsonData;
    private int mIngredientsQuantity;
    private String mStepsJsonData;
    private String mIngredientsQuantityString;
    private String mServingsQuantityString = "unknown";
    private int mServingsQuantity;
    private String mStepsQuantityString;
    private int mStepsQuantity;
    private Bundle mReceivedBundle;
    private boolean mIsAddedToWidget;
    private RecyclerView mIngredientsRecyclerVew;
    private String[] mReceivedDataStringArray;
    private RelativeLayout mDataContainer;

    //Constructor
    public IngredientsFragment() {
        super();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);
        AddFloatingActionButton mAddToWidgetButton = rootView.findViewById(R.id.fab_add_to_widget);


        mReceivedDataStringArray = getActivity().getIntent().getStringArrayExtra(Intent.EXTRA_TEXT);



        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         */
        mLoadingIndicator = rootView.findViewById(R.id.pb_loading_indicator);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = rootView.findViewById(R.id.empty_view);

        /* This view contains all the visible data and should be hidden when error message is shown*/

        mDataContainer = rootView.findViewById(R.id.data_container);


        if (this.getArguments() != null) {
            mReceivedDataStringArray = mReceivedBundle.getStringArray("ingredients");

        }


        if (mReceivedDataStringArray != null) {

            //0 Recipe name
            mRecipeName = mReceivedDataStringArray[0];
            //1 IngredientsDbHelper Json Data
            mIngredientsJsonData = mReceivedDataStringArray[1];
            //2  IngredientsDbHelper quantity
            mIngredientsQuantity = Integer.parseInt(mReceivedDataStringArray[2]);
            mIngredientsQuantityString = mReceivedDataStringArray[2];
            //3 Steps Json data
            mStepsJsonData = mReceivedDataStringArray[3];
            //4 Steps quantity String
            mStepsQuantityString = mReceivedDataStringArray[4];
            //5 Number of servings String
            mServingsQuantityString = mReceivedDataStringArray[5];
            mServingsQuantity = Integer.parseInt(mReceivedDataStringArray[5]);

        } else {

            showErrorMessage();
        }

        /*
         * Initializing
         */

        //RecyclerView
        mIngredientsRecyclerVew = rootView.findViewById(R.id.rv_ingredients);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mIngredientsRecyclerVew.setLayoutManager(layoutManager);
        mIngredientsRecyclerVew.setHasFixedSize(false);


        //Customized Divider
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            DividerItemDecoration itemDecor = null;
            itemDecor = new DividerItemDecoration(Objects.requireNonNull(getContext()), VERTICAL);
            Drawable mDivider = ContextCompat.getDrawable(getContext(), R.drawable.divider);
            if (mDivider != null) {
                itemDecor.setDrawable(mDivider);
            }
            mIngredientsRecyclerVew.addItemDecoration(itemDecor);
        }


        mAdapter = new IngredientsAdapter(mIngredientsQuantity);
        mIngredientsRecyclerVew.setAdapter(mAdapter);


        new IngredientsFragment.FetchIngredientsNamesTask().execute(mIngredientsJsonData);


        TextView servingsTextView = rootView.findViewById(R.id.servings_text);
        String servingsString = "Ingredients for " + mServingsQuantityString + " servings:";
        servingsTextView.setText(servingsString);


        mAddToWidgetButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_view);

                Button dialogButton = dialog.findViewById(R.id.close_button);
                // if button is clicked, close the custom dialog

                dialogButton.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                final CheckBox checkBox = dialog.findViewById(R.id.dialog_checkbox);


                //Add and remove from the database by clicking the checkbox

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {


                        String[] projection = {
                                IngredientsEntry._ID,
                                IngredientsEntry.COLUMN_RECIPE_NAME,
                                IngredientsEntry.COLUMN_INGREDIENTS_JSON,
                                IngredientsEntry.COLUMN_INGREDIENTS_QUANTITY,
                                IngredientsEntry.COLUMN_SERVINGS_QUANTITY

                        };


                        String[] selectionArgs = new String[]{mRecipeName};
                        final Cursor mCurrentCursor = getContext().
                                getContentResolver().query(IngredientsEntry.CONTENT_URI,
                                projection,
                                "recipeName =?",
                                selectionArgs,
                                null);


                        if ((mCurrentCursor == null || mCurrentCursor.getCount() < 1)) {
                            mIsAddedToWidget = false;
                            checkBox.setChecked(false);

                        } else {

                            mIsAddedToWidget = true;
                            checkBox.setChecked(true);

                        }

                        if (mCurrentCursor != null) {
                            mCurrentCursor.close();
                        }


                    }
                });

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (mIsAddedToWidget) {

                            int rowsDeleted = getActivity().getContentResolver()
                                    .delete(IngredientsEntry
                                                    .CONTENT_URI,
                                            null,
                                            null);

                            mIsAddedToWidget = false;
                            BakingWidgetService.startActionUpdateIngredientsWidget(getContext());


                        } else {

                            int rowsDeleted = getActivity().getContentResolver()
                                    .delete(IngredientsEntry
                                                    .CONTENT_URI,
                                            null,
                                            null);

                            ContentValues values = new ContentValues();

                            values.put(IngredientsEntry.COLUMN_RECIPE_NAME, mRecipeName);
                            values.put(IngredientsEntry.COLUMN_INGREDIENTS_JSON, mIngredientsJsonData);
                            values.put(IngredientsEntry.COLUMN_INGREDIENTS_QUANTITY, mIngredientsQuantity);
                            values.put(IngredientsEntry.COLUMN_SERVINGS_QUANTITY, mServingsQuantity);


                            Uri newUri = getActivity().getContentResolver().insert(IngredientsEntry.CONTENT_URI, values);


                            mIsAddedToWidget = true;
                            BakingWidgetService.startActionUpdateIngredientsWidget(getContext());
                        }
                    }

                });

                dialog.show();
            }
        });

        return rootView;
    }

    @NonNull
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                IngredientsEntry._ID,
                IngredientsEntry.COLUMN_RECIPE_NAME,
                IngredientsEntry.COLUMN_INGREDIENTS_JSON,
                IngredientsEntry.COLUMN_INGREDIENTS_QUANTITY,
                IngredientsEntry.COLUMN_SERVINGS_QUANTITY
        };

        return new android.support.v4.content.CursorLoader(getContext(),
                IngredientsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {

        cursor.moveToFirst();
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    }

    /**
     * This method will make the View for the baking data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showRecipeIngredientsDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the baking data is visible */
        mDataContainer.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mDataContainer.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /* Setter methods */
    public void setIngredientsData(Bundle receivedBundle) {
        mReceivedBundle = receivedBundle;

    }

    public class FetchIngredientsNamesTask extends AsyncTask<String, Void, ArrayList<IngredientItem>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<IngredientItem> doInBackground(String... params) {

            try {

                String jsonStepsString = params[0];

                ArrayList<IngredientItem> jsonIngredientsData = JsonUtils
                        .getListOfIngredientItemsFromJson(jsonStepsString);

                return jsonIngredientsData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<IngredientItem> ingredientsData) {

            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (ingredientsData == null) {
                showErrorMessage();


            } else {

                showRecipeIngredientsDataView();
                mAdapter.setIngredientData(ingredientsData);
                mIngredientsData = ingredientsData;
            }
        }

    }

}
