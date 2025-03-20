/*
 * @(#)MethodTest.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**   
 * @title: MethodTest.java 
 * @package com.my.reflect 
 * @description: TODO
 * @author: Administrator
 * @date: 2022年9月9日 上午9:15:15 
 * @version: V1.0   
*/
public class MethodTest {

	/**
	 *@title main 
	 *@description: TODO
	 *@author: Administrator
	 *@date: 2022年9月9日 上午9:15:15
	 *@param args
	 *@throws 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Method[] methods = String.class.getMethods();
		for(Method m : methods){
			System.out.println(m.getName() + ":" + m.getModifiers()+",isPublic:"+Modifier.isPublic(m.getModifiers()));
		}
	}

}
