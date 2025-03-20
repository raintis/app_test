/*
 * @(#)SpanLogOrder.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.biz.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.google.common.io.Files;

/**   
 * @title: SpanLogOrder.java 
 * @package com.my.biz.log 
 * @description: TODO
 * @author: Administrator
 * @date: 2023年2月13日 上午10:51:40 
 * @version: V1.0   
*/
public class SpanLogOrder {

	/**
	 * @throws IOException 
	 *@title main 
	 *@description: TODO
	 *@author: Administrator
	 *@date: 2023年2月13日 上午10:51:40
	 *@param args
	 *@throws 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		TreeMap<Integer, String> orderSpan = new TreeMap<>((o1,o2) ->o2-o1);
		List<Line> lists = new ArrayList<>();
		String dir = "E:\\log\\招商\\202302\\";
		List<String> lines = Files.readLines(new File(dir+"日志查询0004HB-CNY智能合并.txt"), Charset.forName("UTF-8"));
		//List<String> lines = Arrays.asList("--bcm--857285ms ----CslServiceDispatcher.multiOrgCsl[119].executeService()[100]组织[0004HB-CNY]整个执行耗时");
		String prefix = "--bcm--";
		for(String line : lines){//
			if(line != null && line.startsWith(prefix)){
				StringBuilder digit = new StringBuilder();
				int startIndex = prefix.length();
				boolean isFlag = false;
				for(char c : line.substring(prefix.length()).toCharArray()){
					startIndex++;
					if(Character.isDigit(c) && !isFlag){
						digit.append(c);
					}else if(c == '-'){
						isFlag = true;
					}else if(isFlag && c != '-'){
						break;
					}
				}
				//orderSpan.put(Integer.valueOf(digit.toString()), line.substring(startIndex-1));
				lists.add(new Line(Integer.valueOf(digit.toString()),line.substring(startIndex-1)));
			}
		}
		lists.sort((c1,c2) ->c2.spantime - c1.spantime);
		BufferedWriter writer = Files.newWriter(new File(dir+"spanOrder.txt"), Charset.defaultCharset());
		lists.forEach(line ->{
			try {
				writer.write(line.spantime + "   " + line.info+" \r\n");
				//writer.newLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		writer.flush();
		writer.close();
	}

	static class Line{
		int spantime;
		String info;
		
		Line(int spantime,String info){
			this.spantime = spantime;
			this.info = info;
		}
	}
}
