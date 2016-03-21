/* 
 * @Title:  BoxListAdapter.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-11-9 上午12:10:31 
 * @version:  V1.0 
 */
package com.xhk.wifibox.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.xhk.wifibox.box.Box;
import com.xhk.wifibox.box.BoxCache;
import com.xhk.wifibox.box.BoxControler;
import com.xhk.wifibox.utils.Util;
import com.xhk.wifibox.view.BoxView;
import com.xhk.wifibox.view.BoxView.OnBoxViewOpenListener;

/**
 * @author tang
 * 
 */
public class BoxListAdapter extends ArrayAdapter<Box> implements
		OnBoxViewOpenListener{

	private int resource;
	private Context context;
	private static final String TAG = BoxListAdapter.class.getSimpleName();
	private ListView lv = null;

	BoxControler bControler = BoxControler.getInstance();

	/**
	 * @param context
	 * @param resource
	 * @param textViewResourceId
	 * @param objects
	 */
	public BoxListAdapter(Context context, int resource, List<Box> objects) {
		super(context, resource, objects);
		this.context = context;
		this.resource = resource;
		bControler.setContext(context);
	}

	public void setListView(ListView lv) {
		this.lv = lv;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Box box = getItem(position);
		if (convertView == null) {
			BoxView bv = new BoxView(context);
			bv.setBox(box);
			bv.setOnBoxViewOpenListenter(this);
			return bv;
		} else {
			return convertView;

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xhk.wifibox.view.BoxView.OnBoxViewOpenListener#change(com.xhk.wifibox
	 * .box.Box)
	 */
	@Override
	public void change(Box box) {
		Log.d(TAG, box.toString());
		if (lv != null) {
			Util.setListViewHeightBasedOnChildren(lv, context);
		}

	}

}