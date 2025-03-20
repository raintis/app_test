package com.my.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.google.common.io.Files;

//将一个字符串按照zip方式压缩和解压缩  
public class ZipUtil {

	// 压缩
	public static String compress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(str.getBytes());
		gzip.close();
		return out.toString("ISO-8859-1");
	}

	// 解压缩
	public static String uncompress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		// toString()使用平台默认编码，也可以显式的指定如toString("GBK")
		return out.toString();
	}

	public static String uncompress(byte[] b)throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(b);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		// toString()使用平台默认编码，也可以显式的指定如toString("GBK")
		return out.toString();
	}
	
	// 测试方法
	public static void main(String[] args) throws IOException {
//		URL url = ZipUtil.class.getResource("/compress/uncompress.txt");
		String read = Files.toString(new File("E:\\log\\港口\\sdcompressreport.json"), Charsets.UTF_8);
		//Stopwatch watch = Stopwatch.createStarted();
		//String compress = ZipUtil.compress(read);
		 //System.out.println("compress time -->"+watch.toString());
		//System.out.println(compress);
		long start = System.currentTimeMillis();
		String uncompress = ZipUtil.uncompress(Base64.getDecoder().decode(read));
		System.out.println(String.format("unpress time -->%d", (System.currentTimeMillis() - start)));
		//System.out.println();
//		Files.write(uncompress.getBytes(), new File("c:\\uncompress_zip.txt"));
		Files.write(uncompress.getBytes(), new File("E:\\log\\港口\\sduncompressreport.txt"));
	}

}