/*
 * @(#)SaleStore.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.calculate.dispatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**   
 * @title: SaleStore.java 
 * @package com.my.calculate.dispatch 
 * @description: TODO
 * @author: Administrator
 * @date: 2023年8月18日 上午8:43:33 
 * @version: V1.0   
*/
public class SaleStore {

	private static ThreadPoolExecutor executor = genExecutor();
	private static List<Integer> wareHouse = new ArrayList<>();
	private static ReentrantLock lock = new ReentrantLock();
	private static Condition empty = lock.newCondition();
	private static Condition full = lock.newCondition();
	
	public static void main(String[] args) {
		executor.execute(new Productor("Robot1"));
		for(int i = 0;i< 5;i++){
			executor.execute(new Consumer("consumer" + i));
		}
	}
	
	static class Productor implements Runnable{
		String name;
		Productor(String name){
			this.name = name;
		}
		
		/**   
		 * @title: run
		 * @description: TODO   
		 * @see java.lang.Runnable#run()     
		 */ 
		@Override
		public void run() {
			while(true){
				try{
					lock.lock();
					if(wareHouse.size() >= 5){
						try {
							full.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					wareHouse.add(1);
					System.out.println(name+" 生产了1个产品" + "总共有" + wareHouse.size() + "个产品");
					empty.signal();
				}finally{
					lock.unlock();
				}
			}
		}
	}
	
	static class Consumer implements Runnable{
		String name;
		Consumer(String name){
			this.name = name;
		}
		/**   
		 * @title: run
		 * @description: TODO   
		 * @see java.lang.Runnable#run()     
		 */ 
		@Override
		public void run() {
			while(true){
				lock.lock();
				try{
					if(wareHouse.isEmpty()){
						try {
							empty.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					wareHouse.remove(0);
					System.out.println(name + " 消费了1个产品，还剩余" + wareHouse.size() + "个产品。");
					full.signal();
					empty.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					lock.unlock();
				}
			}
		}
	}
	
	private static ThreadPoolExecutor genExecutor(){
		ThreadPoolExecutor executor = new ThreadPoolExecutor(100,
	            120,
	            1L,
	           TimeUnit.SECONDS,
	           new LinkedBlockingQueue<>(50),new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r,"name");
					}
				});
		executor.allowCoreThreadTimeOut(true); 
		return executor;
	}
}
