package com.my.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class DelayQueueTest {

	private static Queue<String> queue = new ConcurrentLinkedQueue<>();
	private static ReentrantLock lock=new ReentrantLock(); 
	
	public static void main(String[] args) {

	}

	static class Consumer extends Thread{
		public void run() {
			lock.lock();
			List<String> list = new ArrayList<>();
			try {
				while (true) {
					if (list.size() == 10 || queue.peek() == null) {
						break;
					}
					String item = queue.poll();
					list.add(item);
				}
				if (!list.isEmpty()) {
					System.out.println();
				}
			} catch (Exception e) {

			} finally {
				lock.unlock();
			}
		}
	}
	
	static class Producer extends Thread {
		public void run() {
			lock.lock();
			int i = 0;
			try {
				while (true) {
					queue.offer(String.valueOf(i++));
					if (queue.size() >= 10) {
						Thread.sleep(2000);
					}
				}
			} catch (Exception e) {

			} finally {
				lock.unlock();
			}
		}
	}
}
