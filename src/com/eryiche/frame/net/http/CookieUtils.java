package com.eryiche.frame.net.http;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;
import android.util.Log;

public class CookieUtils {

	private static final String TAG = CookieUtils.class.getSimpleName();

	/**
	 * Cookie缓冲对象,缓冲的结构为:
	 * { 
	 * 		urlHost1={cookieKey1=cookieValue1, cookieKey2=cookieValue2, cookieKey3=cookieValue3}, 
	 *   	urlHost2={cookieKey1=cookieValue1, cookieKey2=cookieValue2, cookieKey3=cookieValue3}
	 * }
	 * 即按URL的主机地址为Key来缓冲当前地址的所有Cookie信息
	 * 
	 */
	private static Map<String, HashMap<String, String>> mCookieStore = new HashMap<String, HashMap<String, String>>();

	/**
	 * 根据url得到Cookie
	 * @param url
	 * @return
	 */
    public static String getRequestCookies(String url) {
		StringBuffer cookiesBuffer = new StringBuffer();
		
		String hostName = getUrlHost(url);
		HashMap<String, String> cookie = mCookieStore.get(hostName);
		
		if (cookie != null && cookie.size() > 0) {
			for (String key : cookie.keySet()) {
				cookiesBuffer.append(key).append("=").append(cookie.get(key)).append(";");	
			}
		}
		
		Log.i(TAG, "upload cookie:" + cookiesBuffer.toString());
		
		return cookiesBuffer.toString();
	}

	/**
	 * 根据连接缓存Cookie信息在内存
	 * @param conn 当前的HTTP连接
	 */
	public static void storeResponseCookies(HttpURLConnection conn) {
		// 从返回头中获取Cookie信息
		String cookie = conn.getHeaderField("set-cookie");
		
		// 如果Cookie为空则直接返回
		Log.i(TAG, "has cookie:" + cookie);
		if (TextUtils.isEmpty(cookie)){
			return;
		}
		
		cookie = cookie.substring(0, cookie.indexOf(";"));
		
		// URL的主机地址
		String url = conn.getURL().toString();
		String hostUrl = getUrlHost(url);
		
		// 查看是否已经存在该Cookie值
		HashMap<String, String> cookieMap = mCookieStore.get(hostUrl) ;
		if (cookieMap == null) {
			cookieMap = new HashMap<String, String>();
		}
		
		// 分离Cookie
		String[] cookies = cookie.split(";");
		
		for (int i=0; i<cookies.length; i++) {
			String cookieItem = cookies[i];
			
			String[] cookieItems = cookieItem.split("=");
			
			if (cookieItems.length == 2) {
				String cookieItemKey = cookieItems[0];
				String cookieItemValue = cookieItems[1];
				if ("path".endsWith(cookieItemKey)) {
					continue;
				}
				cookieMap.put(cookieItemKey, cookieItemValue);
			}
		}
		
		mCookieStore.put(hostUrl, cookieMap);
	}

	/**
	 * 根据URL得到连接host值
	 * @param urlStr 
	 * @return 
	 */
	private static String getUrlHost(String urlStr) {
		try {
			URL url = new URL(urlStr);
			String hostName = url.getHost();
			return hostName;
		} catch (MalformedURLException e) {
			return "";
		}
	}

	/**
	 * 清楚所有的Cookie信息
	 */
	public static void clearAll() {
		mCookieStore.clear();
	}
}
