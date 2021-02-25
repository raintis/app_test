package com.my.system;

import java.util.Arrays;

import org.junit.Test;

import com.google.common.collect.HashMultimap;

public class ArrayCopyTest {

	@Test
	public void testArrayCopy(){
		String origin = "111!222!safa!rrr";
		String origin2 = "111!222!safa!frrr";
		String[] split = origin.split("!");
		String[] split2 = origin2.split("!");
		int min = Math.min(split.length, split2.length);
		int index = -1;
		for(int i=0;i<min;i++){
			if(!split[i].equals(split2[i])){
				break;
			}
			index = i;
		}
		System.out.println(String.join("!", Arrays.copyOfRange(split, 0,index+1)));
	}
	
	@Test
	public void testMultiMapTest(){
		HashMultimap<String, String> map = HashMultimap.create();
		map.put("AA", "BB");
		map.put("AA", "CC");
		map.put("BB", "DD");
		map.put("BB", "FF");
		map.entries().forEach(e ->{
			System.out.println(String.format("k:%s v:%s", e.getKey(),e.getValue()));
		});
	}
}
