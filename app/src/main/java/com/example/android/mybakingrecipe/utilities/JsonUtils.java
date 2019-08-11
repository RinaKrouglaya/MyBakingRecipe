package com.example.android.mybakingrecipe.utilities;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.mybakingrecipe.data.IngredientItem;
import com.example.android.mybakingrecipe.data.RecipeItem;
import com.example.android.mybakingrecipe.data.StepItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Utility functions to handle  JSON data.
 */

public final class JsonUtils {


    private static final String TAG = JsonUtils.class.getSimpleName();

    /**
     * This method parses JSON from a web response and returns the quantity of Recipe Titles
     *
     * @param recipesJsonStr JSON response from server
     * @return quantityOfRecipes quantity of Recipe Titles
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static int getRecipeQuantityFromJson(String recipesJsonStr)
            throws JSONException {

        int quantityOfRecipes = 0;

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(recipesJsonStr)) {
            return 0;
        }


        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONArray rootArray = new JSONArray(recipesJsonStr);
            quantityOfRecipes = rootArray.length();


        } catch (JSONException e) {

            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("JsonUtils", "Problem parsing the recipeItem JSON results", e);
        }

        return quantityOfRecipes;
    }


    /**
     * This method parses JSON from a web response and returns the ArrayList of {@link  RecipeItem} s
     *
     * @param recipesJsonStr JSON response from server
     * @return ArrayList of {@link  RecipeItem} s
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ArrayList<RecipeItem> getListOfRecipeItemsFromJson(String recipesJsonStr)
            throws JSONException {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(recipesJsonStr)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding {@link  RecipeItem} s to
        ArrayList<RecipeItem> recipeItems = new ArrayList<>();


        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONArray rootArray = new JSONArray(recipesJsonStr);

            // Creates a for loop that goes through the arrayList of rootObject
            for (int i = 0; i < rootArray.length(); i++) {

                // Get a single recipe at position index within the list of recipes
                JSONObject recipeObject = rootArray.getJSONObject(i);

                // Extract the value for the key called "id"
                int recipeId = recipeObject.getInt("id");

                // Extract the value for the key called "name"
                String recipeName = recipeObject.getString("name");


                // Extract the array for the key called "ingredients"
                JSONArray recipeIngredientsArray = recipeObject.getJSONArray("ingredients");
                String recipeIngredientsJsonString = recipeIngredientsArray.toString(recipeIngredientsArray.length());

                // Extract the array for the key called "steps"
                JSONArray recipeStepsArray = recipeObject.getJSONArray("steps");
                String recipeStepsJsonString = recipeStepsArray.toString(recipeStepsArray.length());

                // Extract the value for the key called "servings"
                int recipeServingsNumber = recipeObject.getInt("servings");

                // Create a new {@link  RecipeItem} object
                RecipeItem recipeItemObject =
                        new RecipeItem(recipeId, recipeName,
                                recipeIngredientsJsonString,
                                recipeIngredientsArray.length(),
                                recipeStepsJsonString,
                                recipeStepsArray.length(),
                                recipeServingsNumber);
                recipeItems.add(recipeItemObject);


            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("JsonUtils", "Problem parsing the recipeItem JSON results", e);
        }

        // Return the ArrayList of recipeItems
        return recipeItems;
    }


    /**
     * This method parses JSON from a web response and returns the ArrayList of {@link  StepItem} s
     *
     * @param stepsJsonStr JSON response from server
     * @return ArrayList of {@link  StepItem} s
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ArrayList<StepItem> getListOfStepItemsFromJson(String stepsJsonStr)
            throws JSONException {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(stepsJsonStr)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding {@link  StepItem} s to
        ArrayList<StepItem> stepItems = new ArrayList<>();


        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONArray recipeStepsArray = new JSONArray(stepsJsonStr);

            for (int k = 0; k < recipeStepsArray.length(); k++) {

                // Get a single step at position index within the list of recipes
                JSONObject stepsObject = recipeStepsArray.getJSONObject(k);

                // Extract the value for the key called "shortDescription"
                String stepName =
                        stepsObject.getString("shortDescription");

                // Extract the value for the key called  "description"
                String stepDescription = stepsObject.getString("description");

                // Extract the value for the key called  "videoURL"
                String videoLink = stepsObject.getString("videoURL");

                // Create a new {@link StepItem} object
                StepItem stepItemObject =
                        new StepItem(k,
                                stepName,
                                stepDescription,
                                videoLink);
                stepItems.add(stepItemObject);
            }


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("JsonUtils", "Problem parsing the recipeItem JSON results", e);
        }

        // Return the ArrayList of stepItems
        return stepItems;
    }

    /**
     * This method parses JSON from a web response and returns the ArrayList of {@link  IngredientItem} s
     *
     * @param ingredientsJsonStr JSON response from server
     * @return ArrayList of {@link  IngredientItem} s
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ArrayList<IngredientItem> getListOfIngredientItemsFromJson(String ingredientsJsonStr)
            throws JSONException {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(ingredientsJsonStr)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding {@link  IngredientItem} s to
        ArrayList<IngredientItem> ingredientItems = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONArray recipeIngredientsArray = new JSONArray(ingredientsJsonStr);

            for (int j = 0; j < recipeIngredientsArray.length(); j++) {

                // Get a single ingredient at position index within the list of ingredients
                JSONObject ingredientsObject = recipeIngredientsArray.getJSONObject(j);

                // Extract the value for the key called "quantity"
                float ingredientQuantity =
                        Float.parseFloat(ingredientsObject.getString("quantity"));

                // Extract the value for the key called  "measure"
                String ingredientMeasure = ingredientsObject.getString("measure");

                // Extract the value for the key called   "ingredient"
                String ingredientName = ingredientsObject.getString("ingredient");
                ingredientName = ingredientName.substring(0, 1).toUpperCase() + ingredientName.substring(1);


                // Create a new {@link IngredientItem} object
                IngredientItem ingredientItemObject =
                        new IngredientItem(j,
                                ingredientQuantity,
                                ingredientMeasure,
                                ingredientName);
                ingredientItems.add(ingredientItemObject);
            }


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("JsonUtils", "Problem parsing the recipeItem JSON results", e);
        }
        // Return the ArrayList of ingredientItems
        return ingredientItems;
    }
}