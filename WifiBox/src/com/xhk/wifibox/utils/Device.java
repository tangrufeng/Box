package com.xhk.wifibox.utils;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.eryiche.frame.util.LOG;
import com.eryiche.frame.util.security.MessageDigestUtils;

/**
 * 访问本地设备信息的相关方法集合
 * 
 * @author EX-XIAOFANQING001
 * 
 */
public class Device {

	private static final String LOG_TAG = Device.class.getSimpleName();

	/**
	 * 获取应用程序名称
	 */
	public static String getAppName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取当前的版本号
	 * 
	 * @param context
	 * @return 当前应用的版本名称
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);

			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			LOG.d(LOG_TAG, e.toString());
		}
		return null;
	}

	/**
	 * 获取设备制造商
	 * 
	 * @return 制造商
	 */
	public static String getManufacturer() {
		String manufacturer = Build.MANUFACTURER;
		return (manufacturer == null) || (manufacturer.equals("")) ? "unknown"
				: manufacturer;
	}

	/**
	 * 获取设备品牌机型信息
	 * 
	 * @return
	 */
	public static String getModel() {
		try {
			String str = Build.MODEL;
			return (str == null) || (str.equals("")) ? "unknown" : str;
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	/**
	 * 获取设备屏幕分辨率大小
	 * 
	 * @param paramObject
	 */
	public static String getScreenResolution(Context context) {
		try {
			WindowManager windowManager = (WindowManager) context
					.getSystemService("window");
			Display display = windowManager.getDefaultDisplay();
			int i = display.getWidth();
			int j = display.getHeight();
			String str = i + "x" + j;
			return (str == null) || (str.equals("")) || (str.equals("x"))
					|| (str.equals("0x0")) ? "unknown" : str;
		} catch (Exception e) {
			LOG.e(LOG_TAG, e.toString());
		}
		return null;
	}

	public static String getOSVersion() {
		try {
			String str = Build.VERSION.RELEASE;
			return (str == null) || (str.equals("")) ? "unknown" : str;
		} catch (Exception e) {
			LOG.e(LOG_TAG, e.toString());
		}
		return null;
	}

	static final String DEVICE_ID = "device_id";

	/**
	 * DEVICE ID的产生规则
	 * 
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		// 看看本地是否已经产生过DEVICE_ID,如果已经产生过，则直接返回
		String deviceId = getSharedPreferences(context).getString(DEVICE_ID,
				null);
		if (!TextUtils.isEmpty(deviceId)) {
			return deviceId;
		}

		// 产生一个DeviceId
		String imei = getIMEI(context);
		if (imei != null && !"".equals(imei)) {
			deviceId = "AIMEI_" + MessageDigestUtils.SHA1(imei);
		} else {
			String mac = getLocalMacAddress(context);
			if (mac != null && !"".equals(mac)) {
				deviceId = "AMAC_" + MessageDigestUtils.SHA1(mac);
			} else {
				String serial = null;
				if (Build.VERSION.SDK_INT >= 9) {
					serial = Build.SERIAL;
					if (serial != null && !"".equals(serial)) {
						deviceId = "ASERIAL_" + MessageDigestUtils.SHA1(serial);
					}
				} else {
					String uuid = getUUID();
					if (uuid != null && !"".equals(uuid)) {
						deviceId = "AUUID_" + MessageDigestUtils.SHA1(uuid);
					}
				}
			}
		}

		if (!TextUtils.isEmpty(deviceId)) {
			getSharedPreferences(context).edit().putString(DEVICE_ID, deviceId)
					.commit();
		}

		return deviceId;

	}

	private static SharedPreferences getSharedPreferences(Context ctx) {
		SharedPreferences sharedPreferences = ctx.getSharedPreferences(
				"preference", 0);
		return sharedPreferences;
	}

	public static String getIMEI(Context context) {
		String imei = getSharedPreferences(context).getString("imei", null);
		if (!TextUtils.isEmpty(imei)) {
			LOG.i(LOG_TAG, "imei get cache:" + imei);
			return imei;
		}

		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			imei = tm.getDeviceId();
			getSharedPreferences(context).edit().putString("imei", imei)
					.commit();
			LOG.i(LOG_TAG, "imei generate:" + imei);
			return imei;
		} catch (Exception e) {
			LOG.e(LOG_TAG, e.toString());
		}
		return null;
	}

	/**
	 * 获取SIM卡号
	 * 
	 * @param context
	 * @return
	 */
	public static String getImsi(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imsi = tm.getSubscriberId();
			LOG.i(LOG_TAG, "imei generate:" + imsi);
			return imsi;
		} catch (Exception e) {
			LOG.e(LOG_TAG, e.toString());
		}
		return null;
	}

	private static String getUUID() {
		String uuid = UUID.randomUUID().toString();
		// 去掉“-”符号
		return uuid.substring(0, 8) + uuid.substring(9, 13)
				+ uuid.substring(14, 18) + uuid.substring(19, 23)
				+ uuid.substring(24);

	}

	/**
	 * 获取wifi mac地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getLocalMacAddress(Context context) {
		try {
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			String mac = info.getMacAddress();
			return mac;
		} catch (Exception e) {
			LOG.e(LOG_TAG, e.toString());
		}
		return null;
	}

	public static int getCurrAppVersionCode(Context context) {
		int currCode = 0;
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			currCode = packageInfo.versionCode;

		} catch (NameNotFoundException e) {
			LOG.d(LOG_TAG, e.toString());
		}
		return currCode;
	}

	/**
	 * 获取设备当前的网络是否可用
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isNetWorkConnected(Context ctx) {
		ConnectivityManager connect = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connect.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isAvailable()
				&& networkInfo.isConnected()
				&& networkInfo.getState() == State.CONNECTED) {
			return true;
		}
		return false;
	}

}
