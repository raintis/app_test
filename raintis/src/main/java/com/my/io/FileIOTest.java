package com.my.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;

public class FileIOTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		LineNumberReader rd = new LineNumberReader( new InputStreamReader(new FileInputStream(new File("E:\\jiuqi\\f2.dbf"))));
		String line = null;
		System.out.println(System.getProperty("file.encoding"));
		System.out.println(System.getProperty("sun.jnu.encoding")); 
		while((line=rd.readLine())!= null){
			System.out.println(line);
			System.out.println(new String(line.getBytes("UTF-8"),"GB2312"));
		}
		rd.close();
		
		convertFormat();
	}

	
	private static void  convertFormat() throws Exception{
		File srcFile = new File("E:\\jiuqi\\f2.dbf");
		File destFile = new File("E:\\jiuqi\\f2.txt");
		InputStreamReader isr = new InputStreamReader(new FileInputStream(srcFile), "GBK"); //ANSI编码
		OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8"); //存为UTF-8
				
		int len = isr.read();
		while(-1 != len)
		{
 
			osw.write(len);
			len = isr.read();
		}
		//刷新缓冲区的数据，强制写入目标文件
		osw.flush();
		osw.close();
		isr.close();
	}
}
