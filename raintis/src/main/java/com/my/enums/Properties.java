package com.my.enums;

final public  class Properties {

	public static void main(String[] args) {
		double d = 111111111.9999;
		//System.out.printf("%f",d);
		String prefix = "bcm_userdefine";
		String key = "bcm_userdefine_D_C";
		System.out.println(key.substring(prefix.length()+1));
	}
}


