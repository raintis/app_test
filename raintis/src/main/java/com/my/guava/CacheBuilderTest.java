package com.my.guava;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class CacheBuilderTest {

	private volatile int val = -1;
	
	public static void main(String[] args) {
		final LoadingCache<String,String> cache = CacheBuilder.newBuilder().expireAfterAccess(300, TimeUnit.SECONDS).softValues().build(new CacheLoader<String, String>(){
			@Override
			public String load(String key) {
				try {
					System.out.println("sleep 4 secends start!");
					TimeUnit.SECONDS.sleep(1);
					System.out.println("sleep 4 secends end!");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return key+"kkkk";
			}
		});
		Thread[] threads = new Thread[100];
		AtomicInteger increace = new AtomicInteger(0);
		for(int i=0;i<threads.length;i++){
			threads[i] = new Thread(() ->{
				try {
					System.out.println(cache.get(""+increace.getAndIncrement()));
					System.out.println(cache.asMap());
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}
		for(int i=0;i<10;i++){
			threads[i].start();
		}
		System.out.println(cache.asMap());
		System.gc();
		try {
			TimeUnit.SECONDS.sleep(4);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("sleep 10 sec after cache ->" + cache.asMap());
		
		CacheBuilderTest test = new CacheBuilderTest();
		test.testVolatileVal();
	}
	
	private void testVolatileVal(){
		Thread[] threads = new Thread[10];
		for(int i=0;i<10;i++){
			threads[i] = new Thread(() ->{
				try {
					TimeUnit.MILLISECONDS.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(val == -1){
					val = 0;
					System.out.println("init val");
				}
				System.out.println(String.format("val is %d", val));
			});
		}
		for(int i=0;i<10;i++){
			threads[i].start();
		}
	}
}
