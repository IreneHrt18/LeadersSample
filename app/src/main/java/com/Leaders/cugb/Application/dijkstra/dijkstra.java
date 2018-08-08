package com.Leaders.cugb.Application.dijkstra;

import java.io.IOException;

import java.util.ArrayList;

/**
 *计算路径结果集中相邻两个点之间的距离，并返回结果
 */

class Dis {
    String path= "";
    double value= 0;
    boolean visit= false;
}

/**
 *创建用于路径规划的图
 */
class Graph_DG {
    int vexnum;   //图的顶点个数
    int edge;     //图的边数1
    dijkstra dij;
    double arc[][];   //邻接矩阵
    public Dis [] dis;   //记录各个顶点最短路径的信息
    public point points[]=new point[2005];
    double INT_MAX=1e18;
    public Graph_DG(int vexnum, int edge,dijkstra dij) {
    	  //初始化顶点数和边数
        this.vexnum = vexnum;
        this.edge = edge;
        this.dij=dij;
        //为邻接矩阵开辟空间和赋初值
        arc = new double[this.vexnum][this.vexnum];
        dis = new Dis[this.vexnum];
        for (int i = 0; i < this.vexnum; i++) {
        	dis[i]=new Dis();//amazing???
            arc[i] = new double[this.vexnum];
            for (int k = 0; k < this.vexnum; k++) {
                //邻接矩阵初始化为无穷大
                    arc[i][k] = 1e18;
            }
        }
    }
    //创建图
    void createGraph(int l,int ll) throws IOException {
    	readtxt read=new readtxt();
    	read.readlc(l,ll,this);

    }
    //求最短路径
    void Dijkstra(int begin) {
    	//首先初始化我们的dis数组
        int i;
        //设置起点的到起点的路径为0
        dis[begin - 1].value = 0;
        dis[begin - 1].visit = true;
        for (i = 0; i < this.vexnum; i++) {
            //设置当前的路径
            dis[i].path += 'v'+Integer.toString(begin)+'v'+Integer.toString(i+1); 

            dis[i].value = arc[begin - 1][i];
        }

        int count = 1;
        //计算剩余的顶点的最短路径（剩余this.vexnum-1个顶点）
        while (count != this.vexnum) {
            //temp用于保存当前dis数组中最小的那个下标
            //min记录的当前的最小值
            int temp=0;
            double min = INT_MAX;
            for (i = 0; i < this.vexnum; i++) {
                if (!dis[i].visit && dis[i].value<min) {
                    min = dis[i].value;
                    temp = i;
                }
            }

            //把temp对应的顶点加入到已经找到的最短路径的集合中
            dis[temp].visit = true;
            ++count;
            for (i = 0; i < this.vexnum; i++) {
                //注意这里的条件arc[temp][i]!=INT_MAX必须加，不然会出现溢出，从而造成程序异常
                if (!dis[i].visit && arc[temp][i]!=INT_MAX && (dis[temp].value + arc[temp][i]) < dis[i].value) {
                    //如果新得到的边可以影响其他为访问的顶点，那就就更新它的最短路径和长度
                    dis[i].value = dis[temp].value + arc[temp][i];
    		
                    dis[i].path = dis[temp].path + 'v'+ Integer.toString(i+1);
                }
            }
        }
    }

	/**
	 * 打印最短路径
	 * @param end
	 * @param v
	 */
    void get_path(int end,int v) {

    	String pa="";
    	int patt[]=new int[2005];
    	int p=-1;
    	    for(int i=0;i<dis[end-1].path.length();i++){
    			if(dis[end-1].path.charAt(i)=='v'){
    		    	if(pa!="")
    				patt[++p]=Integer.parseInt(pa);
    				pa="";
    			}
    			else{
    				pa+=dis[end-1].path.charAt(i);
    			}
    		}
    		patt[++p]=Integer.parseInt(pa);

    	for(int i=0;i<p;i++){
    		double a=points[patt[i+1]].x -points[patt[i]].x;
    		double b=points[patt[i+1]].y -points[patt[i]].y;
    		double c=Math.sqrt(a*a+b*b);
    		points[patt[i]].angle =180*Math.acos(b/c)/Math.acos(-1);
    		points[patt[i]].dis=c;
    		if(points[patt[i]].lc==dij.ll)
    			points[patt[i]].id=patt[i]-(dij.ve[0]+dij.ve[dij.l]);
    		else if(points[patt[i]].lc==dij.l)
        		points[patt[i]].id=patt[i]-(dij.ve[0]);
    		else 
    			points[patt[i]].id=patt[i];	
    		
    		dij.myPoints.add(points[patt[i]]);	}
    	if(points[patt[p]].lc==dij.ll)
			points[patt[p]].id=patt[p]-(dij.ve[0]+dij.ve[dij.l]);
		else if(points[patt[p]].lc==dij.l)
    		points[patt[p]].id=patt[p]-(dij.ve[0]);
		else 
			points[patt[p]].id=patt[p];
    	dij.myPoints.add(points[patt[p]]);}
    boolean _check() {
    	if(dis[1].value<dis[2].value)return true;
    	else return false;
    }
}

/**
 * 路径规划模块
 */
public class dijkstra {
	/**
	 * 示例地图数组
	 */
	int ve[]=new int[]{19,545,484,566,797,619,354};
	int ed[]=new int[]{38,648,624,726,797,619,354};
	/**
	 * 起点楼层
	 */
	public int l;
	/**
	 * 终点楼层
	 */
	public int ll;
	/**
	 * 起点编号
	 */
	public int s;
	/**
	 * 终点编号
	 */
	public int t;
	/**
	*路径点数据集合
	 */
	public ArrayList<point> myPoints=new ArrayList<point>();
	public void setST(int l,int s,int ll,int t) {
		this.l=l;
		this.ll=ll;
		this.s=s;
		this.t=t;
	}

	/**
	 * 获取路径点数据
	 * @return
	 */
	public ArrayList<point> getMyPoints() {
		return myPoints;
	}

	/**
	 * 执行路径分析
	 * @throws IOException
	 */
	public void go() throws IOException {
		int v,e;
		s+=ve[0];t+=ve[0];	
		if(l!=ll){
			t+=ve[l];	
		    v=ve[0]+ve[l]+ve[ll];e=ed[0]+ed[l]+ed[ll];
		}
		else{ 
			v=ve[0]+ve[l];e=ed[0]+ed[l];
		}
		Graph_DG graph=new Graph_DG(v,e,this);
	    graph.createGraph(l,ll);

		graph.Dijkstra(s);
		graph.get_path(t,v);
	}
	
}
