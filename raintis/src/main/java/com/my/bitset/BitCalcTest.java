/*
 * @(#)BitCalcTest.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.bitset;

/**   
 * @title: BitCalcTest.java 
 * @package com.my.bitset 
 * @description: TODO
 * @author: Administrator
 * @date: 2023年1月10日 下午4:36:05 
 * @version: V1.0   
*/
public class BitCalcTest {

	/**
	 *@title main 
	 *@description: TODO
	 *@author: Administrator
	 *@date: 2023年1月10日 下午4:36:05
	 *@param args
	 *@throws 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(11>>1);
		System.out.println(-5>>3);
		System.out.println(Integer.toBinaryString(-15));
		System.out.println(Integer.toBinaryString(Double.valueOf(""+Math.pow(2, 31)).intValue()));
	}

}
