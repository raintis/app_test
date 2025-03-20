package com.my.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class ThreadExecutorTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//ThreadExecutorTest test = new ThreadExecutorTest();
		//test.ThreadRun();
		
		for(int i=0;i<10;++i){
			System.out.println("I is "+i);
		}
	}

	@Test
	public  void ThreadRun(){
		//ExecutorService es = Executors.newCachedThreadPool();
		run();
		while(true){
			try {
				Thread.sleep(2000000);
				run();
				break;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testCoreSizeReset(){
		ThreadPoolExecutor es2 = new ThreadPoolExecutor(10,
				10,
				5L,
				TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(50),new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r,"name");
			}
		});
		es2.allowCoreThreadTimeOut(true);

		for(int i = 0;i<100;i++){
			if(i == 20){
				es2.setCorePoolSize(20);
				System.out.println("corePoolSize-->" + es2.getCorePoolSize());
			}
			es2.execute(() -> runSlowTask());
			System.out.println("getActiveCount -->" + es2.getActiveCount());
		}
	}

	private void runSlowTask(){
		try {
			Thread.sleep(2000000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void run(){
		ThreadPoolExecutor es2 = new ThreadPoolExecutor(100,
                120,
                5L,
               TimeUnit.SECONDS,
               new LinkedBlockingQueue<>(50),new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r,"name");
					}
				});
		es2.allowCoreThreadTimeOut(true);
		AtomicInteger ato = new AtomicInteger(0);
		for(int i=0;i<10;i++){
			try {
				es2.execute(() -> {
					System.out.println(String.format("execute threads %d", ato.get()+1));
					Test1(ato.incrementAndGet());
				});
				System.out.println("CorePoolSize" + es2.getCorePoolSize());
			} catch (Exception e) {
				//System.out.println(e.getMessage());
			}
		}
	}
	
	/*@Test
	public  void ThreadRun2(){
		ExecutorService es = Executors.newFixedThreadPool(4);
		AtomicInteger ato = new AtomicInteger(0);
		for(int i=0;i<100000;i++){
			es.submit(() ->{
				Test1(ato.incrementAndGet());
			});
		}
	}*/
	
	public static void Test1(int taskCode){
		String str = "fsafsdfasfsadfasdfasdfadfadfadfasdfasdfasdfadfadfadfasdfasdfasdfasdfadfasdfasdfadfadfadfadfadfadfasdfasdfasdfasdfasdfadfadfuoituowertuowerutowekeywr45454511221trtwrtgggEE";
		System.out.println("taskCode->" + taskCode);
		for(int i=0;i<2;i++){
			str.indexOf("EE") ;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testSerialProfile(){
		List<SerialObject> lists = new ArrayList<SerialObject>(10000000);
		for(int i=0;i<10000000;i++){
			lists.add(new SerialObject());
		}
	}

	static class SerialObject{
		String str = "fasdfadfasdfaffsd";
		int it = 10000;
		String[] strarr = new String[]{"fasdfasf","fjalsdfjaldjf;ad;f;adfasf","fjas;dlfja;sdjf;asjd;fasdf;asjd;fadsf"};
	}
}
