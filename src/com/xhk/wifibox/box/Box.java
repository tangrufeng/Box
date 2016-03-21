/* 
 * @Title:  Box.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-23 上午12:54:55 
 * @version:  V1.0 
 */
package com.xhk.wifibox.box;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.xhk.wifibox.utils.JSONUtil;

/**
 * 音响描述
 * 
 * @author tang
 * 
 */
public class Box implements Parcelable {
	
	private static final String TAG=Box.class.getSimpleName();
	
	/**
	 * 音响IP地址
	 */
	public String deviceIpAddr;
	
	/**
	 * 音响固件版本
	 */
	public String deviceFiremwareVersion = "";
	
	/**
	 * 音响名称
	 */
	public String deviceName = "";
	
	/**
	 * 音响api端口号
	 */
	public int httpApiPort;
	
	/**
	 * 是否正在播放
	 */
	public boolean isPlaying=false;
	
	/**
	 * 是否为当前控制的音响
	 */
	public boolean isCurrent=false;
	
	
	public boolean isOperating=false;
	
	/**
	 * 是否有新的固件版本
	 */
	public boolean haveNewVersion=false;

	/**
	 * @param source
	 */
	public Box(Parcel source) {
		deviceIpAddr=source.readString();
		deviceFiremwareVersion=source.readString();
		deviceName=source.readString();
		httpApiPort=source.readInt();
	}
	
	public Box(){
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Box [deviceIpAddr=" + deviceIpAddr
				+ ", deviceFiremwareVersion=" + deviceFiremwareVersion
				+ ", deviceName=" + deviceName + ", httpApiPort=" + httpApiPort
				+ ", isPlaying=" + isPlaying + ", isCurrent=" + isCurrent + "]";
	}

	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((deviceName == null) ? 0 : deviceName.hashCode());
		return result;
	}

	/*
	 * 暂时通过音响名称来判断两个音响是否为同一个
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Box other = (Box) obj;
		if (deviceName == null) {
			if (other.deviceName != null)
				return false;
		} else if (!deviceName.equals(other.deviceName))
			return false;
		return true;
	}

	public static Box buildBox(String jsonStr){
		Log.d(TAG, jsonStr);
		JSONObject json = null;
		try {
			json = new JSONObject(jsonStr);
		} catch (JSONException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
			return null;
		}

		JSONObject boxJSON = JSONUtil.getJSONObject(json, "Body");
		if (boxJSON.has("DeviceIpAddr")
				&& boxJSON.has("DeviceFiremwareVersion")
				&& boxJSON.has("DeviceName") && boxJSON.has("HttpApiPort")) {
			Box box = new Box();
			box.deviceIpAddr = JSONUtil.getString(boxJSON, "DeviceIpAddr");
			box.deviceFiremwareVersion = JSONUtil.getString(boxJSON,
					"DeviceFiremwareVersion");
			box.deviceName = JSONUtil.getString(boxJSON, "DeviceName");
			box.httpApiPort = JSONUtil.getInt(boxJSON, "HttpApiPort", 80);
			return box;

		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(deviceIpAddr);
		dest.writeString(deviceFiremwareVersion);
		dest.writeString(deviceName);
		dest.writeInt(httpApiPort);
	}

	public final static Parcelable.Creator<Box> CREATOR = new Parcelable.Creator<Box>() {

		@Override
		public Box createFromParcel(Parcel source) {
			return new Box(source);
		}

		@Override
		public Box[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Box[size];
		}
	};
}
