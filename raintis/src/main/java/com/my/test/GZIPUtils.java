/*
 * @(#)GZIPUtils.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**   
 * @title: GZIPUtils.java 
 * @package com.my.test 
 * @description: TODO
 * @author: Administrator
 * @date: 2021年4月2日 下午2:52:38 
 * @version: V1.0   
*/
public class GZIPUtils {

	/**
	 * @throws Exception 
	 *@title main 
	 *@description: TODO
	 *@author: Administrator
	 *@date: 2021年4月2日 下午2:52:39
	 *@param args
	 *@throws 
	 */
	public static void main(String[] args) throws Exception {
		InputStreamReader is = new InputStreamReader(new FileInputStream("E:\\log\\data\\spreadjs.txt"));
		BufferedReader bf = new BufferedReader(is);
		String line = null;
		StringBuilder sb = new StringBuilder(10000000);
		while((line = bf.readLine())!=null){
			sb.append(line);
		}
		long starttime = System.currentTimeMillis();
		String press =compress(sb.toString());
		System.out.println(System.currentTimeMillis() - starttime);
		OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream("E:\\log\\data\\spreadjs1.txt"));
		os.write(press);
		is.close();
		os.close();
		OutputStreamWriter os2 = new OutputStreamWriter(new FileOutputStream("E:\\log\\data\\spreadjs2.txt"));
		os2.write(uncompress(press));
		os2.close();
	}

	public static String compress(String primStr) {
        if (primStr == null || primStr.length() == 0) {
            return primStr;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(primStr.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new sun.misc.BASE64Encoder().encode(out.toByteArray());
    }
 
    /**
     * 使用gzip进行解压缩
     */
    public static String uncompress(String compressedStr) {
        if (compressedStr == null) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = null;
        GZIPInputStream ginzip = null;
        byte[] compressed = null;
        String decompressed = null;
        try {
            compressed = new sun.misc.BASE64Decoder().decodeBuffer(compressedStr);
            in = new ByteArrayInputStream(compressed);
            ginzip = new GZIPInputStream(in);
 
            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ginzip != null) {
                try {
                    ginzip.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            try {
                out.close();
            } catch (IOException e) {
            }
        }
        return decompressed;
    }
}
