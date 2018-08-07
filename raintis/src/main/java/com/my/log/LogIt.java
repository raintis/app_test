package com.my.log;

import java.util.Arrays;

public class LogIt {

	public static void log(String msg){
		System.out.println(new Exception().getStackTrace()[2] + ":" + msg);
	}
}
