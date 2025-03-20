/*
 * @(#)JIOFileReader.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**   
 * @title: JIOFileReader.java 
 * @package com.my.io 
 * @description: TODO
 * @author: Administrator
 * @date: 2022年10月14日 上午10:29:45 
 * @version: V1.0   
*/
public class JIOFileReader {

	/**
	 *@title main 
	 *@description: TODO
	 *@author: Administrator
	 *@date: 2022年10月14日 上午10:29:45
	 *@param args
	 *@throws 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		/*try(InputStreamReader input = new FileReader("E:\\jiuqi\\GZQY2020.jio");
				BufferedReader reader = new BufferedReader(input);){
			String line;
			while((line = reader.readLine()) != null){
				System.out.println(line);
			}
			char[] buff = new char[1024];
			while(reader.read(buff) != -1){
				System.out.println(buff);
			}
		}*/
		testListCopy();
		
	}
	
	private static void testListCopy(){
		List<Integer> list = Arrays.asList(1,2,3,4,5);
		List<List<Integer>> sub = Collections.nCopies(3, list);
		System.out.println(Arrays.toString(sub.toArray()));
		System.out.println(Arrays.toString(list.subList(0, 2).toArray()));
	}

}
 