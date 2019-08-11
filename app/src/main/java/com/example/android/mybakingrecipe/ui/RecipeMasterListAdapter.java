package com.example.android.mybakingrecipe.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.mybakingrecipe.R;
import com.example.android.mybakingrecipe.data.StepItem;

import java.util.ArrayList;

public class RecipeMasterListAdapter extends RecyclerView.Adapter<RecipeMasterListAdapter.RecipeMasterListViewHolder> {
    /*
     * An on-click handler that  defined to make it easy for an Activity to interface with
     * the RecyclerView
     */
    final private RecipeMasterListAdapter.ListItemClickListener mOnClickListener;
    private int mMasterListRecipeItems;
    private ArrayList<StepItem> mListOfRecipeItemsData;


    //Constructor
    public RecipeMasterListAdapter(int numberOfRecipeSteps, RecipeMasterListAdapter.ListItemClickListener listener) {
        mMasterListRecipeItems = numberOfRecipeSteps;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public RecipeMasterListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recipe_master_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        RecipeMasterListViewHolder viewHolder = new RecipeMasterListViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeMasterListViewHolder holder, int position) {
        if (mListOfRecipeItemsData != null) {
            StepItem currentStepItem = mListOfRecipeItemsData.get(position);
            String stepIdAndNameString;
            //Do not show step number 0
            if (currentStepItem.getStepId() != 0) {
                stepIdAndNameString = currentStepItem.getStepId() + ". " + currentStepItem.getStepName();
            } else {
                stepIdAndNameString = currentStepItem.getStepName();
            }

            holder.listItemRecipeMasterListTextView.setText(stepIdAndNameString);

        } else holder.listItemRecipeMasterListTextView.setText(R.string.connecting);
    }

    @Override
    public int getItemCount() {
        return mMasterListRecipeItems;
    }

    /**
     * This method is used to set the  MasterList Recipe Data on a RecipeMasterListAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new RecipeMasterListAdapter   to display it.
     *
     * @param masterListRecipeData The new MasterList  recipe data to be displayed.
     */
    public void setMasterListRecipeData(ArrayList<StepItem> masterListRecipeData) {
        mListOfRecipeItemsData = masterListRecipeData;
        notifyDataSetChanged();
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {

        void onListItemClick(int clickedItemIndex);

    }

    class RecipeMasterListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView listItemRecipeMasterListTextView;

        public RecipeMasterListViewHolder(@NonNull View itemView) {
            super(itemView);
            listItemRecipeMasterListTextView = itemView.findViewById(R.id.tv_item_recipe_master_list);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

}
