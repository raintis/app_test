package com.my.bitset;

import java.nio.charset.Charset;

import org.apache.commons.lang3.RandomStringUtils;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

public class BloonFilterTest {

	public static void main(String[] args) {
		BloomFilter<String> filter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), 10000000, 0.001F);
		String randomStr;
		long start = System.currentTimeMillis();
		for(int i=0;i<100000;i++){
			randomStr = RandomStringUtils.randomAlphanumeric(10);
			if(!filter.mightContain(randomStr)){
				filter.put(randomStr);
			}else{
				System.out.println(randomStr);
			}
			if(i%1000 == 0){
				System.out.println(String.format("--%d hit String -->%s",i, randomStr));
			}
		}
		System.out.println("--span time -->" + (System.currentTimeMillis()-start));
	}
}
