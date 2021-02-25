package com.my.guava;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class MurmurHash3Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String radom = "fasdfalsdfjlasjdfljafljaldjflajdflajdlfjaljdflajdfjadslfjalsfjljdf";
		Random r = new Random();
		Set<String> hashSet = new HashSet<>(1000000);
		Map<String,String> map = new HashMap<>();
		String randomStr,randomCode;
		long start = System.currentTimeMillis();
		HashFunction hasher = Hashing.murmur3_32(7);
		for(int i = 0;i<1000000;i++){
			randomStr = r.nextDouble()+radom + r.nextDouble();
			randomCode = hasher.hashString(randomStr, StandardCharsets.UTF_8).toString();
			//System.out.println(randomCode+"->"+randomStr);
			if(!hashSet.add(randomCode)){
				System.out.println(String.format("str->%s,code->%s,p-str->%s", randomStr,randomCode,map.get(randomCode)));
			}
			map.put(randomCode, randomStr);
		}
		System.out.println(hashSet.size());
		System.out.println("spantime->"+(System.currentTimeMillis() - start));
	}

}
