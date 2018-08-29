package com.Leaders.cugb.AR.app.ImageTargets;

import com.Leaders.cugb.Application.dijkstra.Dij_Main;
import com.Leaders.cugb.Application.dijkstra.Dijkstra;
import com.Leaders.cugb.Application.dijkstra.Point;

import java.io.IOException;
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
    public static int trackableNumber=0;

    /**
     * 获取路径分析结果
     * @return
     */
    public void  analyseDijkstra(Dij_Main dij_main,String startID,String destination) throws IOException {

        ArrayList<MyTrackable> resultList=new ArrayList<MyTrackable>();

        ArrayList<Point> pointArrayList=dij_main.getMyList(startID,destination);


        for (int i=0;i<pointArrayList.size();i++)
        {
            Point _point=pointArrayList.get(i);
            MyTrackable trackable=new MyTrackable(_point.ID,(float)_point.angle,_point.dis);
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
