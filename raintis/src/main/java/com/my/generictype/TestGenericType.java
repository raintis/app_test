package com.my.generictype;

import java.util.HashMap;
import java.util.Map;

public class TestGenericType {

	public static void main(String[] args) {
		IGenericType<String,String> type = getInstance();
		Map<String,String> map = new HashMap<>();
		map.putIfAbsent("aaa", "aaa");
		map.putIfAbsent("aaa", "bbb");
		System.out.println(map);
	}
	
	private static IGenericType<String,String> getInstance(){
		return  (s) -> {return "";};
	}
}
