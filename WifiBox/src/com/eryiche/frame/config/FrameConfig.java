package com.eryiche.frame.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.eryiche.frame.action.AsyncHttpClient;
import com.eryiche.frame.net.http.HttpEngine;
import com.eryiche.frame.net.http.HttpHelper;
import com.eryiche.frame.util.LOG;

/**
 * 读取配置文件
 * 
 * @author Administrator
 * 
 */
public class FrameConfig {
	
	private static final String TAG = FrameConfig.class.getSimpleName();

	private static final String CONFIG_FILE_NAME = "frame_config";

	// 这个类的所有属性都是初始配置
	
	/**
	 * 所有的配置信息都由程序启动的时候保存在这里
	 */
	private static Map<String, Object> configMap = new HashMap<String, Object>();

	public static void initConfig(Context ctx) {
		int id = ctx.getResources().getIdentifier(CONFIG_FILE_NAME, "xml", 	ctx.getPackageName());

		if (id == 0) {
			LOG.e(TAG, "没有配置文件,请在res/xml目录下放置配置文件frame_config.xml");
			return ;
		}
		
		XmlResourceParser xml = ctx.getResources().getXml(id);
		
		configMap.clear();
		
		int eventType = -1;
		while (eventType != XmlResourceParser.END_DOCUMENT) {
			if (eventType == XmlResourceParser.START_TAG) {
				String strNode = xml.getName();
				String value = 	xml.getAttributeValue(null, "value");
				configMap.put(strNode, value);
			} 
			
			try {
				eventType = xml.next();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				// ignore
			} catch (IOException e) {
				e.printStackTrace();
				// ignore
			}
		}
		
		xml.close();
		
		// 主机地址
		String host = (String) configMap.get("host");
		LOG.i(TAG, "host:" + host);
		HttpHelper.setHost(host);
		
		// 是否是在DEBUG模式
		String debugStr = (String) configMap.get("debug");
		LOG.i(TAG, "debug:" + debugStr);
		if ("true".equalsIgnoreCase(debugStr)) {
			LOG.isDebug = true;
		} else {
			LOG.isDebug = false;
		}
	
		// 是否保存日志到文件
		String saveLogStr = (String) configMap.get("is_save_log");
		if ("true".equalsIgnoreCase(saveLogStr)) {
			LOG.isSaveLog = true;
		} else {
			LOG.isSaveLog = false;
		}
		
		// 日志保存的路径
		String logFilePath = (String) configMap.get("log_file_path");
		LOG.logPath = logFilePath;
		
		
		String strType = (String) configMap.get("data_response_type");
		int resType  = Integer.parseInt(strType);
		LOG.i(TAG, "resType:" + resType);
		AsyncHttpClient.DEFAULT_RESPONSE_TYPE = resType; 
		
		String strReqType = (String) configMap.get("net_request_type");
		int reqType = Integer.parseInt(strReqType);
		HttpEngine.setRequestType(reqType);
	}
	
	/**
	 * 从配置文件读取指定的配置
	 * @param key
	 * @return
	 */
	public static String getConfigItem(String key) {
		if (configMap.isEmpty()) {
			return null;
		}
		
		return (String) configMap.get(key);
	}

}
