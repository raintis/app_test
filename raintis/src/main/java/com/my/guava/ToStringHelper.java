package com.my.guava;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

public class ToStringHelper {

	public static void main(String[] args) {
		List<Integer> numbers = Lists.newArrayList(10,20,5,8,55,99,45);
		System.out.println(MoreObjects.toStringHelper("").add("value", numbers));
		Counter sum = new Counter();
		numbers.forEach(n ->{
			sum.add(n);
		});
		System.out.println(sum.sum);
		sum.reSet();
		
		Consumer<String> print = System.out::println;
		//可以做成执行链条，传入上下文，执行一系列任务
		Consumer<Integer> before = b -> {sum.add(b);print.accept("before");;};
		Consumer<Integer> s = before.andThen(a -> {sum.add(a);print.accept("after1");});
		s = s.andThen(a -> {sum.add(a);print.accept("after2");});
		s.accept(1);
		System.out.println(sum.sum);
		
		System.out.println(ReflectionToStringBuilder.toString(numbers));
		
		int[] ids = {1, 2, 3, 4, 5};  
		System.arraycopy(ids, 0, ids, 3, 2);  
	    System.out.println(Arrays.toString(ids)); // [1, 2, 3, 1, 2]  
	    mapToString();
	}
	
	private static void mapToString(){
		Map<Object,Object> map = new HashMap<>();
		map.put("343434", "tttt");
		map.put("3434324", "tttt");
		map.put("34f343f4", "tttt");
		map.put("343sf434", "tttt");
		map.put("343sa434", "tttt");
		map.put("343a4fa34", "tttt");
		System.out.println(map);
	}
	
	static class Counter{
		int sum = 0;
		void add(int v){
			sum+=v;
		}
		int getSum(){
			return sum;
		}
		void reSet(){
			sum = 0;
		}
	}
}
