package com.Leaders.cugb.Application.dijkstra;

import java.io.IOException;

/**
 *地图中路网点的结构
 */
public class point{
	public int id;
	public String name="";

	public double x,y,dis;
	public int lc;//楼层
	public double angle;//正北为0，顺时针偏移
	/**
	 *@param x x坐标
	 * @param y y坐标
	 */
	public point (double x,double y){
		this.x=x;
		this.y=y;
	}
}
