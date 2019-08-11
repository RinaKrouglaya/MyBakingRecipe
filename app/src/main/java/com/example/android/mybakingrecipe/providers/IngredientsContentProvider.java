package com.example.android.mybakingrecipe.providers;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.android.mybakingrecipe.providers.IngredientsContract.IngredientsEntry;


public class IngredientsContentProvider extends ContentProvider {


    public static final int RECIPE = 100;
    public static final int RECIPE_NAME = 101;

    // Declare a static variable for the Uri matcher
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String TAG = IngredientsContentProvider.class.getName();
    // Member variable for a IngredientsDbHelper that's initialized in the onCreate() method
    private IngredientsDbHelper mIngredientsDbHelper;

    // Define a static buildUriMatcher method that associates URI's with their int match
    public static UriMatcher buildUriMatcher() {
        // Initialize a UriMatcher
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Add URI matches

        uriMatcher.addURI(IngredientsContract.AUTHORITY, IngredientsContract.PATH_INGREDIENTS, RECIPE);
        uriMatcher.addURI(IngredientsContract.AUTHORITY, IngredientsContract.PATH_INGREDIENTS + "/#", RECIPE_NAME);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mIngredientsDbHelper = new IngredientsDbHelper(context);
        return true;
    }

    /***
     * Handles requests to insert a single new row of data
     *
     * @param uri
     * @param values
     * @return
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mIngredientsDbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the plants directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned
        switch (match) {
            case RECIPE:
                // Insert new values into the database
                long id = db.insert(IngredientsEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(IngredientsContract.IngredientsEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    /***
     * Handles requests for data by URI
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = mIngredientsDbHelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            // Query for the ingredients directory

            case RECIPE:
                retCursor = db.query(IngredientsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;


            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }

    /***
     * Deletes  data
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return number of rows affected
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = mIngredientsDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        // Keep track of the number of deleted plants
        int rawDeleted; // starts as 0
        switch (match) {
            // Handles all of the items item case
            case RECIPE:

                rawDeleted = db.delete(IngredientsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Notify the resolver of a change and return the number of items deleted
        if (rawDeleted != 0) {
            // A raw was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of raw deleted
        return rawDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        return 0;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
