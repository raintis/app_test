package com.my.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUnitTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//根据枚举类型为数据输入的单位进行转换
		System.out.println(TimeUnit.HOURS.toSeconds(1L));
		System.out.println(TimeUnit.SECONDS.toHours(3600));
		//根据给定的参数类型值转换成枚举类型单位数
		System.out.println(TimeUnit.HOURS.convert(1, TimeUnit.DAYS));
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(format.format(new Date(1534409439000L)));
	}

}
