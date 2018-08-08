package com.Leaders.cugb.AR.app.ImageTargets;

import com.Leaders.cugb.Application.dijkstra.dijkstra;
import com.Leaders.cugb.Application.dijkstra.point;

import java.util.ArrayList;

/**
 * Created by pepper on 2018/6/23.
 */

public class TrackableManager {

    public static final int SAMPLE_DEMO=1;
    public static final int CLASSIC_APP=0;

    public static int MODULE=SAMPLE_DEMO;

    public static ArrayList<MyTrackable> mResultList;

    /**
     * 路径分析结果数目
     */
    private static int trackableNumber;

    /**
     * 路径分析器
     */
    public static dijkstra mDijkstra;
    /**
     * 获取路径分析结果
     * @return
     */
    public void  analyseDijkstra(dijkstra _dijkstra){

        ArrayList<MyTrackable> resultList=new ArrayList<MyTrackable>();

        for (point _point:_dijkstra.getMyPoints()
                ) {
            MyTrackable trackable=new MyTrackable(_point.name,(float)_point.angle,_point.dis);
            resultList.add(trackable);
        }

        trackableNumber=resultList.size();

        resultList.get(trackableNumber).setTexture(MyTrackable.END_TEXTURE);

        mResultList=resultList;

    }
    public ArrayList<MyTrackable> getDijikstraResult(){
        return mResultList;
    }

}
