package com.Leaders.cugb.Application.dijkstra;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;




public class readtxt {

	int ve[]=new int[]{19,545,484,566,642,502,296};
	int ed[]=new int[]{38,648,624,726,797,619,354};
	String lcname[]=new String[]{
			"my\\0.txt",
			"my\\F1id.txt",
			"my\\F2id.txt",
			"my\\F3id.txt",
			"my\\F4id.txt",
			"my\\F5id.txt",
			"my\\F6id.txt"
	};
	String Fname[]=new String[]{
			"",
			"my\\F1name.txt",
			"my\\F2name.txt",
			"my\\F3name.txt",
			"my\\F4name.txt",
			"my\\F5name.txt",
			"my\\F6name.txt"
	};
	public void readlc(int l, int ll,Graph_DG g) throws IOException {
		go(l,0,g);
		readlt(l, g);
		readname(l, g);
		if(l!=ll) {
			go(ll, ve[l],g);
			readname(ll, g);
			for(int id1=1;id1<=ve[0];id1++)
			{
				double min=0xffffff;
				int minid = 0;
				for(int i=ve[0]+ve[l]+1;i<=ve[0]+ve[l]+ve[ll];i++){
					
					double tem=(g.points[id1].x-g.points[i].x)*(g.points[id1].x-g.points[i].x)+(g.points[id1].y-g.points[i].y)*(g.points[id1].y-g.points[i].y);
					if(tem<min){
						min=tem;
						minid=i;
					}
				}		
				g.arc[id1-1][minid-1]=Math.sqrt(min);
				g.arc[minid-1][id1-1]=Math.sqrt(min);
			}	
		}
	}

	public void go(int l,int vel,Graph_DG g) throws IOException {
		ArrayList<String> list=new ArrayList<String>();
		FileInputStream fil=new FileInputStream(lcname[l]);
		InputStreamReader fil1=new InputStreamReader(fil);
		BufferedReader file=new BufferedReader(fil1);

		String values=file.readLine();
		    while(values!=null)
		    {
		         list.add(values);
		    	 values=file.readLine();   
		    }
		   file.close();
		   
		int size=list.size();
		for(int i=0;i<size;i++)
		{
			values=list.get(i);
		    String []value=values.split(" ");
		    int id1=Integer.parseInt(value[0])+ve[0]+vel;
		    double x1=Double.parseDouble(value[1]);
		    double y1=Double.parseDouble(value[2]);
		    int id2=Integer.parseInt(value[3])+ve[0]+vel;
		    double x2=Double.parseDouble(value[4]);
		    double y2=Double.parseDouble(value[5]);
		    double dis=Double.parseDouble(value[6]);
		    g.points[id1]=new point(x1, y1);
		    g.points[id2]=new point(x2, y2);
		    g.points[id1].lc=l;
		    g.points[id2].lc=l;
		    g.arc[id1-1][id2-1]=dis;
			g.arc[id2-1][id1-1]=dis;
		    
//		    System.out.println(id1+" "+x1+" "+y1+" "+dis);
		}
	}
	public void readlt(int lc,Graph_DG g) throws IOException {
		ArrayList<String> list=new ArrayList<String>();
		FileInputStream fil=new FileInputStream("my\\0.txt");
		InputStreamReader fil1=new InputStreamReader(fil);
		BufferedReader file=new BufferedReader(fil1);
	    
		String values=file.readLine();
		    while(values!=null)
		    {
		         list.add(values);
		    	 values=file.readLine();   
		    }
		   file.close();
		   
		int size=list.size();
		for(int i=0;i<size;i++)
		{
			values=list.get(i);
		    String []value=values.split(" ");
		    double x1=Double.parseDouble(value[0]);
		    double y1=Double.parseDouble(value[1]);
		    int id1=Integer.parseInt(value[2]);
		    g.points[id1]=new point(x1, y1);
		    g.points[id1].name=value[3];
		    g.points[id1].lc=0;
		    
			double min=0xffffff;
			int minid = 0;
			for(int i1=ve[0]+1;i1<=ve[0]+ve[lc];i1++){
				double tem=(g.points[id1].x-g.points[i1].x)*(g.points[id1].x-g.points[i1].x)+(g.points[id1].y-g.points[i1].y)*(g.points[id1].y-g.points[i1].y);
				if(tem<min){
					min=tem;
					minid=i1;
				}
			}		
			g.arc[id1-1][minid-1]=Math.sqrt(min);
			g.arc[minid-1][id1-1]=Math.sqrt(min);
//		    System.out.println(id1+" "+x1+" "+y1+" "+value[3]);
		}
	}
	public void readname(int lc,Graph_DG g) throws IOException {
		ArrayList<String> list=new ArrayList<String>();
		FileInputStream fil=new FileInputStream(Fname[lc]);
		InputStreamReader fil1=new InputStreamReader(fil);
		BufferedReader file=new BufferedReader(fil1);
	    
		String values=file.readLine();
		    while(values!=null)
		    {
		         list.add(values);
		    	 values=file.readLine();   
		    }
		   file.close();
		   
		int size=list.size();
		for(int i=0;i<size;i++)
		{
			values=list.get(i);
		    String []value=values.split(" ");
		    String name=value[0];
		    double x1=Double.parseDouble(value[1]);
		    double y1=Double.parseDouble(value[2]);
		    
			double min=0xffffff;
			int minid = 0;
			for(int i1=ve[0]+1;i1<=ve[0]+ve[lc];i1++){
				double tem=(x1-g.points[i1].x)*(x1-g.points[i1].x)+(y1-g.points[i1].y)*(y1-g.points[i1].y);
				if(tem<min){
					min=tem;
					minid=i1;
				}
			}		
			g.points[minid].name=name;
//		    System.out.println(minid+" "+x1+" "+y1+" "+name);
		}
	}
	
}
