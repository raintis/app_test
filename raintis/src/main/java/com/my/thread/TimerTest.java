package com.my.thread;

import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 模拟监听某个任务是否还在执行，定时发送心跳信息
 * @author Administrator
 *
 */
public class TimerTest {

	public static void main(String[] args) throws InterruptedException {
		Timer timer = new Timer();
		AtomicBoolean bool = new AtomicBoolean(false);
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				// send keep live msg
				System.out.println(DateFormat.getTimeInstance().format(new Date()));
				if(bool.get()){// task is completed
					System.out.println("observer is over");
					timer.cancel();// cancel timer send msg
				}
			}			
		}, 0L, 2000L);
		Thread.sleep(10000L);// execute task,span time;
		bool.getAndSet(true);
	}

}
