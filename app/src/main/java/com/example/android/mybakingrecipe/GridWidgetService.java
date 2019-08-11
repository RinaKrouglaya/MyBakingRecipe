package com.example.android.mybakingrecipe;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.mybakingrecipe.data.IngredientItem;
import com.example.android.mybakingrecipe.providers.IngredientsContract;
import com.example.android.mybakingrecipe.utilities.JsonUtils;

import org.json.JSONException;

import java.util.ArrayList;


public class GridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new GridRemoteViewsFactory(this.getApplicationContext());
    }
}

class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Cursor mCursor;
    private ArrayList<IngredientItem> mIngredientsData;


    public GridRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;


    }

    @Override
    public void onCreate() {


    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        // Get  ingredients info from the database

        if (mCursor != null) mCursor.close();

        String[] projection = {
                IngredientsContract.IngredientsEntry._ID,
                IngredientsContract.IngredientsEntry.COLUMN_RECIPE_NAME,
                IngredientsContract.IngredientsEntry.COLUMN_INGREDIENTS_JSON,
                IngredientsContract.IngredientsEntry.COLUMN_INGREDIENTS_QUANTITY,
                IngredientsContract.IngredientsEntry.COLUMN_SERVINGS_QUANTITY

        };


        mCursor = mContext.getContentResolver().query(
                IngredientsContract.IngredientsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );


    }

    @Override
    public void onDestroy() {
        if (mCursor != null) mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor == null || mCursor.getCount() == 0) return 0;
        mCursor.moveToFirst();
        int quantityDataIndex =
                mCursor.getColumnIndex(IngredientsContract.IngredientsEntry.COLUMN_INGREDIENTS_QUANTITY);
        int ingredientsQuantityFromDatabase = mCursor.getInt(quantityDataIndex);
        return ingredientsQuantityFromDatabase;
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the GridView to be displayed
     * @return The RemoteViews object to display for the provided position
     */
    @Override
    public RemoteViews getViewAt(int position) {

        if (mCursor == null || mCursor.getCount() == 0) return null;

        mCursor.moveToFirst();
        int ingredientsDataIndex =
                mCursor.getColumnIndex(IngredientsContract.IngredientsEntry.COLUMN_INGREDIENTS_JSON);
        String ingredientsDataFromJson = mCursor.getString(ingredientsDataIndex);

        //Allowed on the main thread, because the list of ingredients is not a big data
        try {
            ArrayList<IngredientItem> ingredientsData = JsonUtils
                    .getListOfIngredientItemsFromJson(ingredientsDataFromJson);
            if (ingredientsData == null) {
                IngredientItem ingredientItemErrorObject =
                        new IngredientItem(1,
                                0,
                                "",
                                mContext.getString(R.string.no_ingredients_error));

                ingredientsData = new ArrayList<>();
                ingredientsData.add(ingredientItemErrorObject);
                mIngredientsData = ingredientsData;

            } else {

                mIngredientsData = ingredientsData;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RemoteViews row = new RemoteViews(mContext.getPackageName(), R.layout.ingredients_list_item);

        row.setTextViewText(R.id.ingredients_item_ingredient_name, mIngredientsData.get(position).getName());

        String quantityString = String.valueOf(mIngredientsData.get(position).getQuantity());
        if (quantityString.endsWith(".0")) {
            quantityString = quantityString.replace(".0", "");
        }
        row.setTextViewText(R.id.ingredients_item_ingredient_quantity, quantityString);

        row.setTextViewText(R.id.ingredients_item_ingredient_measure, mIngredientsData.get(position).getMeasure());


        return row;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the GridView the same
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


}

