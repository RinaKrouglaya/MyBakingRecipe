package com.example.android.mybakingrecipe.providers;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.mybakingrecipe.providers.IngredientsContract.IngredientsEntry;


public class IngredientsDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "ingredients.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public IngredientsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold the ingredients data
        final String SQL_CREATE_INGREDIENTS_TABLE = "CREATE TABLE " + IngredientsEntry.TABLE_NAME + " (" +
                IngredientsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                IngredientsEntry.COLUMN_RECIPE_NAME + " STRING NOT NULL, " +
                IngredientsEntry.COLUMN_INGREDIENTS_JSON + " STRING NOT NULL, " +
                IngredientsEntry.COLUMN_INGREDIENTS_QUANTITY + " INTEGER NOT NULL, " +
                IngredientsEntry.COLUMN_SERVINGS_QUANTITY + " INTEGER NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_INGREDIENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + IngredientsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
