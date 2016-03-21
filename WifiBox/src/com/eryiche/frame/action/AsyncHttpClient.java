/*
 * 文件名: RequestAction.java
 * 版    权：  Copyright PingAn Technology All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: EX-XIAOFANQING001
 * 创建时间: 2012-6-1
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.eryiche.frame.action;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import com.eryiche.frame.net.http.HttpEngine;
import com.eryiche.frame.net.http.Request;
import com.eryiche.frame.net.http.RequestParams;
import com.eryiche.frame.util.LOG;

/**
 * 
 * 创建该类的对象可以向服务器发送HTTP请求
 * 
 * 所有的HTTP请求将会已单线程排队的序列
 * 
 * @author EX-XIAOFANQING001
 * @date 2012-6-1
 * @version [Android PABank C01, @2012-6-1]
 * @description
 */
public class AsyncHttpClient {

	static final String TAG = AsyncHttpClient.class.getSimpleName();

	public static int DEFAULT_RESPONSE_TYPE = Request.RES_TYPE_TREE_MAP;

	/**
	 * Http执行引擎对象
	 */
	private HttpEngine mHttpEngin;

	/**
	 * Http请求监听器
	 */
	private HttpResponseListener mHttpDataHandler;

	/**
	 * 使用Http监听器创建Http请求对象
	 * 
	 * @param httpDataHandler
	 */
	public AsyncHttpClient(HttpResponseListener httpDataHandler) {
		mHttpEngin = HttpEngine.getInstance();
		mHttpDataHandler = httpDataHandler;
	}

	/**
	 * 发送HTTP请求
	 * 
	 * @param urlId
	 */
	public void sendRequest(int urlId, int requestType) {
		sendRequest(urlId, null, -1, false, requestType);
	}

	/**
	 * 发送带参数的HTTP请求
	 * 
	 * @param urlId
	 * @param params
	 */
	public void sendRequest(int urlId, Map<String, String> params,
			int requestType) {
		sendRequest(urlId, params, -1, false, requestType);
	}

	public void sendRequest(int urlId, Map<String, String> params,
			Map<String, File> uploadFiles, int requestType) {
		sendRequest(urlId, params, uploadFiles, null, -1,
				DEFAULT_RESPONSE_TYPE, false, false, requestType);

	}

	/**
	 * 发送的请求带请求ID,有时候可能一个接口需要重复的同时请求，ID来区分各个请求的结果
	 * 
	 * @param urlId
	 * @param params
	 * @param connectionId
	 */
	public void sendRequest(int urlId, Map<String, String> params,
			int connectionId, int requestType) {
		sendRequest(urlId, params, connectionId, false, requestType);
	}

	/**
	 * 发送HTTP请求，并移除当前排队序列中的所有请求
	 * 
	 * @param urlId
	 * @param params
	 * @param connectionId
	 * @param removeBefore
	 */
	public void sendRequest(int urlId, Map<String, String> params,
			int connectionId, boolean removeBefore, int requestType) {
		sendRequest(urlId, params, connectionId, removeBefore, false,
				requestType);
	}

	/**
	 * 发送HTTP请求
	 * 
	 * @param urlId
	 *            URL ID
	 * @param params
	 *            请求的参数
	 * @param connectionId
	 *            连接ID
	 * @param removeBefore
	 *            是否清空消息队列中的请求
	 * @param isCache
	 *            是否缓存该接口
	 */
	public void sendRequest(int urlId, Map<String, String> params,
			int connectionId, boolean removeBefore, boolean isCache,
			int requestType) {
		sendRequest(urlId, params, connectionId, DEFAULT_RESPONSE_TYPE,
				removeBefore, isCache, requestType);
	}

	/**
	 * 发送HTTP请求
	 * 
	 * @param urlId
	 *            urlID
	 * @param params
	 *            参数
	 * @param connectionId
	 *            连接ID
	 * @param resType
	 *            数据返回类型
	 * @param removeBefore
	 *            移除排队序列
	 * @param isCache
	 *            是否缓存接口,之后再resType为RES_TYPE_TREE_MAP的情况下才可以缓存接口
	 * 
	 */
	public void sendRequest(int urlId, Map<String, String> params,
			int connectionId, int resType, boolean removeBefore,
			boolean isCache, int requestType) {
		sendRequest(urlId, params, null, null, connectionId, resType,
				removeBefore, isCache, requestType);
	}

	public void sendRequest(int urlId, Map<String, String> params,
			Map<String, File> uploadFiles, List<Header> headers,
			int connectionId, int resType, boolean removeBefore,
			boolean isCache, int requestType) {
		LOG.i(TAG, "sendRequest:" + urlId + ", params:" + params + ", resType:"
				+ resType + ", connectionId:" + connectionId + ", remove:"
				+ removeBefore + ", cached:" + isCache + ", requestType:"
				+ requestType);
		Request request = new Request(urlId, params, uploadFiles, headers,
				mHttpDataHandler, connectionId, resType, removeBefore, isCache,
				false, requestType);
		sendRequest(request);
	}

	public void sendRequest(Request request) {
		request.setAsyncHttpClient(this);
		mHttpEngin.sendRequest(request);
	}
}
