/*
 * @(#)NormalizeTest.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.normalize;

import java.text.Normalizer;

/**   
 * @title: NormalizeTest.java 
 * @package com.my.normalize 
 * @description: TODO
 * @author: Administrator
 * @date: 2021年9月13日 下午8:03:13 
 * @version: V1.0   
*/
public class NormalizeTest {

	/**
	 *@title main 
	 *@description: TODO
	 *@author: Administrator
	 *@date: 2021年9月13日 下午8:03:14
	 *@param args
	 *@throws 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String script = "《》！@#￥￥%“”＜＞";
		script = Normalizer.normalize(script,Normalizer.Form.NFKC);
		System.out.println(script);
	}

}
