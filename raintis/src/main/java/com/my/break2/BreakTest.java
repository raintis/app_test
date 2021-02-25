package com.my.break2;

import com.google.common.base.Joiner;

public class BreakTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String casev = "1";
		int v = 1;
		switch(v){
			case 1:
				System.out.println("case1");
			case 2:
				System.out.println("case2");
		}
		System.out.println("esp(\""+com.google.common.base.Joiner.on("\",\"").join("aa", "bb", "cc").toString()+"\")");
	}

}
