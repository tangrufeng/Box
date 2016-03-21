package com.eryiche.frame.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import android.util.Log;

/**
 * 自定义的日志类
 * 
 * 对原来的Log方法做了封装，提供了是否打印日志的标志
 * 
 * 提供了是否保存日志到日志文件的标志
 * 
 * @author Administrator
 *
 */
public class LOG {

	/**
	 * 标识是否开启Log打印
	 */
	public static boolean isDebug = true;
	
	
	/**
	 * 标识是否把Log保存在Log文件当中
	 */
	public static boolean isSaveLog = false;
	
	/**
	 * Log的保存路径
	 */
	public static String logPath;

	public static int v(String tag, String msg) {
		
		if (isSaveLog) {
			saveLogToFile(tag, msg, "V", null);
		}
		
		if (isDebug) {
			return Log.v(tag, msg);
		}
		return 0;
	}

	public static int v(String tag, String msg, Throwable tr) {
		
		if (isSaveLog) {
			saveLogToFile(tag, msg, "V", tr);
		}
		
		if (isDebug) {
			return Log.v(tag, msg, tr);
		}
		return 0;
	}

	public static int d(String tag, String msg) {
		
		if (isSaveLog) {
			saveLogToFile(tag, msg, "D", null);
		}
		
		if (isDebug) {
			return Log.d(tag, msg);
		}
		return 0;
	}

	public static int d(String tag, String msg, Throwable tr) {
		
		if (isSaveLog) {
			saveLogToFile(tag, msg, "D", tr);
		}
		
		if (isDebug) {
			return Log.d(tag, msg, tr);
		}
		return 0;
	}

	public static int i(String tag, String msg) {
		
		if (isSaveLog) {
			saveLogToFile(tag, msg, "I", null);
		}
		
		if (isDebug) {
			return Log.i(tag, msg);
		}
		return 0;
	}

	public static int i(String tag, String msg, Throwable tr) {
		
		if (isSaveLog) {
			saveLogToFile(tag, msg, "I", tr);
		}
		
		if (isDebug) {
			return Log.i(tag, msg, tr);
		}
		return 0;
	}

	public static int w(String tag, String msg) {
		
		if (isSaveLog) {
			saveLogToFile(tag, msg, "W", null);
		}
		
		if (isDebug) {
			return Log.w(tag, msg);
		}
		return 0;
	}

	public static int w(String tag, String msg, Throwable tr) {
		
		if (isSaveLog) {
			saveLogToFile(tag, msg, "W", tr);
		}
		
		if (isDebug) {
			return Log.w(tag, msg, tr);
		}
		return 0;
	}


	public static int e(String tag, String msg) {
		
		if (isSaveLog) {
			saveLogToFile(tag, msg, "E", null);
		}
		
		if (isDebug) {
			return Log.e(tag, msg);
		}
		return 0;
	}

	public static int e(String tag, String msg, Throwable tr) {
		
		if (isSaveLog) {
			saveLogToFile(tag, msg, "E", tr);
		}
		
		if (isDebug) {
			return Log.e(tag, msg, tr);
		}
		return 0;
	}
	
	
	/**
	 * 日期对象
	 */
	private static Calendar calendar = Calendar.getInstance();

	// 保存日志到日志文件
	private static void saveLogToFile(final String tag, final String msg, final String priority, final Throwable thr) {
		// 开
		ThreadPools.execute(new Runnable() {
			@Override
			public void run() {
				File logFile = new File(logPath);
				
				FileOutputStream fileOutputStream = null;
				PrintWriter printWriter = null;
				
				try {
					fileOutputStream = new FileOutputStream(logFile, true);
					printWriter = new PrintWriter(fileOutputStream);
					
					
					if (thr != null) {
						printWriter.println(calendar.getTime().toLocaleString() + "	" + priority + "	" + ">>" + tag + "<<	" + msg +  '\r' + thr.toString());
					} else {
						printWriter.println(calendar.getTime().toLocaleString() + "	" + priority + "	" + ">>" + tag + "<<	" + msg + '\r');
					}
					
					fileOutputStream.flush();
				} catch (Exception e) {
					Log.i("save", "", e);
					// ignore
				}  finally {
					try {
						if (printWriter != null) {
							printWriter.close();
						}
						
						if (fileOutputStream != null) {
							fileOutputStream.close();
						}
					} catch (IOException ie) {
						// ignore
					}
				}
			}
		});
		
	}

}
