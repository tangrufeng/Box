package com.eryiche.frame.net.http;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * 
 * 数据缓存类
 * @description 数据缓存类
 */
public final class DataCache {


	/**
	 * 数据缓存map
	 */
	private static HashMap<Integer, Object> dataCacheMaps = new HashMap<Integer, Object>();


	/**
	 * int长度枚举
	 */
	private final static int[] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE };

	/**
	 * 根据urlId和connectionId获取保存的缓存数据
	 * @param urlId 请求urlId
	 * @param connectionId 请求连接id
	 * @return
	 */
	public static Object getCache(int urlId, int connectionId) {
		return dataCacheMaps.get(dealUrlId(urlId) + connectionId);
	}


	/**
	 * @param urlId
	 *            请求urlId
	 * @param connectionId
	 *            请求连接id
	 * @param maps
	 *            缓存请求体
	 * @param data
	 *            缓存数据
	 * @param state
	 *            请求响应状态码
	 */
	public static void saveToCache(int urlId, int connectionId, Object data) {
		dataCacheMaps.put(dealUrlId(urlId) + connectionId, data);
	}

	/**
	 * 根据urlId和connectionId删除缓存数据
	 * 
	 * @param urlId
	 *            请求urlId
	 * @param connectionId
	 *            请求连接id
	 */
	public static void clearCacheById(int urlId, int connectionId) {
		dataCacheMaps.remove(dealUrlId(urlId) + connectionId);
	}

	/**
	 * 删除所有缓存数据
	 */
	public static void clearAll() {
		dataCacheMaps.clear();
	}

	/**
	 * [对自动生成的请求UrlId进行处理]<BR>
	 * 
	 * @param urlId
	 *            请求id
	 * @return 处理后的urlId author:CUNGUANTONG465 editor:CUNGUANTONG465
	 *         time:2012-2-4
	 */
	private static int dealUrlId(int urlId) {
		int mode = 10000000;
		int afterdeal = 0;
		
		urlId = Math.abs(urlId);
		try {
			int length = sizeOfInt(urlId);
		
			DecimalFormat format = new DecimalFormat("000");
			mode = Integer.valueOf(format.format(Math.pow(10, length - 3)));
			afterdeal = urlId % mode * 100;
		} catch (Exception e) {
			afterdeal = urlId % mode * 100;
		}
		
		return afterdeal;
	}

	/**
	 * [获得Int值长度位数]<BR>
	 * [功能详细描述]
	 * 
	 * @param x
	 *            int值
	 * @return 长度 author:CUNGUANTONG465 editor:CUNGUANTONG465 time:2012-2-4
	 */
	static int sizeOfInt(int x) {
		for (int i = 0;; i++)
			if (x <= sizeTable[i])
				return i + 1;
	}

}
