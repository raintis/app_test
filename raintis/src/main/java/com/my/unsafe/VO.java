package com.my.unsafe;

public class VO
{
	public int a = 0;
	
	public long b = 0;
	
	public static String c= "123";
	
	public static Object d= null;
	
	public static int e = 100;
	
	public static void main(String[] args) {
		String s1 = "abc";
		String s2 = new String("abc");
		System.out.println(s1==s2);
	}
}
