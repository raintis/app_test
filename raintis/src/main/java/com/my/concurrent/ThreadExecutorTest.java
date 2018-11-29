package com.my.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class ThreadExecutorTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Test
	public  void ThreadRun(){
		//ExecutorService es = Executors.newCachedThreadPool();
		ExecutorService es2 = new ThreadPoolExecutor(100,
                 120,
                 60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(50),new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r,"name");
					}
				});
		AtomicInteger ato = new AtomicInteger(0);
		for(int i=0;i<100000;i++){
			try {
				es2.execute(() -> {
					System.out.println(String.format("execute threads %d", ato.get()+1));
					Test1(ato.incrementAndGet());
				});
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
		for(int i=0;i<1000000000;i++){
			str.indexOf("EE") ;
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
