package com.my.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	}

}
