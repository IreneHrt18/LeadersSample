package com.Leaders.cugb.AR.app.ImageTargets;

import com.vuforia.DataSet;
import com.vuforia.ObjectTracker;

/**
 * 数据集初始化
 */

public interface DatasetInitializer {
     /**
      * 初始化指定数据集
      * @param mObjTracker 父类
      * @param mDataset 待初始化数据集
      * @param mDatasetString 待初始化数据集名称
      * @return 返回结果
      */
     boolean doInitDataset(ObjectTracker mObjTracker, DataSet mDataset, String mDatasetString);

     /**
      * 加载指定数据集
      * @param mObjTracker 父类
      * @param mDataset 待初始化数据集
      * @param mDatasetString 待初始化数据集名称
      * @return 返回结果
      */
     boolean doLoadDataset(ObjectTracker mObjTracker, DataSet mDataset, String mDatasetString);

     /**
      * 卸载指定数据集
      * @param mObjTracker 父类
      * @param mDataset 指定数据集
      * @return 返回结果
      */
     boolean doUnLoadDataset(ObjectTracker mObjTracker, DataSet mDataset);

     /**
      * 检查指定数据集状态
      * @param mObjTracker 父类
      * @param mDataset 指定数据集
      * @return 返回结果
      */
     boolean doCheckDataset(ObjectTracker mObjTracker, DataSet mDataset);

}
