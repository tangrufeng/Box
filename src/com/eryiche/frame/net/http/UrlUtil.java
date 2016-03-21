package com.eryiche.frame.net.http;



/**
 * URL 工具类
 * @author EX-XIAOFANQING001
 *
 */
public class UrlUtil {

	/**
	 * 不需要解析的URL
	 */
	private final static int[] NO_PARSER_URLS = new int[]{
//		R.string.GET_VCODE
	};
	
	/**
	 * 是否是不需要做数据解析的URL
	 * @param urlId
	 * @return 如果URL不需要做数据解析返回true
	 */
	public static boolean isNoParseUrl(int urlId) {
		for (int i=0; i<NO_PARSER_URLS.length; i++) {
			if (urlId == NO_PARSER_URLS[i]) {
				return true;
			}
		}
		return false;
	}
}
