package com.my.test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.primitives.Ints;

//import my.com.agent.ObjectSizeFetcher;

public class Test {
	static int[] one = {88};
	static int[] two = {1,2,3,4};
	public static void main(String[] args) {
		
		Map<Object,Boolean> values = new HashMap<>();
		values.put("1", true);
		Object v = values.get("2");
		System.out.println(v);
		List<Integer> list = new ArrayList<>(Ints.asList(two));
		boolean b = list.removeAll(Ints.asList(one));
		System.out.println(b);
		System.out.println(list);
		System.out.println(String.format("long: %s",Long.MAX_VALUE));
		System.out.println(String.format("Integer: %s",BigInteger.valueOf(Long.MAX_VALUE)));
		
		int[] sort = Ints.concat(one,two);
		Arrays.sort(sort);
		System.out.println(Arrays.toString(sort));
		System.out.println("with _" + "AB".contains("_"));
		nulltest();
		System.out.println("=================bitset================");
		bitSetTest();
		
		System.out.println(String.format("--------------------cpu[%d]-----------",Runtime.getRuntime().availableProcessors()));
		
		System.out.println("=====================object-size============");
		testObjectSize();
	}
	
	private static void nulltest(){
		List<Data> list = new ArrayList<>();
		list.add(new Data());
		list.add(new Data());
		list.add(new Data());
		list.add(new Data());
		System.out.println(Arrays.toString(list.toArray()));
		list.forEach(e ->{
			e = null;
		});
		System.out.println(Arrays.toString(list.toArray()));
		System.out.println("==============================");
		list.stream().limit(2).forEach(e ->{
			System.out.println(e);
		});
	}
	
	private static void testObjectSize(){
		try {
			String str = "1234567890";
			Set<String> set = new HashSet<>();
			set.add("fasdfasf");
			set.add("323");
			set.add("fasdf23asf");
			//System.out.println(ObjectSizeFetcher.getObjectSize("1234567890"));
			//System.out.println(ObjectSizeFetcher.getObjectSize(set));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void bitSetTest(){
		BitSet bit = new BitSet();
		bit.set(10);
		bit.set(20);
		bit.set(30);
		bit.set(40);
		System.out.println("cardinality--" + bit.cardinality());
		System.out.println(Arrays.toString(bit.toLongArray()));
		bit.stream().forEach(c ->{
			System.out.println(c);
		});
		BitSet bit2 = new BitSet();
		bit2.set(1);
		bit2.set(2);
		bit2.set(3);
		bit2.set(4);
		bit.or(bit2);
		bit.stream().forEach(c ->{
			System.out.println(c);
		});
	}
	
	static class Data{
		public String toString(){
			return "data";
		}
	}
}
