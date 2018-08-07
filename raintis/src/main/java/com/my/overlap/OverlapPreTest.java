package com.my.overlap;

import java.util.HashMap;
import java.util.Map;

/**
 * description:判断两个区域是否存在交叉的情况
 * @author Administrator
 *
 */
public class OverlapPreTest {

	public static void main(String[] args) {
		Rec r1 = new Rec(1,1,2,2);
		Rec r2 = new Rec(2,2,3,3);
		System.out.println(isOverlap(r1,r2) ? "存在交叉" : "不存在交叉");
		System.out.println(isRight(r1,r2) ? "在右边" : "不在右边");
		
		Rec r3 = new Rec(1,5,3,7);
		Rec r4 = new Rec(1,6,2,6);
		System.out.println(isOverlap(r3,r4) ? "存在交叉" : "不存在交叉");
		System.out.println(isRight(r1,r2) ? "在右边" : "不在右边");
		
		Rec r5 = new Rec(1,10,4,16);
		Rec r6 = new Rec(4,9,5,18);
		System.out.println(isOverlap(r5,r6) ? "存在交叉" : "不存在交叉");
		
		Rec r7 = new Rec(1,21,3,23);
		Rec r8 = new Rec(3,22,5,25);
		System.out.println(isOverlap(r7,r8) ? "存在交叉" : "不存在交叉");
		
		Map<String,String> map = new HashMap<>();
		map.put("11", "11");
		boolean b = map.keySet().stream().anyMatch(p -> p.equals("11"));
		System.out.println("have one item equals 11 :" + b);
		
		String key="bcm_aaa";
		System.out.println(key.split("_")[1]);
	}
	
	private static boolean isOverlap(Rec p1,Rec p2){
		return Math.max(p1.x1,p2.x1) <= Math.min(p1.x2,p2.x2) && Math.max(p1.y1,p2.y1) <= Math.min(p1.y2,p2.y2);
	}
	
	private static boolean isRight(Rec p1,Rec p2){
		return Math.max(p1.y1,p2.y1) <= Math.min(p1.y2,p2.y2);
	}
	
	private static boolean isBottom(Rec p1,Rec p2){
		return Math.max(p1.x1,p2.x1) <= Math.min(p1.x2,p2.x2);
	}
	
	private static class Rec{
		int x1,y1,x2,y2;
		Rec(int x1,int y1,int x2,int y2){
			this.x1 = x1;
			this.x2 = x2;
			this.y1 = y1;
			this.y2 = y2;
		}
	}
}
