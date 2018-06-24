package com.Leaders.cugb.Application.dijkstra;

import java.io.IOException;
import java.util.ArrayList;

class Dis {
    String path= "";
    double value= 0;
    boolean visit= false;
}

class Graph_DG {
    int vexnum;   //ͼ�Ķ������
    int edge;     //ͼ�ı���1
    dijkstra dij;
    double arc[][];   //�ڽӾ���
    public Dis [] dis;   //��¼�����������·������Ϣ
    public point points[]=new point[2005];
    double INT_MAX=1e18;
    public Graph_DG(int vexnum, int edge,dijkstra dij) {
    	  //��ʼ���������ͱ���
        this.vexnum = vexnum;
        this.edge = edge;
        this.dij=dij;
        //Ϊ�ڽӾ��󿪱ٿռ�͸���ֵ
        arc = new double[this.vexnum][this.vexnum];
        dis = new Dis[this.vexnum];
        for (int i = 0; i < this.vexnum; i++) {
        	dis[i]=new Dis();//amazing???
            arc[i] = new double[this.vexnum];
            for (int k = 0; k < this.vexnum; k++) {
                //�ڽӾ����ʼ��Ϊ�����
                    arc[i][k] = 1e18;
            }
        }
    }
    //����ͼ
    void createGraph(int l,int ll) throws IOException {
    	readtxt read=new readtxt();
    	read.readlc(l,ll,this);

//		point item=points[1];
//	    System.out.println(item.id+" "+item.x+" "+item.y+" ");
    }
    //�����·��
    void Dijkstra(int begin) {
    	//���ȳ�ʼ�����ǵ�dis����
        int i;
        //�������ĵ�����·��Ϊ0
        dis[begin - 1].value = 0;
        dis[begin - 1].visit = true;
        for (i = 0; i < this.vexnum; i++) {
            //���õ�ǰ��·��
            dis[i].path += 'v'+Integer.toString(begin)+'v'+Integer.toString(i+1); 

            dis[i].value = arc[begin - 1][i];
        }

        int count = 1;
        //����ʣ��Ķ�������·����ʣ��this.vexnum-1�����㣩
        while (count != this.vexnum) {
            //temp���ڱ��浱ǰdis��������С���Ǹ��±�
            //min��¼�ĵ�ǰ����Сֵ
            int temp=0;
            double min = INT_MAX;
            for (i = 0; i < this.vexnum; i++) {
                if (!dis[i].visit && dis[i].value<min) {
                    min = dis[i].value;
                    temp = i;
                }
            }
            //cout << temp + 1 << "  "<<min << endl;
            //��temp��Ӧ�Ķ�����뵽�Ѿ��ҵ������·���ļ�����
            dis[temp].visit = true;
            ++count;
            for (i = 0; i < this.vexnum; i++) {
                //ע�����������arc[temp][i]!=INT_MAX����ӣ���Ȼ�����������Ӷ���ɳ����쳣
                if (!dis[i].visit && arc[temp][i]!=INT_MAX && (dis[temp].value + arc[temp][i]) < dis[i].value) {
                    //����µõ��ı߿���Ӱ������Ϊ���ʵĶ��㣬�Ǿ;͸����������·���ͳ���
                    dis[i].value = dis[temp].value + arc[temp][i];
    		
                    dis[i].path = dis[temp].path + 'v'+ Integer.toString(i+1);
                }
            }
        }
    }
    //��ӡ���·��
    void get_path(int end,int v) {

//    	System.out.println(dis[end-1].path);//
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
    		pa="";
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
    		
    		dij.myPoints.add(points[patt[i]]);
//		    System.out.println(points[patt[i]].id+" "+points[patt[i]].x+" "+points[patt[i]].y+" �Ƕ�"+points[patt[i]].angle+" ����"+c);   		
    	}
    	if(points[patt[p]].lc==dij.ll)
			points[patt[p]].id=patt[p]-(dij.ve[0]+dij.ve[dij.l]);
		else if(points[patt[p]].lc==dij.l)
    		points[patt[p]].id=patt[p]-(dij.ve[0]);
		else 
			points[patt[p]].id=patt[p];
    	dij.myPoints.add(points[patt[p]]);
//    	System.out.println(points[patt[p]].id+" "+points[patt[p]].x+" "+points[patt[p]].y);
    }
    boolean _check() {
    	if(dis[1].value<dis[2].value)return true;
    	else return false;
    }
};
public class dijkstra {
	int ve[]=new int[]{19,545,484,566,797,619,354};
	int ed[]=new int[]{38,648,624,726,797,619,354};
	public int l,ll,s,t;
	public ArrayList<point> myPoints=new ArrayList<point>();
	public void setST(int l,int s,int ll,int t) {
		this.l=l;
		this.ll=ll;
		this.s=s;
		this.t=t;
	}
	public ArrayList<point> getMyPoints() {
		for(int i=0;i<myPoints.size();i++){
			myPoints.get(i).ID=Integer.toString(myPoints.get(i).id+myPoints.get(i).lc*1000);
		}
		return myPoints;
	}
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
