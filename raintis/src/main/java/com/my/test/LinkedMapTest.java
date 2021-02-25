package com.my.test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class LinkedMapTest {

	public static void main(String[] args) {
		List<String> list = new ArrayList<>();
		list.add("a");
		list.add("c");
		list.add("d");
		list.add(1, "b");
		System.out.println(list.toString());
		test();
		testLRU();
	}

	private static void test(){
		boolean bool = true;
		bool |= false;
		System.out.println(bool);
	}
	
	private static void testLRU(){
		LinkedHashMap<Integer, String> lruMap = new LinkedHashMap<Integer, String>(3) {
			private static final long serialVersionUID = 1L;
			private int capacity = 100;
			{
			    capacity = 3;
			}
			protected boolean removeEldestEntry(java.util.Map.Entry<Integer, String> eldest) {
				//当链表元素大于容量时，移除最老（最久未被使用）的元素
				return size() > capacity;
			}
		};
		lruMap.put(1, "1");
		lruMap.put(2, "2");
		lruMap.put(3, "3");
		lruMap.put(4, "4");
		System.out.println("-----------" + lruMap);
	}
}
