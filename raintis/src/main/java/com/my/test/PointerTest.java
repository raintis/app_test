package com.my.test;

public class PointerTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Boolean isTrue = true;
		testPointerPostman(isTrue);
		System.out.println(isTrue);
		String a = "bbbb";
		testPointerPostmanStr(a);
		System.out.println(a);
	}

	public static void testPointerPostman(Boolean isTrue){
		isTrue = false;
	}
	
	public static void testPointerPostmanStr(String a){
		a = "aaaaa";
	}
	
}
