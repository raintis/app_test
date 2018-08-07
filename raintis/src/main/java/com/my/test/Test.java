package com.my.test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.primitives.Ints;

public class Test {
	static int[] one = {88};
	static int[] two = {1,2,3,4};
	public static void main(String[] args) {
		List<Integer> list = new ArrayList<>(Ints.asList(two));
		boolean b = list.removeAll(Ints.asList(one));
		System.out.println(b);
		System.out.println(list);
		System.out.println(String.format("long: %s",Long.MAX_VALUE));
		System.out.println(String.format("Integer: %s",BigInteger.valueOf(Long.MAX_VALUE)));
		
		int[] sort = Ints.concat(one,two);
		Arrays.sort(sort);
		System.out.println(Arrays.toString(sort));
		nulltest();
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
	}
	
	static class Data{
		public String toString(){
			return "data";
		}
	}
}
