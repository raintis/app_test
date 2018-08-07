package com.my.bitset;

import java.util.BitSet;

public class BitSetTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BitSet bs = new BitSet(63);
		bs.set(10, 30, true);
		System.out.println(bs.get(65));
	}

}
