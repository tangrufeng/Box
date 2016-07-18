/* 
 * @Title:  XHKActionHelper.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-9-10 下午11:27:02 
 * @version:  V1.0 
 */
package com.xhk.wifibox.action;

import android.content.Context;

import com.xhk.wifibox.track.TrackMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tang
 * 
 */
public class XHKActionHelper {

	private final static String TAG = XHKActionHelper.class.getSimpleName();

	private static XHKActionHelper intance =null;

	private List<XHKAction> actions = null;

	private XHKActionHelper() {
	}

	private XHKActionHelper(Context ctx) {
		actions = new ArrayList<XHKAction>();
		actions.add(new XMAction(ctx));
//		actions.add(new XMLYAction(ctx));
	}

	public static XHKActionHelper getIntance(Context ctx){
		if(intance==null){
			intance=new XHKActionHelper(ctx);
		}
		return intance;
	}

	public List<TrackMeta> searchSong(String key, int pageSize, int pageIndex) {
		List<TrackMeta> songs = new ArrayList<TrackMeta>();
		for(XHKAction action:actions){
			List<TrackMeta> list=action.searchSong(key, pageSize, pageIndex);
			if(list!=null){
				songs.addAll(list);
			}
		}
		return songs;
	}
}
