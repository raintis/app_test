package com.my.md5;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;  
  
/** 
 * ����MD5���ܽ��� 
 * @author tfq 
 * @datetime 2011-10-13 
 */  
public class MD5Util {  
  
    /*** 
     * MD5���� ����32λmd5�� 
     */  
    public static String string2MD5(String inStr){  
        MessageDigest md5 = null;  
        try{  
            md5 = MessageDigest.getInstance("MD5");  
        }catch (Exception e){  
            System.out.println(e.toString());  
            e.printStackTrace();  
            return "";  
        }  
        char[] charArray = inStr.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
  
        for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}  
        byte[] md5Bytes = md5.digest(byteArray);  
        StringBuffer hexValue = new StringBuffer();  
        for (int i = 0; i < md5Bytes.length; i++){  
            int val = ((int) md5Bytes[i]) & 0xff;  
            if (val < 16) {
				hexValue.append("0");
			}  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();  
  
    }  
  
    /** 
     * ���ܽ����㷨 ִ��һ�μ��ܣ����ν��� 
     */   
    public static String convertMD5(String inStr){  
  
        char[] a = inStr.toCharArray();  
        for (int i = 0; i < a.length; i++){  
            a[i] = (char) (a[i] ^ '1');  
        }  
        String s = new String(a);  
        return s;  
  
    }  
  
    // ����������  
    public static void main(String args[]) {  
        String s = new String("tangfuqiang");  
        System.out.println("原数据-->" + s);  
        String string2MD5 = string2MD5(s);
        System.out.println("MD5码-->" + string2MD5);  
        String convertMD5 = convertMD5(s);
        System.out.println("转换md5-->" + convertMD5);  
        System.out.println("是否相等-->"+convertMD5.equals(convertMD5(s)));
        System.out.println("解密-->" + convertMD5(convertMD5(s)));  
        String str = "fffsfl|fdsfsdf|dfsfafaf";
        String[] splitStr = str.split("\\|");
        for(String s1:splitStr){
        	System.out.println(s1);
        }
        String decode = getRandomString();
        System.out.println(string2MD5(decode));
        System.out.println(string2MD5(decode));
        System.out.println(string2MD5(decode));
        System.out.println(string2MD5(decode));
        
        //testEnCode();
        
        String[] cmp1 = getRandomStr(10000);
        String[] cmp2 = getRandomStr(10000);
        System.out.println("executeTimes:" +(cmp1.length * cmp2.length));
        long start = System.currentTimeMillis();
        for(String s1 : cmp1){
        	for(String s2 : cmp2){
        		if(s1.equals(s2)) {
					break;
				}
        	}
        }
        System.out.println("spanTime:" + (System.currentTimeMillis() - start));
    }  
    
    private static void testEnCode(){
    	Map<String,String> store = new HashMap<String,String>(100000);
    	long start = System.currentTimeMillis();
    	for(int i=0;i<1000000;i++){
    		String randomStr = getRandomString();
    		store.put(string2MD5(randomStr), randomStr);
    	}
    	System.out.println("MapSize-->" +store.size());
    	System.out.println("spantime-->" + (System.currentTimeMillis() - start));
    	System.out.println("Avgtime-->" + new BigDecimal(""+(System.currentTimeMillis() - start)).divide(new BigDecimal(""+store.size())));
    }
    
    public static String[] getRandomStr(int count){
    	String[] arr = new String[count];
    	for(int i=0;i<count;i++){
    		arr[i]= getRandomString();
    	}
    	return arr;
    }
    
    private static String getRandomString(){
		return RandomStringUtils.randomAlphanumeric(100);
	}
}  