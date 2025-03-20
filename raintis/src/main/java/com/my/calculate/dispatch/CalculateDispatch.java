/*
 * @(#)CalculateDispatch.java
 *
 * 金蝶国际软件集团有限公司版权所有 
 */
package com.my.calculate.dispatch;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.my.calculate.tree.CalcStatusEnum;
import com.my.calculate.tree.CalcTask;
import com.my.calculate.tree.TreeBuilder;

import sun.util.locale.provider.TimeZoneNameUtility;

/**   
 * @title: CalculateDispatch.java 
 * @package com.my.calculate.dispatch 
 * @description: TODO
 * @author: Administrator
 * @date: 2023年8月11日 下午3:24:56 
 * @version: V1.0   
*/
public class CalculateDispatch {

	private static ThreadPoolExecutor executor = genExecutor();
	private AtomicBoolean needRunTask = new AtomicBoolean(true);
	private ReentrantLock runLock = new ReentrantLock();
	private Condition wait = runLock.newCondition();
	//private Condition running = runLock.newCondition();
	
	public void execDispatch(){
		TreeMap<Integer,List<CalcTask>> taskTree = TreeBuilder.buildCalcTree();
		CalcTask root = taskTree.get(1).get(0);
		while(root.status == CalcStatusEnum.REDEAD){
			if(needRunTask.compareAndSet(true, false)){
				try{
					runLock.lock();
					System.out.println("--->lock");
					pushTask2ThreadPool(taskTree);
					try {
						long start = System.currentTimeMillis();
						System.out.println("entrant await-->");
						//wait.await(10, TimeUnit.SECONDS);
						wait.await();
						System.out.println("wait time -->" + (System.currentTimeMillis() - start) + "ms");
						//TimeUnit.MICROSECONDS.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
						//record error info into log table
					}
				}finally{
					System.out.println("release lock");
					runLock.unlock();
				}				
				//needRunTask.compareAndSet(true, false);
			}else{
			}
		}
	}
	
	void signal(String node){
		try{
			runLock.lock();
			System.out.println(String.format("node %s signal",node));
		}finally{
			runLock.unlock();
		}
	}
	
	void setNeedRunTask(){
		if(needRunTask.compareAndSet(false, true)){
			System.out.println("wait signal success");
			wait.signal();
		}else{
			System.out.println("wait signal fail");
		}
	}
	private void pushTask2ThreadPool(TreeMap<Integer,List<CalcTask>> taskTree){
		taskTree.forEach((lev,tasks) ->{
			tasks.forEach(t ->{
				if( t.status == CalcStatusEnum.REDEAD){
					if(t.node.isLeaf){
						executeTask(t);
					}else{
						if(t.node.subCount.get() == 0){
							executeTask(t);
						}
					}
					//running.signal();
					//wait.signal();
				}
			});
		});
	}
	
	private void executeTask(CalcTask t){
		executor.execute(() ->{
			if(t.status == CalcStatusEnum.REDEAD){// double check
				try {
					t.status = CalcStatusEnum.CALCULATING;
					TimeUnit.SECONDS.sleep(5);
					t.status = CalcStatusEnum.COMPLETE;
					System.out.println("--->" + t.node.number);
					//needRunTask.compareAndSet(false, true);
					if(t.node.parent != null){
						t.node.parent.decrement();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					t.status = CalcStatusEnum.FAIL;
				}finally{
					//signal(t.node.number);
					setNeedRunTask();
				}
		}});
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
	
	public static void main(String[] args) {
		CalculateDispatch dp = new CalculateDispatch();
		dp.execDispatch();
	}
}
