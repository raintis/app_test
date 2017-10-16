package com.my.md5.autocloseable;

/**
 * description:实现接口AutoCloseable代表为可关闭的资源，那么在try语句时增加括号进行包装，最后一句无需增加分号，
 * 那么不用再使用finally来进行资源关闭，简化代码也防止忘记的资源关闭
 * @author Administrator
 *
 */
public class MyResource implements AutoCloseable{

	@Override
	public void close() throws Exception {
		System.out.println("关闭资源");
	}

	public void readResource(){
		System.out.println("读取资源");
	}
	
	
	public static void main(String[] args) {
		try(MyResource rs = new MyResource()){
			rs.readResource();
		}catch(Exception e){
			System.out.println(e);
		}finally{
			System.out.println("finally");
		}
	}
}
