package com.my.guava;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;

public class TreeMultiMapTest {

	public static void main(String[] args) {
		List<String> sortList = Arrays.asList("22","33","99","44","00");
		Comparator<List<String>> cp = (s1,s2) -> s1.toString().compareTo(s2.toString());
		TreeMultimap<Integer, List<String>> multiMap = TreeMultimap.create(Ordering.natural(),Ordering.from(cp));
		TreeMultimap<Integer,Integer> map = TreeMultimap.create();
		map.put(1, 2);
		map.put(1, 3);
		map.put(1, 4);
		map.put(1, 5);
		map.put(1, 5);
		map.put(2, 5);
		map.put(2, 6);
		System.out.println(map);
		System.out.println(map.values());
		
		TreeMultimap<String,String> map2 = TreeMultimap.create((s1,s2) -> s1.compareTo(s2),Ordering.natural());
		map2.put("3", "2");
		map2.put("3", "3");
		map2.put("4", "4");
		map2.put("4", "5");
		map2.put("4", "5");
		map2.put("2", "5");
		map2.put("2", "6");
		System.out.println(map2);
	}
}
