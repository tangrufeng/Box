/* 
 * @Title:  NetUtils.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-12 下午2:34:01 
 * @version:  V1.0 
 */
package com.xhk.wifibox.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

/**
 * @author tang
 * 
 */
public class NetUtils {
	private final String TAG = this.getClass().getSimpleName();
	private final static NetUtils instance = new NetUtils();
	private final static int timeOut = 30000;

	private NetUtils() {

	}

	public static NetUtils getInstance() {
		return instance;
	}

	/**
	 * Convert a IPv4 address from an integer to an InetAddress.
	 * 
	 * @param hostAddress
	 *            an int corresponding to the IPv4 address in network byte order
	 */
	public static InetAddress intToInetAddress(int hostAddress) {
		byte[] addressBytes = { (byte) (0xff & hostAddress),
				(byte) (0xff & (hostAddress >> 8)),
				(byte) (0xff & (hostAddress >> 16)),
				(byte) (0xff & (hostAddress >> 24)) };

		try {
			return InetAddress.getByAddress(addressBytes);
		} catch (UnknownHostException e) {
			throw new AssertionError();
		}
	}

	public String getJSONDataByGet(String url) {

		JSONObject json = null;
		if (TextUtils.isEmpty(url)) {
			return "";
		}
		HttpGet get = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();

		try {
			HttpResponse res = client.execute(get);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = res.getEntity();
				byte[] responseBytes = EntityUtils.toByteArray(entity);
				json = new JSONObject(new String(responseBytes));
			}

		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		} catch (JSONException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		} finally {
		}
		return String.valueOf(json);
	}

	public JSONObject getJSONDataByPost(String url, List<NameValuePair> params) {

		JSONObject json = null;
		if (TextUtils.isEmpty(url)) {
			return new JSONObject();
		}
		HttpPost post = new HttpPost(url);
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, timeOut);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				timeOut);

		try {
			if (params != null && !params.isEmpty()) {
				post.setEntity(new UrlEncodedFormEntity(params));
			}
			HttpResponse res = client.execute(post);

			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = res.getEntity();
				byte[] responseBytes = EntityUtils.toByteArray(entity);
				json = new JSONObject(new String(responseBytes, "UTF-8"));
			}

		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		} catch (JSONException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		} catch (Exception e) {
			Log.d(TAG, e.getLocalizedMessage(), e);
		} finally {
		}
		if (json == null) {
			return new JSONObject();
		} else {
			return json;
		}
	}
}
