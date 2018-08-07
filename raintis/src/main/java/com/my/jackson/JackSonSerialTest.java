package com.my.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JackSonSerialTest {

	public static void main(String[] args) throws Exception {
		PackageList l = new PackageList();
		l.addPackage(new APackage("1","王丽"));
		l.addPackage(new APackage("2","张三"));
		ObjectMapper objectMapper = new ObjectMapper();
		String serial = objectMapper.writeValueAsString(l);
		System.out.println(serial);
		l = objectMapper.readValue(serial,PackageList.class);
		System.out.println(l);
	}

}
