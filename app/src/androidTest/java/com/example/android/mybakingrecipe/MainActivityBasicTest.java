package com.example.android.mybakingrecipe;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.FragmentManager;


import com.example.android.mybakingrecipe.ui.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import android.support.test.espresso.contrib.RecyclerViewActions;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityBasicTest {

    private static final int TEST_POSITION = 2;
    private static final String RECIPE_NAME = "Yellow Cake";
    private IdlingResource mIdlingResource;

    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Test
    public void recyclerViewIsDisplayed() {
        //First check whether recycler view is visible
        onView(withId(R.id.rv_baking_main)).check(matches(isDisplayed()));
    }

    @Test
    public void recipesAreDisplayedCorrectly() {
        //Then check one of the items shows the expected recipe name
        onView(withId(R.id.rv_baking_main)).perform(RecyclerViewActions.scrollToPosition(TEST_POSITION));
        onView(withText(RECIPE_NAME)).check(matches(isDisplayed()));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }
}

