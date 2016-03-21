/* 
 * @Title:  AppVersionInfo.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-8-20 上午11:17:01 
 * @version:  V1.0 
 */
package com.xhk.wifibox.model;

import java.io.Serializable;

import org.json.JSONObject;

import com.xhk.wifibox.utils.JSONUtil;

/**
 * @author tang
 * 
 */
public class UpdateInfo implements Serializable {

	private static final long serialVersionUID = 5785093262951295844L;

	/**
	 * 客户编号
	 */
	public String id;
	/**
	 * 固件版本编号
	 */
	public String version;
	
	/**
	 * 固件版本地址
	 */
	public String url;
	
	/**
	 * App版本号
	 */
	public int appVersionCode;
	
	/**
	 * App版本地址
	 */
	public String appUrl;
	
	/**
	 * App版本描述
	 */
	public String appReleaseNote;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UpdateInfo [id=" + id + ", version=" + version + ", url=" + url
				+ ", appVersionCode=" + appVersionCode + ", appUrl=" + appUrl
				+ ", appReleaseNote=" + appReleaseNote + "]";
	}
	
	
	
}
