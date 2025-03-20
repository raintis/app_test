/*
 * @(#)RefrenceGCTest.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.thread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**   
 * @title: RefrenceGCTest.java 
 * @package com.my.thread 
 * @description: TODO
 * @author: Administrator
 * @date: 2022年8月23日 下午1:30:39 
 * @version: V1.0   
*/
public class RefrenceGCTest {

	/**
	 *@title main 
	 *@description: TODO
	 *@author: Administrator
	 *@date: 2022年8月23日 下午1:30:39
	 *@param args
	 *@throws 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Context ctx = new Context();
		final int size = ctx.bigData.size();
		new Thread(() ->{
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.gc();
			System.out.println(size);
			System.gc();
			System.out.println();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println();
		}).start();
		
	}

	private static class Context{
		private List<Integer> bigData = new ArrayList<>();
		
		Context(){
			for(int i=0;i<10000000;i++){
				bigData.add(i);
			}
		}
	}
}
