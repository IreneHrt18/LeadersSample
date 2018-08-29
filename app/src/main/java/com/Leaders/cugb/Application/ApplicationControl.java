/*===============================================================================
Copyright (c) 2016-2017 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.Leaders.cugb.Application;

import com.vuforia.State;


//  Interface to be implemented by the activity which uses ApplicationSession
public interface ApplicationControl
{

    /**
     * 调用以初始化跟踪器
     * @return
     */
    boolean doInitTrackers();


    /**
     * 被调用来加载跟踪器的数据
     * @return
     */
    boolean doLoadTrackersData();


    /**
     * 调用初始化跟踪器和它们的跟踪器开始跟踪
     * @return
     */
    boolean doStartTrackers();


    /**
     * 停止追踪
     * @return
     */
    boolean doStopTrackers();


    /**
     * 销毁追踪数据
     * @return
     */
    boolean doUnloadTrackersData();
    
    

    boolean doDeinitTrackers();


    /**
     * 这个回调在Vuforia初始化完成之后调用，跟踪器已初始化，它们的数据已加载，追踪准备完毕
     * @param e
     */
    void onInitARDone(ApplicationException e);


    /**
     * 这个回调每个周期都被调用
     * @param state
     */
    void onVuforiaUpdate(State state);


    /**
     * 这个回调在Vuforia中被调用
     */
    void onVuforiaResumed();


    /**
     * 这个回调在Vuforia启动后被调用
     */
    void onVuforiaStarted();
    
}
