package com.my.guava;

import com.google.common.base.CharMatcher;

public class CharMatcherTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String s = "M_M05";
		String s2 = "M_M15";
		char[] chars = s2.toCharArray();
		StringBuilder digit = new StringBuilder();
		int firstDigitInd = -1;
		for(int i=0;i<chars.length;i++){
			if(Character.isDigit(chars[i]) /*&& c != '0'*/){
				digit.append(""+chars[i]);
				if(firstDigitInd == -1){
					firstDigitInd = i;
				}
			}
		}
		System.out.println(digit);
		int period = Integer.valueOf(digit.toString()).intValue();
		period = period -1;
		String newStr = s2.substring(0, firstDigitInd)+ (period > 9 ? period  : "0"+period);
		System.out.println(newStr);
	}

}
