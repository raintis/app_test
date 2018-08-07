package com.my.guava;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Ordering;

public class OrderingTest {

	public static void main(String[] args) {
		HashMultimap<Integer,String> map = HashMultimap.create();
		map.put(1, "a");
		map.put(1, "b");
		map.put(-3, "e");
		map.put(3, "f");
		map.put(1, "c");
		map.put(2, "d");
		map.put(7, "f");
		map.put(5, "c");
		map.put(4, "d");
		System.out.println(map);
		
		List<Integer> sort = Ordering.natural().sortedCopy(map.asMap().keySet());
		System.out.println(Arrays.toString(sort.toArray()));
	}
}
