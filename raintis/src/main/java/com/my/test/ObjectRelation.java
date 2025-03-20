/*
 * @(#)ObjectRelation.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.test;

/**   
 * @title: ObjectRelation.java 
 * @package com.my.test 
 * @description: TODO
 * @author: Administrator
 * @date: 2021年4月2日 上午10:04:03 
 * @version: V1.0   
*/
public class ObjectRelation {

	public static void main(String[] args) {
		Object j = new Object();
		setObject2Null(j);
		System.out.println(j);
		String[] fy = new String[]{"FY2021","FY2020","FY2019"};
		String fy_cmp = "FY2020";
		for(String f : fy){
			System.out.println(f + "-p-" + fy_cmp + " = " + f.compareTo(fy_cmp));
		}
	}
	
	private static void setObject2Null(Object obj){
		System.out.println(obj);
		obj = null;
		System.out.println(obj);
	}
}
