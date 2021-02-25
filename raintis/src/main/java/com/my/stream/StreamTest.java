package com.my.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Stream.iterate(0, item -> item + 1).limit(10).forEach(System.out::println);
		//Stream.of("1","2","3","4","5").map(e -> Integer.valueOf(e)).forEach(System.out::print);
		//Stream.of("1","2","3","4","5").mapToInt(e -> Integer.valueOf(e)).forEach(System.out::print);
		//Stream.of("1","2","3","4","5").flatMap(e -> Stream.of(e,Math.random())).forEach(e -> System.out.println("n:" +e));
		
		/*Stream.of("1","2","3","4","5").peek(e -> System.out.println(e+Math.random())).forEach(System.out::println);
		
		Integer one = 10;
		Integer two = 20;
		BinaryOperator<Integer> oprt = BinaryOperator.minBy((o1,o2) -> o1.intValue() - o2.intValue());
		System.out.println("min data is :" + oprt.apply(one, two));
		System.out.println("max data is :" + maxBy(BinaryOperator.maxBy((o1,o2) -> o1.intValue() - o2.intValue()),one,two));
		IntStreamTest();*/
		IntStreamTest2();
		
		System.out.println(Arrays.toString(Arrays.asList(1,2,3,4).stream().map(i -> i.toString()).collect(Collectors.toList()).toArray()));
		
		/*List<String> ids = new ArrayList<>();
		ids.add("111");
		ids.add("222");
		int i = 0;
		for(String id : ids){
			if(i == 0){
				ids.add("ending");
				i++;
			}
			System.out.println(id);
		}*/
		
		String a = new String("hhh");
		   String b = new String("hhh");

		   System.out.println(System.identityHashCode(a));
		   System.out.println(System.identityHashCode(b));
		   System.out.println(a.hashCode());
		   System.out.println(b.hashCode());
	}

	private static Integer maxBy(BinaryOperator<Integer> oprt,Integer one,Integer two){
		return oprt.apply(one, two);
	}
	
	private static void IntStreamTest2(){
		IntStream.range(0, 9).boxed().collect(Collectors.toList()).forEach(e -> System.out.println(e));
	}
	
	private static void IntStreamTest(){
		System.out.println("IntStreamTest");
		IntStream.range(1, 50).forEach(e ->{
			System.out.println(e);
		});
		
		Stream<String> s = Stream.of("1s","2s","3s");
		String[] ss = s.toArray(String[]::new);
		System.out.println(Arrays.toString(ss));
		
		Stream<A> aa = Stream.of(new A(),new A());
		A[] as = aa.toArray(A[]::new);
		System.out.println(Arrays.toString(as));
		
		List<String> ls = Arrays.asList("222","333");
	}
	
	static class A{
		public String toString(){
			return "test";
		}
	}
}
