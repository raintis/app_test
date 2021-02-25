package com.my.generictype;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestParameterizedType {

	public static void main(String[] args) {
		List<Map<String,Integer>> test = new ArrayList<>();
		Map<String,Integer> one = new HashMap<>();
		one.put("one", 1);
		Map<String,Integer> two = new HashMap<>();
		two.put("two", 2);
		test.add(one);
		test.add(two);
		
		if(test.getClass().isAssignableFrom(ArrayList.class)){
			TypeVariable<?>[] fcs = test.getClass().getTypeParameters();
		}
	}

}
