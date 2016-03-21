package com.eryiche.frame.net.http;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.eryiche.frame.action.HttpResponseListener;
import com.eryiche.frame.dataparser.DataParserFactory;
import com.eryiche.frame.dataparser.TreeHashMap;
import com.eryiche.frame.util.LOG;

/**
 * 处理HTTP返回信息
 * @author Administrator
 *
 */
public class ResponseHandler {
	
	static final String TAG = ResponseHandler.class.getSimpleName();
	
	/**
     * 处理HTTP返回结果
     * @param currentRequest
     * @param datas
     */
    void handleRespnseData(final Request currentRequest, final byte[] datas) {
    	// 如果当前的URL不需要解析，则直接把二进制返回
    	if (UrlUtil.isNoParseUrl(currentRequest.getUrlId())) {
    		HttpResponseListener listener = currentRequest.getHttpResponseListener();
	        if (listener != null) {
	         	listener.response(HttpResponseListener.NET_SUCCESS, datas, currentRequest.getUrlId(), currentRequest.getConnectionId());
	        }
    		return ;
    	} 

		HttpResponseListener listener = currentRequest.getHttpResponseListener();
		
		int resType = currentRequest.getResponseType();
		switch (resType) {
		case Request.RES_TYPE_BINARY:
			cache(currentRequest, datas);
			if (listener != null) {
	         	listener.response(HttpResponseListener.NET_SUCCESS, datas, currentRequest.getUrlId(), currentRequest.getConnectionId());
	        }
			break;
		case Request.RES_TYPE_TEXT:
			try {
				String text = new String(datas, "utf-8");
				cache(currentRequest, text);
				if (listener != null) {
		         	listener.response(HttpResponseListener.NET_SUCCESS, text, currentRequest.getUrlId(), currentRequest.getConnectionId());
		        }	
			} catch (UnsupportedEncodingException e1) {
				// ignore
			}
			break;
		case Request.RES_TYPE_JSON_OBJECT:
		case Request.RES_TYPE_TREE_MAP:
			handleResponse(currentRequest, datas);
			break;
		}
    }
    
    
    /**
     * 框架如何调用返回处理
     * 
     * @param currentRequest
     * @param datas
     */
    public void handleResponse(final Request currentRequest, final byte[] datas) {
    	HttpResponseListener listener = currentRequest.getHttpResponseListener();
    	int resType = currentRequest.getResponseType();
		switch (resType) {
			case Request.RES_TYPE_JSON_OBJECT:
				try {
					String text = new String(datas, "utf-8");
					JSONObject jsonObject = new JSONObject(text);
					Log.i(TAG,jsonObject.toString());
					cache(currentRequest, jsonObject);

					if (listener != null) {
			         	listener.response(HttpResponseListener.NET_SUCCESS, jsonObject, currentRequest.getUrlId(), currentRequest.getConnectionId());
			        }	
				} catch (UnsupportedEncodingException e1) {
					// ignore
				} catch (JSONException e) {
					LOG.i(TAG, "json解析失败", e);
					// 数据解析失败
					backRequestError(HttpResponseListener.NET_DATA_PARSER_ERROR, currentRequest, datas);
				}
				break;
			case Request.RES_TYPE_TREE_MAP:
				// 解析数据
		    	try {
		    		LOG.i(TAG, "response data:" + new String(datas));
		    		
		    		// 把byte数组转换成Map树的
					TreeHashMap<String, Object> treeHashMap = DataParserFactory.createDataParser(DataParserFactory.parserType).parseData(datas);
		
					LOG.i(TAG, "parse data: " + treeHashMap);
					
					// 看看是否需要缓存
					cache(currentRequest, treeHashMap);
		
					if (listener != null) {
			         	listener.response(HttpResponseListener.NET_SUCCESS, treeHashMap, currentRequest.getUrlId(), currentRequest.getConnectionId());
			        }
		    	} catch (Exception e) {
		    		LOG.i(TAG, "XML解析失败", e);
					// 数据解析失败
					backRequestError(HttpResponseListener.NET_DATA_PARSER_ERROR, currentRequest, datas);
				}
				break;
		}
    }
		
	protected void cache(Request r, Object data) {
    	// 看看是否需要缓存
		if (r.isCache()) {
			LOG.i(TAG, "cache data"  );
			DataCache.saveToCache(r.getUrlId(), r.getConnectionId(), data);
		}
    }
	
	  
    /**
     * 返回服务器网络连接请求错误给客户端
     * @param code 错误码
     * @param request 当前的数据请求对象
     */
    public static void backRequestError(int code, Request request, Object datas) {
    	HttpResponseListener listener = request.getHttpResponseListener();
        if (listener != null) {
        	// 如果是解析失败，直接把数据返回给页面
        	if (code == HttpResponseListener.NET_DATA_PARSER_ERROR) {
        		listener.response(code, datas, request.getUrlId(), request.getConnectionId());
        	} else {
        		listener.response(code, null, request.getUrlId(), request.getConnectionId());
        	}
        }
    }
    
}
