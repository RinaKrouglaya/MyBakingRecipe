package com.example.android.mybakingrecipe.data;


/**
 * A {@link  StepItem} object contains information related to
 * a single ingredient in the Recipe.
 */

public class StepItem {

    /**
     * The ID of the step
     */
    private int mStepId;


    /**
     * Name of the step - short description
     */
    private String mStepName;


    /**
     * Detailed description of the step
     */
    private String mDescription;

    /**
     * Link to video URL of the step
     */
    private String mVideoLink;


    /**
     * Constructs a new {@link  StepItem} object with all the parameters.
     */
    public StepItem(int mStepId, String mStepName, String mDescription, String mVideoLink) {
        this.mStepId = mStepId;
        this.mStepName = mStepName;
        this.mDescription = mDescription;
        this.mVideoLink = mVideoLink;
    }

    public int getStepId() {
        return mStepId;
    }

    public String getStepName() {
        return mStepName;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getVideoLink() {
        return mVideoLink;
    }
}
