package com.example.android.mybakingrecipe.data;


/**
 * A {@link  IngredientItem} object contains information related to
 * a single ingredient in the Recipe.
 */


public class IngredientItem {

    /**
     * The ID of the ingredient from JSON
     */

    private int externalId;


    /**
     * Quantity of the ingredient
     */
    private float quantity;

    /**
     * Measure unit of the ingredient
     */
    private String measure;


    /**
     * Name of the ingredient
     */
    private String name;


    /**
     * Constructs a new {@link  IngredientItem} object with all the parameters
     */

    public IngredientItem (int externalId, float quantity, String measure, String name) {
        this.externalId = externalId;
        this.quantity = quantity;
        this.measure = measure;
        this.name = name;
    }





    public int getExternalId() {
        return externalId;
    }

    public float getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getName() {
        return name;
    }


}
