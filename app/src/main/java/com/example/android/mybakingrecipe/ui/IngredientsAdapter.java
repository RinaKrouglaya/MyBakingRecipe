package com.example.android.mybakingrecipe.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.mybakingrecipe.R;
import com.example.android.mybakingrecipe.data.IngredientItem;

import java.util.ArrayList;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder> {
    private int mIngredientsItems;

    private ArrayList<IngredientItem> mListOfIngredientsItemsData;


    //Constructor
    public IngredientsAdapter(int numberOfRecipeSteps) {
        mIngredientsItems = numberOfRecipeSteps;

    }

    @NonNull
    @Override
    public IngredientsAdapter.IngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.ingredients_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        IngredientsAdapter.IngredientsViewHolder viewHolder = new IngredientsAdapter.IngredientsViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsAdapter.IngredientsViewHolder holder, int position) {
        if (mListOfIngredientsItemsData != null) {
            IngredientItem currentIngredientItem = mListOfIngredientsItemsData.get(position);
            int ingredientNumberForUI = currentIngredientItem.getExternalId() + 1;
            String IngredientNameAndNumber =
                    ingredientNumberForUI +
                            ". " +
                            currentIngredientItem.getName();
            holder.ingredientNameTextView.setText(IngredientNameAndNumber);

            String quantityString = String.valueOf(currentIngredientItem.getQuantity());
            if (quantityString.endsWith(".0")) {
                quantityString = quantityString.replace(".0", "");
            }

            holder.quantityTextView.setText(quantityString);
            holder.measureTextView.setText(currentIngredientItem.getMeasure());

        } else holder.ingredientNameTextView.setText(R.string.connecting);
    }


    @Override
    public int getItemCount() {
        return mIngredientsItems;
    }

    /**
     * This method is used to set the  IngredientsDbHelper Recipe Data on a IngredientsAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new IngredientsAdapter  to display it.
     *
     * @param ingredientsData The new ingredients data to be displayed.
     */
    public void setIngredientData(ArrayList<IngredientItem> ingredientsData) {
        mListOfIngredientsItemsData = ingredientsData;
        notifyDataSetChanged();
    }

    class IngredientsViewHolder extends RecyclerView.ViewHolder {


        TextView ingredientNameTextView;
        TextView quantityTextView;
        TextView measureTextView;

        public IngredientsViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientNameTextView = itemView.findViewById(R.id.ingredients_item_ingredient_name);
            quantityTextView = itemView.findViewById(R.id.ingredients_item_ingredient_quantity);
            measureTextView = itemView.findViewById(R.id.ingredients_item_ingredient_measure);
        }
    }

}
