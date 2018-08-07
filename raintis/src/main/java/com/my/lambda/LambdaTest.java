package com.my.lambda;

public class LambdaTest {

	public static void main(String[] args) {
		Lambda test = () -> System.out.println("stestst");
		Printer p = new Printer();
		p.print(test);
		
		String msg = String.format("haha %d", 111);
		System.out.println(msg);
	}

	static class Printer{
		public Printer(){}
		public void print(Lambda p){
			p.print();
		}
	}
	
	//@FunctionalInterface
	interface Lambda{
		void print();
		default void print(String dd){};
	}
}
