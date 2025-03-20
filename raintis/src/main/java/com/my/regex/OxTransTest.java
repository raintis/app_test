/*
 * @(#)OxTransTest.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.regex;

import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

/**   
 * @title: OxTransTest.java 
 * @package com.my.regex 
 * @description: TODO
 * @author: Administrator
 * @date: 2022年12月13日 上午11:18:12 
 * @version: V1.0   
*/
public class OxTransTest {

	private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	/**
	 *@title main 
	 *@description: TODO
	 *@author: Administrator
	 *@date: 2022年12月13日 上午11:18:13
	 *@param args
	 *@throws 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String original = "12212"+"_" + "业务规则编码";
		String encode = Base64.getEncoder().encodeToString(original.getBytes());
		System.out.println(encode);
		//System.out.println(Long.decode(encode));
		System.out.println(Arrays.toString(encode.getBytes()));
		UUID uuidseed = UUID.nameUUIDFromBytes(encode.getBytes());
		System.out.println(uuidseed);
		String uuid = uuidseed.toString().replaceAll("-", "");
		long longuuid = hexToId(uuid);
		System.out.println(longuuid);
		System.out.println(idToHex(longuuid));
	}

	 public static long hexToId(String hexString) {
        int length = hexString.length();
        if (length >= 1 && length <= 32) {
            int beginIndex = length > 16 ? length - 16 : 0;
            return hexToId(hexString, beginIndex);
        } else {
            throw new IllegalArgumentException("Malformed id: " + hexString);
        }
    }
	 
	 public static long hexToId(String lowerHex, int index) {
	        long result = 0L;

	        for(int endIndex = Math.min(index + 16, lowerHex.length()); index < endIndex; ++index) {
	            char c = lowerHex.charAt(index);
	            result <<= 4;
	            if (c >= '0' && c <= '9') {
	                result |= (long)(c - 48);
	            } else {
	                if (c < 'a' || c > 'f') {
	                    throw new IllegalArgumentException("Malformed id: " + lowerHex);
	                }

	                result |= (long)(c - 97 + 10);
	            }
	        }

	        return result;
	    }
	 
	 public static String idToHex(long id) {
	        char[] data = new char[16];
	        writeHexLong(data, 0, id);
	        return new String(data);
	    }
	 
	 static void writeHexLong(char[] data, int pos, long v) {
	        writeHexByte(data, pos + 0, (byte)((int)(v >>> 56 & 255L)));
	        writeHexByte(data, pos + 2, (byte)((int)(v >>> 48 & 255L)));
	        writeHexByte(data, pos + 4, (byte)((int)(v >>> 40 & 255L)));
	        writeHexByte(data, pos + 6, (byte)((int)(v >>> 32 & 255L)));
	        writeHexByte(data, pos + 8, (byte)((int)(v >>> 24 & 255L)));
	        writeHexByte(data, pos + 10, (byte)((int)(v >>> 16 & 255L)));
	        writeHexByte(data, pos + 12, (byte)((int)(v >>> 8 & 255L)));
	        writeHexByte(data, pos + 14, (byte)((int)(v & 255L)));
	    }

	    static void writeHexByte(char[] data, int pos, byte b) {
	        data[pos + 0] = HEX_DIGITS[b >> 4 & 15];
	        data[pos + 1] = HEX_DIGITS[b & 15];
	    }
}
