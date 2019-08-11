package com.example.android.mybakingrecipe;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android.mybakingrecipe.ui.MainActivity;


/**
 * Implementation of App Widget functionality.
 */
public class BakingWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, boolean isDatabaseEmpty, String recipeName, int ingredientsQuantity, int servingsQuantity) {

        // Construct the RemoteViews object
        RemoteViews rv;
        rv = getIngredientsRemoteView(context, isDatabaseEmpty, recipeName, ingredientsQuantity, servingsQuantity);

        appWidgetManager.updateAppWidget(appWidgetId, rv);

    }

    /**
     * Creates and returns the RemoteViews to be displayed in the GridView mode widget
     *
     * @param context The context
     * @return The RemoteViews for the GridView mode widget
     */
    private static RemoteViews getIngredientsRemoteView(Context context, boolean isDatabaseEmpty, String recipeName, int ingredientsQuantity, int servingsQuantity) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);

        // Handle empty  ingredients database
        views.setEmptyView(R.id.widget_grid_view, R.id.empty_view);


        views.setTextViewText(R.id.servings_text,
                "Ingredients for the " +
                        recipeName + " recipe, \n" +
                        "" +
                        "for " +
                        servingsQuantity + " servings:");

        views.setTextViewText(R.id.hardcode_ingredient, "Ingredient");
        views.setTextViewText(R.id.hardcode_measure, "Measure");
        views.setTextViewText(R.id.hardcode_quantity, "Quantity");


        // Set the GridWidgetService intent to act as the adapter for the GridView
        Intent gridAdapterIntent = new Intent(context, GridWidgetService.class);
        views.setRemoteAdapter(R.id.widget_grid_view, gridAdapterIntent);

        // Set MainActivity intent to launch when clicked
        Intent appIntent = new Intent(context, MainActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.empty_view, appPendingIntent);


        return views;
    }

    /**
     * Updates all widget instances given the widget Ids and display information
     *
     * @param context          The calling context
     * @param appWidgetManager The widget manager
     * @param appWidgetIds     Array of widget Ids to be updated
     */
    public static void updateIngredientsWidget(Context context, AppWidgetManager appWidgetManager,
                                               int[] appWidgetIds, boolean isDatabaseEmpty, String recipeName, int ingredientsQuantity, int servingsQuantity) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, isDatabaseEmpty, recipeName, ingredientsQuantity, servingsQuantity);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the intent service update widget action, the service takes care of updating the widgets UI

        BakingWidgetService.startActionUpdateIngredientsWidget(context);


    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

