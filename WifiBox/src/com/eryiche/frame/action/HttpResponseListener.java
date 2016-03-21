package com.eryiche.frame.action;

/**
 * 服务器相应监听器
 * @author EX-XIAOFANQING001
 *
 */
public interface HttpResponseListener {
	
	
	//  自定义
	
	/**
	 * 服务器返回成功
	 */
	public static final int NET_SUCCESS = 0;
	
	
	/**
	 * URL地址非法
	 */
	public static final int NET_MALFORMED_URL = 1;
	
	/**
	 * 网络连接错误
	 */
	public static final int NET_CONNECT_ERROR = 2;
	
	/**
	 * 404错误
	 */
	public static final int NET_NOT_FUND_404 = 3;
    
	/**
	 * 500错误
	 */
	public static final int NET_SERVER_ERROR_500 = 4;
	
	/**
	 * 服务器返回除200、404、500以外的其他错误 
	 */
	public static final int NET_SERVER_RESPONSE_NOT_200 = 5;
	
	/**
	 * 数据解析错误
	 */
	public static final int NET_DATA_PARSER_ERROR = 6;
	
	/**
	 * 未知错误
	 */
	public static final int NET_UNKNOW_ERROR = 7;
	
	

	/**
	 * 接口返回的响应到页面的方法
	 * 
	 * @param state  请求响应状态
	 * @param data   通用解析后的响应报文
	 * @param urlId	  请求URLID 	
	 * @param connectionId  请求的connectionId，区别同一URL的不同数据请求
	 */
	public void response(int state, Object data, int urlId, int connectionId);
}
