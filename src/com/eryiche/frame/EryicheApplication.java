package com.eryiche.frame;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

import com.eryiche.frame.config.FrameConfig;
import com.eryiche.frame.net.http.HttpEngine;

/**
 * 所有使用框架的应用必须创建自己的Application对象，并使用自己的Application对象继承自该
 * Application对象
 * @author Administrator
 *
 */
public class EryicheApplication extends Application {

	protected static EryicheApplication instance;

	/**
	 * Application提供单例方法获取当前Application对象的实例
	 * @return
	 */
	public static EryicheApplication getInstance() {
		return instance;
	}

	// Application手动聚合所有当前正在运行的Activity
	private List<Activity> mActivityStack = new ArrayList<Activity>();

	/**
	 * 当前应用程序的缓存目录
	 */
	private String homeDir;

	@Override
	public void onCreate() {
		instance = this;

		super.onCreate();

		initHomeDir();

		FrameConfig.initConfig(this);
	}

	private void initHomeDir() {
		homeDir = "/sdcard/." + getPackageName();
		File homeDirFile = new File(homeDir);
		if (!homeDirFile.exists()) {
			if(!homeDirFile.mkdirs()){
				homeDir=getApplicationContext().getFilesDir().getAbsolutePath();
			}
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		HttpEngine.release();
	}

	/**
	 * 应用程序缓存的主目录
	 * @return
	 */
	public String getHomePath() {
		return homeDir;
	}

	/**
	 * 把Activity运行添加到当前运行的ActivityStack
	 * 
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		mActivityStack.add(activity);
	}

	/**
	 * 从运行的ActivityStack
	 * 
	 * @param activity
	 */
	public void removeActivity(Activity activity) {
		mActivityStack.remove(activity);
	}

	/**
	 * 关闭所有的Activity
	 */
	public void finishAllActivity() {
		for (int i = 0; i < mActivityStack.size(); i++) {
			Activity activity = (Activity) mActivityStack.get(i);
			activity.finish();
		}
	}

}
