/*
 * @(#)MemorySize.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.memory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openjdk.jol.Main;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
import org.openjdk.jol.vm.VM;

/**   
 * @title: MemorySize.java 
 * @package com.my.memory 
 * @description: TODO
 * @author: Administrator
 * @date: 2022年11月2日 上午9:25:26 
 * @version: V1.0   
*/
public class MemorySize {

	private int intVal = 99;
	private char aChar = '0';
	private char a1Char = '是';
	private long aLong = 11111L;
	private double aDouble = 2222D;
	private char[] chars = new char[]{'a','是','什','谁','他','我'};
	private String[] strings = new String[]{"Asaf","我我我"};
	private String string = "我我我";
	/**
	 *@title main 
	 *@description: TODO
	 *@author: Administrator
	 *@date: 2022年11月2日 上午9:25:27
	 *@param args
	 *@throws 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		/*Long one = new Long(1);
		Set<Long> ids = new HashSet<>(103572);
		Long[] longs = new Long[103572];
		for(int i=0;i<103572;i++){
			ids.add(new Long(i));
			longs[i] = new Long(i);
		}
		System.out.println(VM.current().details());
		System.out.println(ClassLayout.parseClass(Long.class).toPrintable());
		System.out.println(ClassLayout.parseInstance(longs));
		System.out.println(GraphLayout.parseInstance(ids).totalSize());
		System.out.println(GraphLayout.parseInstance(longs).totalSize());*/
		Map<String,String> map = new HashMap<>();
		/*map.put("a","1");
		map.put("b","1");
		map.put("c","1");*/
		System.out.println(VM.current().details());
		//System.out.println(ClassLayout.parseInstance(new MemorySize()).toPrintable());
		//System.out.println(ClassLayout.parseInstance(new Object()).toPrintable());
		System.out.println(ClassLayout.parseInstance(map).toPrintable());
		System.out.println(ClassLayout.parseInstance("map").toPrintable());
		//Main.main("internals","java.util.HashMap");
	}

}
