package com.my.serial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * description:对象序列化时如果要转成成字符串，则必须调用Base64方式处理，否则在反序列化时会报初始化文件头错误:invalid stream
 * header:EFBFBDEF
 * 
 * @author Administrator
 *
 */
public class ObjectSerialTest {

	public static void main(String[] args) throws IOException {
		String serial = serial();
		deSerial(serial);
	}

	private static String serial() {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream(); ObjectOutput oo = new ObjectOutputStream(os)) {
			oo.writeObject(new SerialClass(10, "豆豆我问玩儿玩儿玩儿玩儿完任务二万人玩儿玩儿完任务任务任务二"));
			String serial = new String(Base64.getEncoder().encode(os.toByteArray()));
			System.out.println("before compress lenth:" + serial.length());
			serial = compress(serial);
			System.out.println("after compress lenth:" + serial.length());
			return serial;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void deSerial(String serial) throws IOException {
		System.out.println("before uncompress lenth:" + serial.length());
		serial = uncompress(serial);
		System.out.println("after uncompress lenth:" + serial.length());
		try (ByteArrayInputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(serial));
				ObjectInput io = new ObjectInputStream(is);
				SerialClass clazz = (SerialClass) io.readObject();) {

			System.out.println(clazz.name + "->" + clazz.i);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

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
		    ByteArrayInputStream in = new ByteArrayInputStream(str   
		        .getBytes("gbk"));   
		    GZIPInputStream gunzip = new GZIPInputStream(in);   
		    byte[] buffer = new byte[256];   
		    int n;   
		    while ((n = gunzip.read(buffer)) != -1) {   
		      out.write(buffer, 0, n);   
		    }   
		    // toString()使用平台默认编码，也可以显式的指定如toString(&quot;GBK&quot;)  
		    return out.toString();   
		  }

}
