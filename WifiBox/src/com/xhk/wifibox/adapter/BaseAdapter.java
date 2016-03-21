/* 
 * @Title:  BaseAdapter.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-12-19 上午8:12:40 
 * @version:  V1.0 
 */
package com.xhk.wifibox.adapter;

import java.util.List;

import com.xiami.sdk.entities.OnlineArtist;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * @author tang
 *
 */
public abstract class BaseAdapter<T> extends ArrayAdapter<T> {

	/**
	 * @param context
	 * @param resource
	 */
	public BaseAdapter(Context context, int resource) {
		super(context, resource);
	}
	/**
	 * 
	 */
	public BaseAdapter(Context context, int resource,List<T> objects) {
		super(context, resource, objects);
	}


	public abstract void setEditMode(boolean isEdit) ;

	public abstract void clearFlush();
	
	public abstract List<T> getFlush();
}
