package com.example.android.mybakingrecipe.data;


/**
 * A {@link  RecipeItem} object contains information related to a single Recipe.
 */


public class RecipeItem {



    /**
     * The ID of the recipe from JSON
     */


    private int mExternalId;

    /**
     * Name of the baked goods that are prepared in the recipe - Recipe Name
     */


    private String mRecipeName;

    /**
     * JSON STRING - data  about the ingredients
     */


    private String mIngredientsJsonData;

    /**
     * Quantity of the ingredients
     */

    private int mIngredientsQuantity;

    /**
     * JSON STRING - data  about  the steps
     */

    private String mStepsJsonData;

    /**
     * Quantity of the steps
     */

    private int mStepsQuantity;

    /**
     * Number of the servings of the recipe
     */

    private int mServingsNumber;


    /**
     * Constructs a new {@link  RecipeItem} object with all the parameters
     */

    public RecipeItem(int mExternalId, String mRecipeName, String mIngredientsJsonData,
                      int mIngredientsQuantity, String mStepsJsonData,
                      int  mStepsQuantity, int mServingsNumber) {
        this.mExternalId = mExternalId;
        this.mRecipeName = mRecipeName;
        this.mIngredientsJsonData = mIngredientsJsonData;
        this.mIngredientsQuantity = mIngredientsQuantity;
        this.mStepsJsonData = mStepsJsonData;
        this.mStepsQuantity = mStepsQuantity;
        this.mServingsNumber = mServingsNumber;
    }



    public int getExternalId() {
        return mExternalId;
    }

    public String getRecipeName() {
        return mRecipeName;
    }

    public String getIngredientsJsonData() {
        return mIngredientsJsonData;
    }

    public int getIngredientsQuantity() {
        return mIngredientsQuantity;
    }

    public int getStepsQuantity() {
        return mStepsQuantity;
    }

    public String getStepsJsonData() {
        return mStepsJsonData;
    }

    public int getServingsNumber() {
        return mServingsNumber;
    }
}
