package com.Leaders.cugb.AR.app.ImageTargets;

import com.Leaders.cugb.Application.dijkstra.dijkstra;
import com.Leaders.cugb.Application.dijkstra.point;

import java.util.ArrayList;

/**
 * Created by pepper on 2018/5/22.
 */

public class MyTrackable {


    public static final int NORMAL_TEXTURE=0;
    public static final int END_TEXTURE=1;

    /**
     * 跟踪对象名称
     */
    private String trackableName;
    /**
     * 跟踪对象方向（以北为正方向）
     */
    private float orientation;

    /**
     * 与下一跟踪目标距离
     */
    private double nextDistance;

    /**
     * 该对象代表的纹理材质
     */
    private int texture;


    public MyTrackable(String _trackableName ,float _orienation,double _nextDistence){
        this.trackableName=_trackableName;
        this.orientation=_orienation;
        this.nextDistance=_nextDistence;
        this.texture=NORMAL_TEXTURE;
    }


    /**
     * 获取跟踪对象名称
     * @return
     */
    public String getName(){
        return this.trackableName;
    }

    /**
     * 设置跟踪对象信息
     * @param name
     */
    public void setTrackableName(String name){this.trackableName=name; }

    /**
     * 获取跟踪对象方位
     * @return
     */
    public float getOrientation(){
        return this.orientation;
    }

    /**
     * 设置跟踪对象方位
     * @param orientation
     */
    public void setOrientation(float orientation){this.orientation=orientation;}

    /**
     * 设置跟踪对象显示材质
     */
    public void setTexture(int _texture){

        this.texture=_texture;
    }
    /**
     * 获取跟踪对象显示材质
     */
    public int getTextture(){
        return 0;
    }

    /**
     * 示例代码
     * @return
     */
    public static ArrayList<MyTrackable> defalteResult(){
        ArrayList<MyTrackable> mTrackables=new ArrayList<MyTrackable>();
        String targetname[]={"000_03","000_04","000_05","000_06","000_07"};


        // go straight left right straight  straight
        float orientations[]={-90.f,0.f,-90.f,-90.f,-90.f};
        for(int i=0;i<5;i++){
            MyTrackable t=new MyTrackable(targetname[i],orientations[i],0);
            mTrackables.add(t);
        }
        return mTrackables;
    }


}
