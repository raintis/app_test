package com.my.generictype;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestGenericType {

	public static void main(String[] args) {
		IGenericType<String,String> type = getInstance();
		Map<String,String> map = new HashMap<>();
		map.putIfAbsent("aaa", "aaa");
		map.putIfAbsent("aaa", "bbb");
		System.out.println(map);
		testGrendar();
	}
	
	private static IGenericType<String,String> getInstance(){
		return  (s) -> {return "";};
	}
	
	
	private static void testGrendar(){
		LocalDate localDate = LocalDate.of(2019, 2, 28);
		ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        Instant instant1 = zonedDateTime.toInstant();
        Date from = Date.from(instant1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd mm:ss");
        System.out.println(format.format(from));
	}
}
