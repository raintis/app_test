package com.my.thread;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrainLockTest {

	public static void main(String[] args) {
		ReentrantLock lock = new ReentrantLock();
		Runnable r1 = new Task(lock);
		Runnable r2 = new Task(lock);
		new Thread(r1).start();
		new Thread(r2).start();
		new Thread(r1).start();
		new Thread(r2).start();
	}

	private static class  Task implements Runnable{

		private ReentrantLock lock ;
		
		Task(ReentrantLock lock){
			this.lock = lock;
		}
		
		@Override
		public void run() {
			try{
				lock.lock();
				//lock.lock();
				Thread.sleep(1000);
				System.out.println(Thread.currentThread().getName());
			}catch(Exception e){
				System.out.println(e);
			}finally{
				lock.unlock();
				lock.unlock();
			}
		}
		
	}
}
