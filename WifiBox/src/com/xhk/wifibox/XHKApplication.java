package com.xhk.wifibox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.eryiche.frame.util.PreferenceUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.xhk.wifibox.activity.ReadyActivity;
import com.xhk.wifibox.box.BoxControler;
import com.xhk.wifibox.model.BitmapCache;
import com.xhk.wifibox.model.MediaDatabase;
import com.xhk.wifibox.utils.AudioUtil;
import com.xhk.wifibox.utils.Contants;
import com.xhk.wifibox.utils.HttpServer;

public class XHKApplication extends Application {
	public final static String TAG = "XHKApplication";
	private static XHKApplication instance;

	public final static String SLEEP_INTENT = "org.videolan.vlc.SleepIntent";

	private List<Activity> mActivityStack = new ArrayList<Activity>();

	@Override
	public void onCreate() {
		super.onCreate();

		instance = this;
		// Are we using advanced debugging - locale?
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String p = pref.getString("set_locale", "");
		if (p != null && !p.equals("")) {
			Locale locale;
			// workaround due to region code
			if (p.equals("zh-TW")) {
				locale = Locale.TRADITIONAL_CHINESE;
			} else if (p.startsWith("zh")) {
				locale = Locale.CHINA;
			} else if (p.equals("pt-BR")) {
				locale = new Locale("pt", "BR");
			} else if (p.equals("bn-IN") || p.startsWith("bn")) {
				locale = new Locale("bn", "IN");
			} else {
				/**
				 * Avoid a crash of java.lang.AssertionError: couldn't
				 * initialize LocaleData for locale if the user enters
				 * nonsensical region codes.
				 */
				if (p.contains("-"))
					p = p.substring(0, p.indexOf('-'));
				locale = new Locale(p);
			}
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config,
					getBaseContext().getResources().getDisplayMetrics());
		}

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder() //
				.showImageForEmptyUri(null) //
				.showImageOnFail(null) //
				.cacheInMemory(true) //
				.cacheOnDisk(true) //
				.build();//
		ImageLoaderConfiguration config = new ImageLoaderConfiguration//
		.Builder(getApplicationContext())//
				.defaultDisplayImageOptions(defaultOptions)//
				//
				.build();//
		ImageLoader.getInstance().init(config);

		// Initialize the database soon enough to avoid any race condition and
		// crash
		MediaDatabase.getInstance(this);
		// Prepare cache folder constants
		AudioUtil.prepareCacheFolder(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(BoxControler.ACTION_BOX_NO_ADDRESS);
		registerReceiver(new MyBroadcastReceiver(), filter);
		Log.d(TAG, "===================REGEDIT");
		HttpServer.start(Contants.LOCAL_HTTP_PORT);

		storeLocalIP();
	}

	/**
	 * 
	 */
	private void storeLocalIP() {
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String localIP = ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff)
				+ "." + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
		PreferenceUtils.putString(Contants.PREF_LOCAL_IP, localIP);
	}

	public static XHKApplication getInstance() {
		return instance;
	}

	class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, intent.getAction());
			if (BoxControler.ACTION_BOX_NO_ADDRESS.equals(intent.getAction())) {
				Intent i = new Intent(XHKApplication.this, ReadyActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
		}

	}

	/**
	 * Called when the overall system is running low on memory
	 */
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.w(TAG, "System is running low on memory");

		BitmapCache.getInstance().clear();
	}

	/**
	 * @return the main context of the Application
	 */
	public static Context getAppContext() {
		return instance;
	}

	/**
	 * @return the main resources from the Application
	 */
	public static Resources getAppResources() {
		if (instance == null)
			return null;
		return instance.getResources();
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

	public void exit() {
		finishAllActivity();
		System.exit(0);
	}

	public void restartApplication() {
		final Intent intent = getPackageManager().getLaunchIntentForPackage(
				getPackageName());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
