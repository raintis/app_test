/*
 * @(#)SyncroTest.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.test;

import java.util.Set;

import com.google.common.collect.Sets;

/**   
 * @title: SyncroTest.java 
 * @package com.my.test 
 * @description: TODO
 * @author: Administrator
 * @date: 2021年4月9日 下午2:06:50 
 * @version: V1.0   
*/
public class SyncroTest {

	/**
	 *@title main 
	 *@description: TODO
	 *@author: Administrator
	 *@date: 2021年4月9日 下午2:06:50
	 *@param args
	 *@throws 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SyncroObject obj = new SyncroObject();
		for(int i=0;i<100;i++){
			new Thread(() ->{
				obj.iterator();
			}).start();
			new Thread(() ->{
				obj.remove("1");
			}).start();
		}
	}

	private static class SyncroObject{
		private Set<String> keys = Sets.newHashSet("1","2","3");
		
		void remove(String key){
			System.out.println("remove");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			keys.remove(key);
		}
		
		void iterator(){
			synchronized(keys){
				keys.forEach(e ->{
					try {
						System.out.println(e);
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});
			}
		}
	}
}
