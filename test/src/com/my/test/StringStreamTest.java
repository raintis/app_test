package com.my.test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;  
public class StringStreamTest {  
    public static void main(String[] args) {  
        String str = "abcdefghijklmn";  
        transform(str);  
    }  
    public static void transform(String str) {  
        StringReader sr = new StringReader(str);  
        StringWriter sw = new StringWriter();  
        char[] chars = new char[1024];  
        try {  
            int len = 0;  
            while ((len = sr.read(chars)) != -1) {  
                String strRead = new String(chars, 0, len).toUpperCase();  
                System.out.println(strRead);  
                sw.write(strRead);  
                sw.flush();  
            }  
            sr.close();  
            sw.close();  
            System.out.println(sw.toString());
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            sr.close();  
            try {  
                sw.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
}  
