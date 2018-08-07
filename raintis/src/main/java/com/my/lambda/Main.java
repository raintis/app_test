package com.my.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Main {

	Runnable r1 = () -> {
		System.out.println(this);
	};
	Runnable r2 = () -> {
		System.out.println(toString());
	};

	@Override
	public String toString() {
		return "Hello, world";
	}

	public static void main(String... args) {
		new Main().r1.run();
		new Main().r2.run();

		int sum = 0;
		List<Integer> numbers = Arrays.asList(new Integer[] { 1, 2, 3, 5 });
		sum = numbers.stream().mapToInt(n -> n.intValue()).sum();
		System.out.println(sum);
		referenceLambda();
	}
	
	private static void referenceLambda(){
		Consumer<String> print = System.out::println;
		print.accept("lambda function");
		//print.andThen(System.out::);
		executeReferenceFun(print,"are you ok !");
		Consumer<Integer[]> sort = Arrays::sort;
		Integer[] sorts = new Integer[] { 4, 2, 3, 5 };
		/*sort.accept(sorts);
		for(Integer i : sorts){
			System.out.println(i);
		}*/
		executeReferenceFun(sort,sorts);
		for(Integer i : sorts){
			System.out.println(i);
		}
		
		Consumer<Integer> num = Integer::new;
		num.accept(11);
		//num.andThen(20);
	}
	
	private static <T> void executeReferenceFun(Consumer<T> c,T acct){
		c.accept(acct);
	}
}
