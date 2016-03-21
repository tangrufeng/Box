/* 
 * @Title:  DFVManager.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-11-12 下午4:57:54 
 * @version:  V1.0 
 */
package com.xhk.wifibox.box;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.eryiche.frame.util.LOG;
import com.eryiche.frame.util.PreferenceUtils;
import com.xhk.wifibox.R;
import com.xhk.wifibox.XHKApplication;
import com.xhk.wifibox.utils.Contants;
import com.xhk.wifibox.utils.JSONUtil;
import com.xhk.wifibox.utils.NetUtils;

/**
 * @author tang
 * 
 */
public class DFVManager {

	private final static String TAG = DFVManager.class.getSimpleName();

	private final static DFVManager instance = new DFVManager();

	private boolean isReady = false;
	private Boolean isDownloading = false;
	private File verFile = null;

	private OnNewVersionFileReadyListener listener;

	private DFVManager() {

	}

	public void setOnNewVersionFileReadyListener(
			OnNewVersionFileReadyListener listener) {
		this.listener = listener;
	}

	public static DFVManager getManager() {

		return instance;
	}

	public void checkVersion(Box box) {
		LOG.d(TAG, "==jiancebanban==" + box);

		String[] oldArrVer = getVersionInfo(box.deviceFiremwareVersion);
		LOG.d(TAG, "==the box customer name==" + oldArrVer[1]);
		String strVersion = NetUtils.getInstance().getJSONDataByGet(
				XHKApplication.getAppContext().getString(R.string.url_upgrade)
						+ "?serl=" + System.currentTimeMillis());
		try {
			if (strVersion != null) {
				JSONObject jsonVer = new JSONObject(strVersion);
				JSONArray arrVer = JSONUtil.getJSONArray(jsonVer, "CustItems");
				for (int i = 0; i < arrVer.length(); i++) {
					JSONObject jo = JSONUtil.getJSONObject(arrVer, i);
					LOG.d(TAG, "==the online version customer name==" + jo.toString());
					if (oldArrVer[1].equals(JSONUtil.getString(jo, "Id"))) {
						String version = JSONUtil.getString(jo, "Version");
						LOG.d(TAG, "==the version customer name==" + version);
						if (!version.equals(box.deviceFiremwareVersion)) {
							String url = JSONUtil.getString(jo, "Url");
							PreferenceUtils.putString(
									Contants.PREF_VERSION_NAME, version);
							PreferenceUtils.putString(
									Contants.PREF_VERSION_URL, url);
							box.haveNewVersion = true; // 有新版本

							new Thread(new DownloadThread()).start();
							return;	//找到一个就不找了
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}
		// String newVersion = PreferenceUtils
		// .getString(Contants.PREF_VERSION_NAME);
		// if (newVersion != null
		// && !newVersion.equals(box.deviceFiremwareVersion)) {
		// String[] newArrVer = getVersionInfo(newVersion);
		// String[] oldArrVer = getVersionInfo(box.deviceFiremwareVersion);
		// if (newArrVer != null && oldArrVer != null // 版本号信息正确
		// // && newArrVer[1].equals(oldArrVer[1]) // 相同客户
		// && !newArrVer[0].equals(oldArrVer[0])) { // 不同日期
		//
		// LOG.d(TAG, "==Have new device firemware version==" + newVersion);
		// box.haveNewVersion = true; // 有新版本
		//
		// new Thread(new DownloadThread()).start();
		// }
		// }
	}

	private String[] getVersionInfo(String version) {
		String[] arrVer = version.split("-");
		if (arrVer.length == 4) {
			String[] result = new String[] { arrVer[1], arrVer[2] };
			return result;
		}
		return null;
	}

	public boolean isReady() {
		return isReady;
	}

	public String getLocalFileURL() {
		if (verFile != null) {
			return getLocalHost() + "/version";
		}
		return "";
	}

	public String getLocalVersionFile() {
		if (verFile != null) {
			return verFile.getAbsolutePath();
		}
		return "";
	}

	private String getLocalHost() {
		return "http://" + PreferenceUtils.getString(Contants.PREF_LOCAL_IP)
				+ ":" + Contants.LOCAL_HTTP_PORT;
	}

	class DownloadThread implements Runnable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			synchronized (isDownloading) {
				if (isDownloading) {
					return;
				} else {
					isDownloading = true;
				}
			}
			String fileName = PreferenceUtils
					.getString(Contants.PREF_VERSION_NAME);
			String fileUrl = PreferenceUtils
					.getString(Contants.PREF_VERSION_URL);
			Log.d(TAG, "===FILEURL========>" + fileUrl);
			if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(fileUrl)) { // 没有版本号
				LOG.d(TAG, "==The new device firemware version is empty==>");
				return;
			}

			verFile = new File(Environment.getExternalStorageDirectory()
					+ "/XHKBOX/" + fileName);
			Log.d(TAG, verFile.getAbsolutePath());
			File parentDir = verFile.getParentFile();
			if (!parentDir.exists()) {
				parentDir.mkdirs();
			}
			InputStream in = null;
			OutputStream out = null;
			HttpEntity entity = null;

			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(fileUrl);
			// 断点续传的功能，设置当前已下载的字节到服务器
			long verFileLength = 0l;
			try {
				synchronized (verFile) {
					if (verFile.exists()) {
						verFile.delete();
					}
					verFile.createNewFile();

					HttpResponse response = client.execute(get);
					int status = response.getStatusLine().getStatusCode();
					LOG.d(TAG, "state:" + status);
					entity = response.getEntity();
					verFileLength = entity.getContentLength();
					in = entity.getContent();
					out = new FileOutputStream(verFile);
					byte[] bytes = new byte[4 * 1024];
					LOG.d(TAG,
							"Begin download new version file==>"
									+ verFile.length());
					int len;
					while ((len = in.read(bytes)) != -1) {
						out.write(bytes, 0, len);
					}

					LOG.d(TAG,
							"The file have download,size==>" + verFile.length());
					isReady = true;

				}

			} catch (Exception e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
			} finally {
				if (out != null) {
					try {
						out.flush();
					} catch (IOException e) {
					}
					try {
						out.close();
					} catch (IOException e) {
					}
				}

				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
				if (verFileLength != 0l && verFileLength == verFile.length()) {
					isReady = true;
					if (listener != null) {
						listener.onReady();
					}
				}
			}

		}

	}

	public interface OnNewVersionFileReadyListener {
		public void onReady();
	}

}
