package com.Leaders.cugb.AR.app.ImageTargets;

import android.util.Log;

import com.Leaders.cugb.Application.dijkstra.Dijkstra;

/**
 * 数据集管理类
 */

public class DatasetManager {

    private static final String DATASET_TITLE="FL0";
    private String mStartDatasetString;
    private String mEndDatasetString;
    private String LOGTAG = "MyDatasetExcption:";
    private Dijkstra mDijkstra;

    public DatasetManager(Dijkstra _dijkstra){
        mDijkstra=_dijkstra;

    }
    /**
     *设置起点数据集名称
     */
    public boolean setStartDatasetString() {
        //trans the start floor to string
        String startDatasetString=DATASET_TITLE+Integer.toString(mDijkstra.layer1);
        try {
            mStartDatasetString = startDatasetString;
        } catch (Exception e) {
            Log.e(LOGTAG, e.toString());
            return false;
        }

        return true;
    }

    /**
     *设置终点数据集名称
     */
    public boolean setEndDatasetString() {
        //trans the end floor to string
        String endDatasetString=DATASET_TITLE+Integer.toString(mDijkstra.layer2);
        try {
            mEndDatasetString = endDatasetString;
        } catch (Exception e) {
            Log.e(LOGTAG, e.toString());
            return false;
        }
        return true;

    }

    /**
     *获取起始数据集名称
     */
    public String getStartDatasetString() {
        if (mStartDatasetString != null)
            return mStartDatasetString;
        else
            return null;
    }

    /**
     *获取终点数据集名称
     */
    public String getEndDadtasetString() {
        if (mEndDatasetString != null)
            return mEndDatasetString;
        else
            return null;
    }






}
