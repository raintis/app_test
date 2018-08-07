package com.my.guava;

import java.util.Arrays;

import com.google.common.collect.HashMultimap;

public class HashMultimapTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		HashMultimap<String,String> kv = HashMultimap.create();
		kv.put("one", "w1");
		kv.put("one", "w2");
		kv.put("two", "w2");
		kv.put("two", "w2");
		kv.put("three", "w3");
		kv.put("three", "w3");
		kv.entries().forEach( e ->{
			System.out.print(e.getKey()+" ");
			System.out.print(e.getValue()+" ");
			//System.out.print(e.getValue().getClass().getName());
			System.out.println();
		});
		System.out.println(Arrays.toString(kv.get("one").toArray()));
		
		kv.asMap().entrySet().forEach(e ->{
			System.out.print(e.getKey() + "-->");
			System.out.println(Arrays.toString(e.getValue().toArray()));
		});
	}
}
