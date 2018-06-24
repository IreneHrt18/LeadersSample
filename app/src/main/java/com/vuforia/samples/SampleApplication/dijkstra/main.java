package com.vuforia.samples.SampleApplication.dijkstra;

import java.io.IOException;
import java.util.ArrayList;


public class main {
	
	public static void main(String[] args) throws IOException {
		ArrayList<point> myList=new ArrayList<point>();
		dijkstra my=new dijkstra();
		int l=3,ll=4,s=4,t=39;
		System.out.println("���¥��"+l+" ���"+ll+" �յ�¥��"+s+" �յ�"+t);
		my.setST(l,s,ll,t);
		
		my.go();
	    myList=my.getMyPoints();
	    
	    for(int i=0;i<myList.size();i++) {
	    	System.out.println(myList.get(i).lc+" "+myList.get(i).id+" "+myList.get(i).x+" "+myList.get(i).y+" name:"+myList.get(i).name+" �Ƕ�:"+myList.get(i).angle+" ����:"+myList.get(i).dis);
	    }
	}
}
