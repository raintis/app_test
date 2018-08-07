package com.my.md5;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Decoder;

   
   
public class Des {  
   
    private final static String DES = "DES";  
       
    /** 
     * Description ���ݼ�ֵ���м��� 
     * @param data  
     * @param key  ���ܼ�byte���� 
     * @return 
     * @throws Exception 
     */  
    public static String encrypt(String data, String key) throws Exception {  
        byte[] bt = encrypt(data.getBytes(), key.getBytes());  
        String strs = new  sun.misc.BASE64Encoder().encode(bt);  
        return strs;  
    }  
   
    /** 
     * Description ���ݼ�ֵ���н��� 
     * @param data 
     * @param key  ���ܼ�byte���� 
     * @return 
     * @throws IOException 
     * @throws Exception 
     */  
    public static String decrypt(String data, String key) throws IOException,  
            Exception {  
        if (data == null) {
			return null;
		}  
        BASE64Decoder decoder = new BASE64Decoder();  
        byte[] buf = decoder.decodeBuffer(data);  
        byte[] bt = decrypt(buf,key.getBytes());  
        return new String(bt);  
    }  
   
    /** 
     * Description ���ݼ�ֵ���м��� 
     * @param data 
     * @param key  ���ܼ�byte���� 
     * @return 
     * @throws Exception 
     */  
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {  
        // ����һ�������ε������Դ  
        SecureRandom sr = new SecureRandom();  
   
        // ��ԭʼ��Կ���ݴ���DESKeySpec����  
        DESKeySpec dks = new DESKeySpec(key);  
   
        // ����һ����Կ������Ȼ��������DESKeySpecת����SecretKey����  
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);  
        SecretKey securekey = keyFactory.generateSecret(dks);  
   
        // Cipher����ʵ����ɼ��ܲ���  
        Cipher cipher = Cipher.getInstance(DES);  
   
        // ����Կ��ʼ��Cipher����  
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);  
   
        return cipher.doFinal(data);  
    }  
       
       
    /** 
     * Description ���ݼ�ֵ���н��� 
     * @param data 
     * @param key  ���ܼ�byte���� 
     * @return 
     * @throws Exception 
     */  
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {  
        // ����һ�������ε������Դ  
        SecureRandom sr = new SecureRandom();  
   
        // ��ԭʼ��Կ���ݴ���DESKeySpec����  
        DESKeySpec dks = new DESKeySpec(key);  
   
        // ����һ����Կ������Ȼ��������DESKeySpecת����SecretKey����  
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);  
        SecretKey securekey = keyFactory.generateSecret(dks);  
   
        // Cipher����ʵ����ɽ��ܲ���  
        Cipher cipher = Cipher.getInstance(DES);  
   
        // ����Կ��ʼ��Cipher����  
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);  
   
        return cipher.doFinal(data);  
    }  
      
      
    /** 
     * Description ��ȡ�ַ���MD5ֵ 
     * @param sourceStr 
     */  
    private static String MD5(String sourceStr) {  
        String result = "";  
        try {  
            MessageDigest md = MessageDigest.getInstance("MD5");  
            md.update(sourceStr.getBytes());  
            byte b[] = md.digest();  
            int i;  
            StringBuffer buf = new StringBuffer("");  
            for (int offset = 0; offset < b.length; offset++) {  
                i = b[offset];  
                if (i < 0) {
					i += 256;
				}  
                if (i < 16) {
					buf.append("0");
				}  
                buf.append(Integer.toHexString(i));  
            }  
            result = buf.toString();  
            // System.out.println("MD5(" + sourceStr + ",32) = " + result);  
            // System.out.println("MD5(" + sourceStr + ",16) = " +  
            // buf.toString().substring(8, 24));  
        } catch (NoSuchAlgorithmException e) {  
           System.out.println(e);
        }  
        return result;  
    }  
      
      
    public static void main(String[] args) throws Exception {  
        String data = "{devType:\"1\",Sys:\"01\",Name:\"����\",PoId:\"000002\",TarPho:\"15527609770\",Desc:\"����͵��\"}";  
        String key = "12345678";//��Կ  
        long start = System.currentTimeMillis();
        String encode = encrypt(data, key);  
        System.err.println(encode);  
        System.out.println("spanTime:" + (System.currentTimeMillis() - start));
        String dcode = decrypt(encode, key);  
        System.err.println(dcode);  
    }  
}  
