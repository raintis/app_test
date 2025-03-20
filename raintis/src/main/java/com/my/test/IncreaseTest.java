/*
 * @(#)IncreaseTest.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.test;

import java.util.HashMap;
import java.util.Map;

/**   
 * @title: IncreaseTest.java 
 * @package com.my.test 
 * @description: TODO
 * @author: Administrator
 * @date: 2021年3月24日 下午2:07:26 
 * @version: V1.0   
*/
public class IncreaseTest {

	/**
	 *@title main 
	 *@description: TODO
	 *@author: Administrator
	 *@date: 2021年3月24日 下午2:07:27
	 *@param args
	 *@throws 
	 */
	public static void main(String[] args) {
		int index = 0;
		Map<String,Integer> map = new HashMap<>();
		do{
			map.put("idx:"+index, index);
			index++;
		}while(index <9);
		System.out.println(index);
		System.out.println(map);
		boolean istrue = true;
		testBoolean(istrue);
	}

	
	private static void testBoolean(boolean istrue){
		System.out.println(istrue);
	}
}
