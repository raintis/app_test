/*
 * @(#)ObjectRelaMemoryTest.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.biz;

import java.util.ArrayList;
import java.util.List;

/**   
 * @title: ObjectRelaMemoryTest.java 
 * @package com.my.biz 
 * @description: TODO
 * @author: Administrator
 * @date: 2023年2月17日 上午9:03:51 
 * @version: V1.0   
*/
public class ObjectRelaMemoryTest {

	/**
	 *@title main 
	 *@description: TODO
	 *@author: Administrator
	 *@date: 2023年2月17日 上午9:03:51
	 *@param args
	 *@throws 
	 */
	public static void main(String[] args) {
		List<Member> original = new ArrayList<>(5000);
		for(int i=0;i<5000;i++){
			original.add(new Member("Member-"+i,i));
		}
		
		List<List<Member>> lightCopy = new ArrayList<>();
		List<List<Member>> deepCopy = new ArrayList<>();
		int totalCopy = 100;
		for(int i = 0;i<totalCopy;i++){
			lightCopy.add(lightCopy(original));
			deepCopy.add(deepCopy(original));
		}
		
		System.out.println(lightCopy.size()+deepCopy.size());
		try {
			Thread.sleep(120000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static List<Member> lightCopy(List<Member> original){
		List<Member> copy = new ArrayList<>(original.size());
		copy.addAll(original);
		return copy;
	}
	
	private static List<Member> deepCopy(List<Member> original){
		List<Member> copy = new ArrayList<>(original.size());
		for(Member m : original){
			copy.add(new Member(m.number,m.id));
		}
		return copy;
	}
	
	static class Member{
		String number;
		int id;
		
		Member(String number,int id){
			this.id = id;
			this.number = number;
		}
	}
}
