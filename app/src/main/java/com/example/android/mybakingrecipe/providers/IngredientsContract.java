package com.example.android.mybakingrecipe.providers;


import android.net.Uri;
import android.provider.BaseColumns;

public class IngredientsContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.example.android.mybakingrecipe";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "ingredients" directory
    public static final String PATH_INGREDIENTS = "ingredients";

    public static final long INVALID_INGREDIENTS_ID = -1;

    public static final class IngredientsEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();

        public static final String TABLE_NAME = "ingredients";
        public static final String COLUMN_RECIPE_NAME = "recipeName";
        public static final String COLUMN_INGREDIENTS_JSON = "ingredientsJsonData";
        public static final String COLUMN_INGREDIENTS_QUANTITY = "ingredientsQuantity";
        public static final String COLUMN_SERVINGS_QUANTITY = "servingsQuantity";
    }
}
