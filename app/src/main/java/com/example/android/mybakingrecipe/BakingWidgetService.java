package com.example.android.mybakingrecipe;


import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.example.android.mybakingrecipe.providers.IngredientsContract;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class BakingWidgetService extends IntentService {
    public static final String ACTION_UPDATE_INGREDIENTS_WIDGET = "com.example.android.mybakingrecipe.action.update_ingredients_widget";
    final static String TAG = BakingWidgetService.class.getSimpleName();

    public BakingWidgetService() {
        super("BakingWidgetService");
    }

    /**
     * Starts this service to perform Update Ingredients action with the given parameters.
     */
    public static void startActionUpdateIngredientsWidget(Context context) {
        Intent intent = new Intent(context, BakingWidgetService.class);
        intent.setAction(ACTION_UPDATE_INGREDIENTS_WIDGET);
        context.startService(intent);

    }

    /**
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_UPDATE_INGREDIENTS_WIDGET.equals(action)) {
                handleActionUpdateIngredientsWidget();
            }
        }
    }


    /**
     * Handle action ACTION_UPDATE_INGREDIENTS_WIDGET in the provided background thread
     */
    private void handleActionUpdateIngredientsWidget() {

        //Query database

        Cursor mCursor;
        boolean mIsDatabaseEmpty;
        String mRecipeName;
        int mIngredientsQuantity;
        int mServingsQuantity;

        String[] projection = {
                IngredientsContract.IngredientsEntry._ID,
                IngredientsContract.IngredientsEntry.COLUMN_RECIPE_NAME,
                IngredientsContract.IngredientsEntry.COLUMN_INGREDIENTS_JSON,
                IngredientsContract.IngredientsEntry.COLUMN_INGREDIENTS_QUANTITY,
                IngredientsContract.IngredientsEntry.COLUMN_SERVINGS_QUANTITY

        };
        mCursor = getContentResolver().query(
                IngredientsContract.IngredientsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
        if (mCursor != null && mCursor.getCount() > 0) {
            mIsDatabaseEmpty = false;
            mCursor.moveToFirst();
            int recipeNameIndex =
                    mCursor.getColumnIndex(IngredientsContract.IngredientsEntry.COLUMN_RECIPE_NAME);
            int ingredientsQuantityIndex =
                    mCursor.getColumnIndex(IngredientsContract.IngredientsEntry.COLUMN_INGREDIENTS_QUANTITY);
            int servingsQuantityIndex =
                    mCursor.getColumnIndex(IngredientsContract.IngredientsEntry.COLUMN_SERVINGS_QUANTITY);


            mRecipeName = mCursor.getString(recipeNameIndex);
            mIngredientsQuantity = mCursor.getInt(ingredientsQuantityIndex);
            mServingsQuantity = mCursor.getInt(servingsQuantityIndex);


        } else {
            mIsDatabaseEmpty = true;
            mRecipeName = "";
            mIngredientsQuantity = 0;
            mServingsQuantity = 0;
        }

        if (mCursor != null) {
            mCursor.close();
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingWidgetProvider.class));

        //Trigger data update to handle the GridView widgets and force a data refresh

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);


        //Now update all widgets
        BakingWidgetProvider.updateIngredientsWidget(this, appWidgetManager, appWidgetIds,
                mIsDatabaseEmpty, mRecipeName, mIngredientsQuantity, mServingsQuantity);
    }
}

