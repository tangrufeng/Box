/* 
 * @Title:  BoxAP.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-31 下午7:32:46 
 * @version:  V1.0 
 */
package com.xhk.wifibox.box;

import org.json.JSONObject;

import android.util.Log;

import com.xhk.wifibox.utils.JSONUtil;

/**
 * @author tang
 * 
 */
public class BoxAP implements Comparable<BoxAP> {

	private static final String TAG = BoxAP.class.getSimpleName();

	public String SSID = "";

	public String BSSID = "";

	public int Rssi = 0;

	public int Channel = 0;

	public String Encry = "";

	public String Auth = "";

	public static BoxAP buildBoxAP(JSONObject jo) {
		if (jo == null) {
			return null;
		}

		if (jo.has("SSID") && jo.has("BSSID") && jo.has("Rssi")
				&& jo.has("Channel") && jo.has("Encry") && jo.has("Auth")) {
			BoxAP boxAP = new BoxAP();
			boxAP.SSID = JSONUtil.getString(jo, "SSID");
			boxAP.BSSID = JSONUtil.getString(jo, "BSSID");
			boxAP.Encry = JSONUtil.getString(jo, "Encry");
			boxAP.Auth = JSONUtil.getString(jo, "Auth");
			boxAP.Channel = JSONUtil.getInt(jo, "Channel");
			boxAP.Rssi = JSONUtil.getInt(jo, "Rssi");
			return boxAP;

		} else {
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BoxAP [SSID=" + SSID + ", BSSID=" + BSSID + ", Rssi=" + Rssi
				+ ", Channel=" + Channel + ", Encry=" + Encry + ", Auth="
				+ Auth + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(BoxAP another) {
		if(another==null){
			return 1;
		}
		return Rssi < another.Rssi ? -1 : (Rssi == another.Rssi ? 0 : 1);
	}
}
