package com.my.log;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

public class BizCode {
	Stopwatch watch = Stopwatch.createUnstarted();
	public void testTime(){
		watch.reset();
		for(int i=0;i<1000000;i++){
			//System.out.println(i);
		}
		LogIt.log("消耗时间：" + watch.elapsed(TimeUnit.MILLISECONDS) + "ms");
	}
	
	public void start(){
		watch.start();
	}
	
	public void testTime2(){
		watch.reset();
		for(int i=0;i<1000000;i++){
			//System.out.println(i);
		}
		LogIt.log("消耗时间：" + watch.toString() + "ms");
	}
	
	public void testTime3(){
		watch.reset();
		watch.start();
		for(int i=0;i<1000000;i++){
			
		}
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LogIt.log("消耗时间：" + watch.toString()/*elapsed(TimeUnit.MILLISECONDS)*/ + "");
	}
	
	public static void main(String[] args) {
		BizCode biz = new BizCode();
		biz.start();
		biz.testTime();
		biz.testTime2();
		biz.testTime3();
	}
}
