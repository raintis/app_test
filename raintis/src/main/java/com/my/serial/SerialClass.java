package com.my.serial;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;

/**
 * desciption:实现Closeable关闭资源接口，只有在try（）里才会自动调度close方法，否则是不会进行关闭操作
 * @author Administrator
 *
 */
public class SerialClass implements Serializable,Closeable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int i = 0;
	public String name;

	public SerialClass(int i,String name){
		this.i = i;
		this.name = name;
	}

	@Override
	public void close() throws IOException {
		System.out.println("close resouce");
	}
}