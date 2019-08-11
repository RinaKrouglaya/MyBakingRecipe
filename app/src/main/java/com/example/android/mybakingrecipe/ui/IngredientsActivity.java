package com.example.android.mybakingrecipe.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.android.mybakingrecipe.R;

public class IngredientsActivity extends AppCompatActivity {

    private String mRecipeName = "";
    private String mIngredientsJsonData;
    private String mStepsJsonData;
    private String mIngredientsQuantityString;
    private String mServingsQuantityString;
    private String mStepsQuantityString;


    private String[] mReceivedDataStringArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        //Get data from intent
        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

                mReceivedDataStringArray =
                        intentThatStartedThisActivity.getStringArrayExtra(Intent.EXTRA_TEXT);

                //0 Recipe name
                mRecipeName = mReceivedDataStringArray[0];
                //1 IngredientsDbHelper Json Data
                mIngredientsJsonData = mReceivedDataStringArray[1];
                //2  IngredientsDbHelper quantity
                mIngredientsQuantityString = mReceivedDataStringArray[2];
                //3 Steps Json data
                mStepsJsonData = mReceivedDataStringArray[3];
                //4 Steps quantity String
                mStepsQuantityString = mReceivedDataStringArray[4];
                //5 Number of servings String
                mServingsQuantityString = mReceivedDataStringArray[5];
            }
        }

        /*
         * Initializing
         */

        // Customized toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle(mRecipeName + " Recipe");
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        // Create a new IngredientsFragment
        IngredientsFragment ingredientsFragment = new IngredientsFragment();


        // Add the fragment to its container using a FragmentManager and a Transaction
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.ingredients_fragment_container, ingredientsFragment)
                .commit();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, RecipeMasterListActivity.class);

        String[] stringArray = {
                //0 Recipe name
                mRecipeName,
                //1 IngredientsDbHelper Json Data
                mIngredientsJsonData,
                //2  IngredientsDbHelper quantity String
                mIngredientsQuantityString,
                //3 Steps Json data
                mStepsJsonData,
                //4 Steps quantity String
                mStepsQuantityString,
                //5 Number of servings String
                mServingsQuantityString,
        };
        i.putExtra(Intent.EXTRA_TEXT, stringArray);

        startActivity(i);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        final int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
