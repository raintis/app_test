package com.my.function;

/**
 * description: 函数指针的效果或委托
 * @author Administrator
 *
 */
public class FunctionProxy {

	public void remoteInvoke(){
		Remote proxy = new Remote();
		proxy.service(this::callBack);
	}
	
	public void callBack(String t,String r){
		System.out.println(t + "," + r);
	}
	
	private static class Remote{
		public void service(Function<String,String> f){
			System.out.println("execute remote --start");
			f.apply("hellow", " are you ok?");
			System.out.println("execute remote --end");
		}
	}

	@FunctionalInterface
	private interface Function<T,R>{
		public void apply(T t,R r);
	}
	
	public static void main(String[] args) {
		FunctionProxy remote = new FunctionProxy();
		remote.remoteInvoke();
	}
}
