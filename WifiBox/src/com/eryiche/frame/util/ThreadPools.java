package com.eryiche.frame.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 线程池工具类
 * @author EX-XIAOFANQING001
 *
 */
public class ThreadPools {
	
	private static ExecutorService mExecutorService ;

	/**
	 * 把任务交给线程池运行
	 * @param task
	 */
	public static Future<?> execute(Runnable task) {
		if (mExecutorService == null) {
			mExecutorService = Executors.newCachedThreadPool();
		}
		return mExecutorService.submit(task);
	}
	
	public static void clear() {
		if (mExecutorService != null) {
			mExecutorService.shutdownNow();
		}
	}
}
