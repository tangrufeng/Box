/*
 * 文件名: Request.java
 * 版    权：  Copyright PingAn Technology All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: EX-XIAOFANQING001
 * 创建时间: 2012-6-1
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.eryiche.frame.net.http;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import com.eryiche.frame.action.AsyncHttpClient;
import com.eryiche.frame.action.HttpResponseListener;

/**
 * 封装了一次HTTP请求信息
 * 
 * @author EX-XIAOFANQING001
 * @date 2012-6-1
 * @version [Android PABank C01, @2012-6-1]
 * @description
 */
public class Request {
    
	/**
	 * 返回二进制数组
	 */
	public final static int RES_TYPE_BINARY = 1;
	
	/**
	 * 返回文本
	 */
	public final static int RES_TYPE_TEXT = 2;
	
	/**
	 * 已JSON对象的形式返回
	 */
	public final static int RES_TYPE_JSON_OBJECT = 3;
	
	/**
	 * 使用TreeMap对数据进行解析
	 * 
	 * 解析数据的类型由DataParserFactory里面的parserType决定
	 * 
	 */
	public final static int RES_TYPE_TREE_MAP = 4;
	
	
    /**
     * 当前请求的URL的资源ID
     */
    private int urlId;
    
    /**
     * 连接ID
     */
    private int connectionId;
    
    /**
     * 要上送的参数
     */
    private Map<String, String> param;
    
    /**
     * 要上传的文件
     */
    private Map<String, File> uploadFiles;
    
    /**
     * 要上传
     */
    private List<Header> mHeaders;
    
    /**
     * 发送该请求的时候是否移除队列中之前的请求
     */
    private boolean isRemoveBefore;
    
    /**
     * 是否缓存该接口
     */
    private boolean isCache;
    
    /**
     * 要求返回给用户的数据类型
     * 
     */
    private int responseType;
    
    /**
     * 结果处理监听器
     */
    private HttpResponseListener mHttpResponseListener;
    
    /**
     * 是否剥离头信息
     */
    private boolean isHandleHead = false;
    
    
    private int requestType;
    
    private AsyncHttpClient asyncHttpClient;
    
    public Request() {}
    
    public Request(int urlId, Map<String, String> param, HttpResponseListener httpDataHandler, int connectionId, int responseType, boolean isRemoveBefore, boolean isCache,int requestType) {
    	this(urlId, param, httpDataHandler, connectionId, responseType, isRemoveBefore, isCache, true,requestType);
    }
    
    public Request(int urlId, Map<String, String> param, HttpResponseListener httpDataHandler, int connectionId, int responseType, boolean isRemoveBefore, boolean isCache, boolean isHandleHead,int requestType) {
        this(urlId, param, null, null, httpDataHandler, connectionId, responseType, isRemoveBefore, isCache, isHandleHead,requestType);
    }
    
    public Request(int urlId, Map<String, String> param, Map<String, File> uploadFiles, List<Header> headers, HttpResponseListener httpDataHandler, int connectionId, int responseType, boolean isRemoveBefore, boolean isCache, boolean isHandleHead,int requestType) {
        this.setUrlId(urlId);
        this.setParam(param);
        this.setHttpDataHandler(httpDataHandler);
        this.setConnectionId(connectionId);
        this.setRemove(isRemoveBefore);
        this.setCache(isCache);
        this.setResponseType(responseType);
        this.setHandleHead(isHandleHead);
        this.setUploadFiles(uploadFiles);
        this.setHeaders(headers);
        this.setRequestType(requestType);
    }

	public void setHttpDataHandler(HttpResponseListener mHttpDataHandler) {
		this.mHttpResponseListener = mHttpDataHandler;
	}

	public HttpResponseListener getHttpResponseListener() {
		return mHttpResponseListener;
	}
	
	public void setRequestType(int requestType){
		this.requestType=requestType;
	}

	public int getRequestType(){
		return requestType;
	}
	
	public void setUrlId(int urlId) {
		this.urlId = urlId;
	}

	public int getUrlId() {
		return urlId;
	}

	public void setParam(Map<String, String> param) {
		this.param = param;
	}

	public Map<String, String> getParam() {
		return param;
	}

	public void setConnectionId(int connectionId) {
		this.connectionId = connectionId;
	}

	public int getConnectionId() {
		return connectionId;
	}

	public void setRemove(boolean isRemove) {
		this.isRemoveBefore = isRemove;
	}

	public boolean isRemove() {
		return isRemoveBefore;
	}

	public boolean isCache() {
		return isCache;
	}

	public void setCache(boolean isCache) {
		this.isCache = isCache;
	}

	public int getResponseType() {
		return responseType;
	}

	public void setResponseType(int responseType) {
		this.responseType = responseType;
	}

	public boolean isHandleHead() {
		return isHandleHead;
	}

	public void setHandleHead(boolean isHandleHead) {
		this.isHandleHead = isHandleHead;
	}

	public AsyncHttpClient getAsyncHttpClient() {
		return asyncHttpClient;
	}

	public void setAsyncHttpClient(AsyncHttpClient asyncHttpClient) {
		this.asyncHttpClient = asyncHttpClient;
	}

	public Map<String, File> getUploadFiles() {
		return uploadFiles;
	}

	public void setUploadFiles(Map<String, File> uploadFiles) {
		this.uploadFiles = uploadFiles;
	}

	public List<Header> getHeaders() {
		return mHeaders;
	}

	public void setHeaders(List<Header> mHeaders) {
		this.mHeaders = mHeaders;
	}
	
	public class RequestType{
		public final static int GET=1;
		public final static int POST=2;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Request [urlId=" + urlId + ", connectionId=" + connectionId
				+ ", param=" + param + ", uploadFiles=" + uploadFiles
				+ ", mHeaders=" + mHeaders + ", isRemoveBefore="
				+ isRemoveBefore + ", isCache=" + isCache + ", responseType="
				+ responseType + ", mHttpResponseListener="
				+ mHttpResponseListener + ", isHandleHead=" + isHandleHead
				+ ", requestType=" + requestType + ", asyncHttpClient="
				+ asyncHttpClient + "]";
	}
}

