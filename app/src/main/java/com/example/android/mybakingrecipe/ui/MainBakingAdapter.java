package com.example.android.mybakingrecipe.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.mybakingrecipe.R;
import com.example.android.mybakingrecipe.data.RecipeItem;

import java.util.ArrayList;

public class MainBakingAdapter extends RecyclerView.Adapter<MainBakingAdapter.MainRecipesViewHolder> {
    private static final String TAG = MainBakingAdapter.class.getSimpleName();
    /*
     * An on-click handler that  defined to make it easy for an Activity to interface with
     * the RecyclerView
     */
    final private ListItemClickListener mOnClickListener;
    private int mMainRecipesItems;
    private ArrayList<RecipeItem> mListOfRecipeItemsData;
    private int mNumberItems;

    /**
     * Constructor for MainBakingAdapter that accepts a number of items to display and
     * the specification for the ListItemClickListener.
     *
     * @param numberOfItems Number of items to display in list
     * @param listener      Listener for list item clicks
     */
    public MainBakingAdapter(int numberOfItems, ListItemClickListener listener) {
        mMainRecipesItems = numberOfItems;
        mOnClickListener = listener;

    }

    @NonNull
    @Override
    public MainRecipesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.main_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        MainRecipesViewHolder viewHolder = new MainRecipesViewHolder(view);

        return viewHolder;
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */

    @Override
    public void onBindViewHolder(@NonNull MainRecipesViewHolder holder, int position) {

        if (mListOfRecipeItemsData != null) {
            RecipeItem currentRecipe = mListOfRecipeItemsData.get(position);
            holder.mBakingTextView.setText(currentRecipe.getRecipeName());

        } else holder.mBakingTextView.setText(R.string.connecting);
    }

    @Override
    public int getItemCount() {
        return mMainRecipesItems;
    }

    /**
     * This method is used to set the  Baking Data on a MainBakingAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new MainBakingAdapter  to display it.
     *
     * @param bakingData The new baking data to be displayed.
     */
    public void setBakingData(ArrayList<RecipeItem> bakingData) {
        mListOfRecipeItemsData = bakingData;
        notifyDataSetChanged();
    }


    /**
     * The interface that receives onClick messages.
     */
    public interface ListItemClickListener {

        void onListItemClick(int clickedItemIndex);

    }

    /**
     * The ViewHolder  - Cache of the children views for a list item.
     */

    class MainRecipesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mBakingTextView;

        //Constructor
        public MainRecipesViewHolder(@NonNull View itemView) {
            super(itemView);

            mBakingTextView = itemView.findViewById(R.id.tv_item_main_recipes);
            itemView.setOnClickListener(this);
        }

        /**
         * Called whenever a user clicks on an item in the list.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }

    }
}
