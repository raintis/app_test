package com.my.sock;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class HttpClientTest {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		/*InputStream is = null;
		HttpClient client = null;
		BufferedReader read = null;
		InputStreamReader inputStreamReader = null;
		try{
		URL url = new URL("http://120.131.8.110:8888/StocksData/Transfer?companyName=%E6%B1%9F%E8%8B%8F%E6%96%B0%E6%97%A5%E7%94%B5%E5%8A%A8%E8%BD%A6%E8%82%A1%E4%BB%BD%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8&reportTime=2017-03-31&tableName=%E5%88%A9%E6%B6%A6%E8%A1%A8");
		client = HttpClient.New(url);
		is = client.getInputStream();
		inputStreamReader = new InputStreamReader(is);
		read = new BufferedReader(inputStreamReader);
		String line;
		while((line = read.readLine()) != null){
			System.out.println(line);
		}
		}catch(Exception e){
			System.out.println(e);
		}finally{
			
			if(read != null){
				read.close();
			}
			if(inputStreamReader != null){
				inputStreamReader.close();
			}
			if(is != null){
				is.close();
			}
			
		}*/
		doPost();
		//doGet();
	}

	private static void doPost()throws Exception{
		String parameterData = "companyName=%E6%B1%9F%E8%8B%8F%E6%96%B0%E6%97%A5%E7%94%B5%E5%8A%A8%E8%BD%A6%E8%82%A1%E4%BB%BD%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8&reportTime=2017-03-31&tableName=%E5%88%A9%E6%B6%A6%E8%A1%A8";
        
        URL localURL = new URL("http://120.131.8.110:8888/StocksData/Transfer");
        URLConnection connection = localURL.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
        
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        //httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(parameterData.length()));
        
        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;
        
        try {
            outputStream = httpURLConnection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);
            
            outputStreamWriter.write(parameterData.toString());
            outputStreamWriter.flush();
            
            if (httpURLConnection.getResponseCode() >= 300) {
                throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }
            
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            reader = new BufferedReader(inputStreamReader);
            
            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
            
        } finally {
            
            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }
            
            if (outputStream != null) {
                outputStream.close();
            }
            
            if (reader != null) {
                reader.close();
            }
            
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            
            if (inputStream != null) {
                inputStream.close();
            }
            
        }
        JSONObject obj =  JSON.parseObject(resultBuffer.toString());
        if(obj != null && obj.size() > 0){
        	Set<Map.Entry> entry = (((java.util.Map)((java.util.Map)obj.get("data")).get("result"))).entrySet();
        	for(Map.Entry e : entry){
        		System.out.println(e.getKey() + ":" +e.getValue());
        	}
        }
         System.out.println(resultBuffer.toString());
	}
	
	 public static void doGet() throws Exception {
	        URL localURL = new URL("http://120.131.8.110:8888/StocksData/Transfer?companyName=%E6%B1%9F%E8%8B%8F%E6%96%B0%E6%97%A5%E7%94%B5%E5%8A%A8%E8%BD%A6%E8%82%A1%E4%BB%BD%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8&reportTime=2017-03-31&tableName=%E5%88%A9%E6%B6%A6%E8%A1%A8");
	        URLConnection connection = localURL.openConnection();
	        HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
	        
	        httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
	        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        
	        InputStream inputStream = null;
	        InputStreamReader inputStreamReader = null;
	        BufferedReader reader = null;
	        StringBuffer resultBuffer = new StringBuffer();
	        String tempLine = null;
	        
	        if (httpURLConnection.getResponseCode() >= 300) {
	            throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
	        }
	        
	        try {
	            inputStream = httpURLConnection.getInputStream();
	            inputStreamReader = new InputStreamReader(inputStream);
	            reader = new BufferedReader(inputStreamReader);
	            
	            while ((tempLine = reader.readLine()) != null) {
	                resultBuffer.append(tempLine);
	            }
	            
	        } finally {
	            
	            if (reader != null) {
	                reader.close();
	            }
	            
	            if (inputStreamReader != null) {
	                inputStreamReader.close();
	            }
	            
	            if (inputStream != null) {
	                inputStream.close();
	            }
	            
	        }
	        
	        System.out.println(resultBuffer.toString()); 
	    }
	    
}
