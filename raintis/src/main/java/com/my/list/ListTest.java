package com.my.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import com.google.common.collect.Lists;

public class ListTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<String,String> map = new HashMap<>();
		map.put("11", "aa");
		map.put("12", "bb");
		map.put("13", "cc");
		map.put("14", "dd");
		List<String> list = Lists.transform(new ArrayList<String>(map.keySet()), v1 ->{
			return v1;
		});
		System.out.println(Arrays.toString(list.toArray()));
		System.out.println(String.join("_", "addd"));
		
		/*List<String> list2 = Arrays.asList("1","2");
		list2.set(1, "3");
		list2.remove(1);
		System.out.println(list2);*/
		test();
		testClone();
	}

	private static void  test(){
		String compress = "base64jjljljsdfljsljflsjfljsdfjlsjlfsljflsjfljslfjsljfsljfjls";
		String base64 = compress.substring(0, 6);
		System.out.println("base:" + base64);
		System.out.println("content:" +compress.substring(6));
		
		Stack<String> stack = new Stack<>();
		stack.push("A");
		stack.push("B");
		stack.push("C");
		List<String> list = new ArrayList<>(stack);
		System.out.println(list);
		Collections.reverse(list);
		System.out.println(list);
		
		
		Object[] args = new Object[]{"sfasdf",8989};
		System.out.println(args.getClass().isArray());
	}
	
	private static void testClone(){
		CloneObject cb = new CloneObject();
		cb.name = "rere";
		
		CloneObject copy = cb.clone();
		copy.persons.add("fsafasfaf");
		System.out.println(Arrays.toString(cb.persons.toArray()));
		System.out.println(Arrays.toString(copy.persons.toArray()));
	}
}
