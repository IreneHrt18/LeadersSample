package com.Leaders.cugb.AR.app.ImageTargets;

import com.Leaders.cugb.Application.dijkstra.dijkstra;
import com.Leaders.cugb.Application.dijkstra.point;

import java.util.ArrayList;

/**
 * Created by pepper on 2018/5/22.
 */

public class MyTrackable {


    private String trackableName;
    private float orientation;


    public MyTrackable(String _trackableName ,float _orienation){
        this.trackableName=_trackableName;
        this.orientation=_orienation;
    }

    public MyTrackable(){

    }

    public String getName(){
        return this.trackableName;
    }

    public void setTrackableName(String name){this.trackableName=name; }

    public float getOrientation(){
        return this.orientation;
    }

    public void setOrientation(float orientation){this.orientation=orientation;}

    public static ArrayList<MyTrackable> defalteResult(){
        ArrayList<MyTrackable> mTrackables=new ArrayList<MyTrackable>();
        String targetname[]={"0002","0004","0005","0006","0007","0008","0009"};


        // go straight straight straight left straight straight right
        float orientations[]={0.f,0.f,0.f,90.f,90.f,90.f,0.f};
        for(int i=0;i<7;i++){
            MyTrackable t=new MyTrackable(targetname[i],orientations[i]);
            mTrackables.add(t);
        }
        return mTrackables;
    }


}
