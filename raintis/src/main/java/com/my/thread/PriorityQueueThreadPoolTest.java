/*
 * @(#)PriorityQueueThreadPoolTest.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.thread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.hamcrest.core.IsInstanceOf;

/**   
 * @title: PriorityQueueThreadPoolTest.java 
 * @package com.my.thread 
 * @description: TODO
 * @author: Administrator
 * @date: 2023年1月6日 上午9:50:16 
 * @version: V1.0   
*/
public class PriorityQueueThreadPoolTest {

	public static void main(String[] args) {
		System.out.println(30_000);
		/*BlockingQueue<Runnable> workQueue = new PriorityBlockingQueue<Runnable>(10,(c1,c2)->((RunnableWithPriority)c1).getPriority()-((RunnableWithPriority)c2).getPriority());
		ThreadPoolExecutor srv =  new ThreadPoolExecutor(5, 10,
                5L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10)workQueuenew SynchronousQueue<>(),new ThreadFactory() {
					private AtomicInteger auto = new AtomicInteger(0);
					@Override
					public Thread newThread(Runnable r) {
						
						Thread thread =  new Thread(r,"thread-" + auto.incrementAndGet());
						if(r.getClass().isAssignableFrom(RunnableWithPriority.class)){
							int prePriority = thread.getPriority();
							thread.setPriority(RunnableWithPriority.class.cast(r).getPriority());
							System.out.println(String.format("设置线程优先级由%d变为%d",prePriority,thread.getPriority()));
						}
						return thread;
					}
				});*/
		final BlockingQueue<Runnable> workQueue = new PriorityBlockingQueue<Runnable>(10,(c1,c2)->((RunnableWithPriority)c1).getPriority()-((RunnableWithPriority)c2).getPriority());
		KillableThreadPoolExecutor srv = new KillableThreadPoolExecutor(10, 10,30L, TimeUnit.SECONDS,workQueue,"killThread-");
		GenThreadFactor f = new GenThreadFactor();
		for(int i=0;i<1000;i++){
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
		srv.allowsCoreThreadTimeOut();
		System.out.println();
		while(true){
			if(srv.getQueue().isEmpty()){
				break;
			}
			try {
				//TimeUnit.SECONDS.sleep(1);
				srv.tryInterruptThread("5");
			} catch (/*Interrupted*/Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println(String.format("ActiveCount:%d,QueueSize:%d", srv.getActiveCount(),srv.getQueue().size()));
		}
		srv.shutdown();
		System.out.println(30_000);
	}

	static class GenThreadFactor{
		final static Random priority = new Random();
		RunnableWithPriority genThread(){
			return new RunnableWithPriority(){
				private int priorityVal = priority.nextInt(10);
				@Override
				public void run() {
					try {
						//Thread.sleep(1000);
						int count = 0;
						long starttime = System.currentTimeMillis();
						Map<String,Integer> map = new HashMap<>();
						while(true){
							count++;
							//map.put(""+count, count);
							/*if(count == 100000){
								break;
							}*/
							if(Thread.interrupted()){
								throw new InterruptedException();
							}
						}
						//System.out.println("span time ->" + (System.currentTimeMillis() - starttime));
						//Thread.sleep(1000);
						//System.out.println(String.format("run end execute + thread %s,priority->"+priorityVal,Thread.currentThread().getName()));
					} catch (InterruptedException e) {
						e.printStackTrace();
						System.out.println("interrupted Thread name-->"+Thread.currentThread().getName());
					}
				}
				@Override
				public int getPriority() {
					// TODO Auto-generated method stub
					return priorityVal;
				}
			};
		}
	}
	
	interface RunnableWithPriority extends Runnable{
		 int getPriority();
	}

	static class KillableThreadPoolExecutor extends ThreadPoolExecutor {

	    private final Map<Runnable, Thread> executingThreads;
	    
	    public KillableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,BlockingQueue<Runnable> workQueue, String threadNamePrefix) {
	        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new ThreadFactory() {
				private AtomicInteger auto = new AtomicInteger(0);
				@Override
				public Thread newThread(Runnable r) {
					
					Thread thread =  new Thread(r,threadNamePrefix + auto.incrementAndGet());
					if(r.getClass().isAssignableFrom(RunnableWithPriority.class)){
						int prePriority = thread.getPriority();
						thread.setPriority(RunnableWithPriority.class.cast(r).getPriority());
						System.out.println(String.format("设置线程优先级由%d变为%d",prePriority,thread.getPriority()));
					}
					return thread;
				}
			});
	        executingThreads = new HashMap<>(maximumPoolSize);
	    }

	    @Override
	    protected synchronized void beforeExecute(Thread t, Runnable r) {
	        super.beforeExecute(t, r);
	        executingThreads.put(r, t);
	    }

	    @Override
	    protected synchronized void afterExecute(Runnable r, Throwable t) {
	        super.afterExecute(r, t);
	        if(executingThreads.containsKey(r)) {
	            executingThreads.remove(r);
	        }
	        if(t != null){
	        	t.printStackTrace();
	        }
	    }

	    @Override
	    public synchronized List<Runnable> shutdownNow() {
	        List<Runnable> runnables = super.shutdownNow();
	        for(Thread t : executingThreads.values()) {
	            t.interrupt();
	        }
	        return runnables;
	    }
	    
	    public void tryInterruptThread(String threadName){
	    	Map<Runnable, Thread> copy = new HashMap<>(executingThreads);
	    	int count = 10;
	    	for(Thread t : copy.values()){
	    		if(t.isAlive()){
					t.interrupt();
					System.out.println("try interrupt thread "+t.getName());
					count--;
				}
	    		if(count == 0){
	    			break;
	    		}
	    	}
	    	/*copy.values().forEach(t ->{
	    		if(t.getName().contains(threadName)){
	    			try{
	    				if(t.isAlive()){
	    					t.interrupt();
	    					System.out.println("try interrupt thread "+t.getName());
	    				}
	    			}catch(Throwable e){
	    				e.printStackTrace();
	    			}
	    		}
	    	});*/
	    }
	}
}
