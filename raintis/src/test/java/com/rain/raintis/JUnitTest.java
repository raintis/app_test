package com.rain.raintis;

import org.junit.AfterClass;
import org.junit.Test;

public class JUnitTest {

	@Test
	public void test(){
		System.out.println("@test");
	}
	
	@AfterClass
	public static void afterClass(){
		System.out.println("@AfterClass");
	}
	
	public void staticprofix(){
		final int p = 10;
	}
}
