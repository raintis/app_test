package com.my.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorsTest {

	public static void main(String[] args) {
		ExecutorService srv =  new ThreadPoolExecutor(50, 100,
                5L, TimeUnit.SECONDS,
                /*new ArrayBlockingQueue<>(10)*/new LinkedBlockingQueue<Runnable>()/*new SynchronousQueue<>()*/,new ThreadFactory() {
					private AtomicInteger auto = new AtomicInteger(0);
					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r,"thread-" + auto.incrementAndGet());
					}
				});
		GenThreadFactor f = new GenThreadFactor();
		for(int i=0;i<100;i++){
			/*if(i %10 == 0){
				try {
					Thread.sleep(11);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
			//System.out.println(String.format("start execute + thread %d",i));
			srv.execute(f.genThread());
			//System.out.println(String.format("end execute + thread %d",i));
		}
	}

	static class GenThreadFactor{
		Runnable genThread(){
			return () ->{
				try {
					Thread.sleep(1000);
					System.out.println(String.format("run end execute + thread %s",Thread.currentThread().getName()));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
		}
	}
}
