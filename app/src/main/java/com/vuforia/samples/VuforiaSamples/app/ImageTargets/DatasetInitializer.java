package com.vuforia.samples.VuforiaSamples.app.ImageTargets;

import com.vuforia.DataSet;
import com.vuforia.ObjectTracker;

/**
 * Created by pepper on 2018/6/16.
 */

public interface DatasetInitializer {

     boolean doInitDataset(ObjectTracker mObjTracker, DataSet mDataset, String mDatasetString);

}
