package com.vuforia.samples.VuforiaSamples.app.ImageTargets;

import android.util.Log;

/**
 * Created by pepper on 2018/6/16.
 */

public class DatasetManager {

    private String mStartDatasetString;
    private String mEndDatasetString;
    private String LOGTAG = "MyDatasetExcption:";

    public boolean setStartDatasetString(String startDatasetString) {
        try {
            mStartDatasetString = startDatasetString;
        } catch (Exception e) {
            Log.e(LOGTAG, e.toString());
            return false;
        }

        return true;
    }

    public boolean setEndDatasetString(String endDatasetString) {
        try {
            mEndDatasetString = endDatasetString;
        } catch (Exception e) {
            Log.e(LOGTAG, e.toString());
            return false;
        }
        return true;

    }


    public String getStartDatasetString() {
        if (mStartDatasetString != null)
            return mStartDatasetString;
        else
            return null;
    }

    public String getEndDadtasetString() {
        if (mEndDatasetString != null)
            return mEndDatasetString;
        else
            return null;
    }






}
