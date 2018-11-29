package com.my.gc;

public class EdenAllocationTest {

	private static final int _1MB = 1024 * 1024;

	public static void main(String[] args) {
		byte[] allocation1 = new byte[2 * _1MB];
		byte[] allocation2 = new byte[2 * _1MB];
		byte[] allocation3 = new byte[2 * _1MB];
		byte[] allocation4 = new byte[3 * _1MB];
		System.out.println(System.getProperty("java.ext.dirs"));
	}

}
