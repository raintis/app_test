package com.my.zip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtil {
	/**
	 * 解压到指定目录
	 */
	public static void unZipFiles(String zipPath, String descDir) throws IOException {
		unZipFiles(new File(zipPath), descDir);
	}

	/**
	 * 解压文件到指定目录
	 */
	@SuppressWarnings("rawtypes")
	public static void unZipFiles(File zipFile, String descDir) throws IOException {
		File pathFile = new File(descDir);
		if (!pathFile.exists()) {
			pathFile.mkdirs();
		}
		// 解决zip文件中有中文目录或者中文文件
		ZipFile zip = new ZipFile(zipFile, Charset.forName("GBK"));
		for (Enumeration entries = zip.entries(); entries.hasMoreElements();) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			if(!entry.getName().endsWith("jar")){
				continue;
			}
			String zipEntryName = entry.getName().split("/")[1];
			InputStream in = zip.getInputStream(entry);
			String outPath = (descDir + zipEntryName).replaceAll("\\*", "/");
			;
			// 判断路径是否存在,不存在则创建文件路径
			File file = new File(outPath/*.substring(0, outPath.lastIndexOf('/'))*/);
			if (file.exists()) {
				continue;
			}
			/*
			// 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
			if (new File(outPath).isDirectory()) {
				continue;
			}*/
			// 输出文件路径信息
			System.out.println(outPath);
			OutputStream out = new FileOutputStream(outPath);
			byte[] buf1 = new byte[1024];
			int len;
			while ((len = in.read(buf1)) > 0) {
				out.write(buf1, 0, len);
			}
			in.close();
			out.close();
		}
		zip.close();
		System.out.println("******************解压完毕********************");
	}

	public static void main(String[] args) throws IOException {
		/**
		 * 解压文件
		 */
	/*	File zipFile = new File("d:/资料.zip");
		String path = "d:/zipfile/";
		unZipFiles(zipFile, path);*/
		unzipTest();
	}
	
	private static void unzipTest() throws IOException{
		String[] pathes = new String[]{"C:\\biz-baselinev1.5\\zip\\biz","C:\\biz-baselinev1.5\\zip\\bos","C:\\biz-baselinev1.5\\zip\\trd"};
		String[] despathes = new String[]{"C:\\biz-baselinev1.5\\mservice\\lib\\biz\\","C:\\biz-baselinev1.5\\mservice\\lib\\bos\\","C:\\biz-baselinev1.5\\mservice\\lib\\trd\\"};
		int indx = 0;
		for(String path : pathes){
			File zipPath = new File(path);
			for(File jar : zipPath.listFiles()){
				unZipFiles(jar,despathes[indx]);
			}
			indx++;
		}
	}
}
