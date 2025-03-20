/*
 * @(#)OlapCommandRegex.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.regex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

/**   
 * @title: OlapCommandRegex.java 
 * @package com.my.regex 
 * @description: TODO
 * @author: Administrator
 * @date: 2022年11月9日 下午3:02:25 
 * @version: V1.0   
*/
public class OlapCommandRegex {

	private static String[] matchKeys = {"A315A","MRpt","FY2021","M_M02","CS"
			
	};
	
	
	private static void compareFile(){
		File back = new File("E:\\log\\华侨城\\adjustdata\\打回.txt");
		File audit = new File("E:\\log\\华侨城\\adjustdata\\审核1.txt");
		Set<String> backSet = new HashSet<>(1000);
		Set<String> auditSet = new HashSet<>(1000);
		collectInfo(backSet,back);
		collectInfo(auditSet,audit);
		Set<String> b1 = new HashSet<>(auditSet);
		b1.removeAll(backSet);
		Set<String> b2 = new HashSet<>(backSet);
		b2.removeAll(auditSet);
		
		
		
		System.out.println(b1);
		System.out.println(b1.size());
		System.out.println(b2);
		System.out.println(b2.size());
	}
	
	private static void collectInfo(Set<String> set,File f){
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))){
			String line = null;
			while((line = br.readLine()) != null){
				int lastIndex = line.lastIndexOf(',');
				set.add(line.substring(0, lastIndex).trim());
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @throws Exception 
	 *@title main 
	 *@description: TODO
	 *@author: Administrator
	 *@date: 2022年11月9日 下午3:02:25
	 *@param args
	 *@throws 
	 */
	private static final TreeSet<HintCommandInfo> commandhints = new TreeSet<>((o1,o2) -> o2.command - o1.command);
	public static void main(String[] args) throws Exception {
		/*if(true){
			compareFile();
			return;
		}*/
		// TODO Auto-generated method stub
		//String inputFilePath = "F:\\log\\百洋\\command\\log-by\\download-20221108154656\\commands_31601000_31600999.log";
		long starttime = System.currentTimeMillis();
		String dir = "E:\\log\\顺丰\\20230504\\commands.tar.gz\\commands";
		
		File savefile = new File(dir+"\\result\\result2.txt");
		if(!savefile.exists()){
			if(!savefile.getParentFile().exists()){
				savefile.getParentFile().mkdir();
			}
			savefile.createNewFile();
		}
		FileOutputStream ost = new FileOutputStream(savefile);
		
		List<File> lists = new ArrayList<>();
		findAllLogFiles(new File(dir),lists);
		System.out.println("找到" + lists.size() + " 个文件.");
		ExecutorService srv = Executors.newFixedThreadPool(10);
		CountDownLatch latch = new CountDownLatch(lists.size());
		//lists.parallelStream().forEach(log ->{
			for(File log : lists){
				srv.execute(() ->{
					if(log.getName().endsWith(".logz")){
						try {
							SevenZFile sevenZFile = new SevenZFile(log);
							SevenZArchiveEntry entry = null;
							while((entry = sevenZFile.getNextEntry()) != null){
								InputStream is = sevenZFile.getInputStream(entry);
								BufferedReader br = null;
								try{
									br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
									String line;
									StringBuilder block = new StringBuilder();
									while((line = br.readLine()) != null){
										if(line.contains("#{")){
											outputMatchLine(log,block.toString());
											block.setLength(0);
										}
										block.append(line);
									}
									outputMatchLine(log,block.toString());
								}catch(Exception i){
									System.out.println(i);
								}finally{
									is.close();
									br.close();
								}
							}
							sevenZFile.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}finally{
							latch.countDown();
							System.out.println(String.format("complemt task %d/%d", latch.getCount(),lists.size()));
						}
					}else{
						try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(log), "UTF-8"));){
							String line;
							StringBuilder block = new StringBuilder();
							while((line = br.readLine()) != null){
								if(line.contains("#{")){
									outputMatchLine(log,block.toString());
									block.setLength(0);
								}
								block.append(line);
							}
							outputMatchLine(log,block.toString());
						}catch(Exception i){
							System.out.println(i);
						}finally{
							latch.countDown();
							System.out.println(String.format("complemt task %d/%d", latch.getCount(),lists.size()));
						}
				}
					});
			}
		//});
		latch.await();
		srv.shutdown();
		
		try{
			for(HintCommandInfo info : commandhints){
				ost.write(info.line.getBytes());
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			ost.close();
		}
		System.out.println("match " + commandhints.size() + " line commands");
		System.out.println("cost time :" + (System.currentTimeMillis()-starttime) + "ms");
	}
	
	private static void findAllLogFiles(File file ,List<File> lists){
		if(file != null && file.isDirectory()){
			File[] directfiles = file.listFiles();
			for(File f : directfiles){
				if(f.isFile() && (f.getName().endsWith(".log")||f.getName().endsWith(".logz"))){
					lists.add(f);
				}else{
					findAllLogFiles(f, lists);
				}
			}
		}else{
			if(file.getName().endsWith(".log")){
				lists.add(file);
			}
		}
	}
	
	private static int outputMatchLine(File log,String line){
		for(String key : matchKeys){
			if(!line.contains(key)){
				return 0;
			}
		}
		int commandNum = Integer.valueOf(line.substring(0, line.indexOf("#")));
		commandhints.add(new HintCommandInfo(commandNum,log.getAbsolutePath() + "-->" + line + "\n"));
		//System.out.println(log.getAbsolutePath() + "-->" + line);
		return 1;
	}

	private static class HintCommandInfo{
		HintCommandInfo(int command,String line){
			this.command = command;
			this.line = line;
		}
		int command;
		String line;
	}
}
