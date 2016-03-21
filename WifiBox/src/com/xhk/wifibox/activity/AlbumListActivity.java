/* 
 * @Title:  PlayListActivity.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-9-19 下午10:55:57 
 * @version:  V1.0 
 */
package com.xhk.wifibox.activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.xhk.wifibox.R;
import com.xhk.wifibox.action.XHKAction;
import com.xhk.wifibox.adapter.AlbumListAdapter;
import com.xhk.wifibox.adapter.BaseAdapter;
import com.xhk.wifibox.track.Album;
import com.xhk.wifibox.utils.Contants;

/**
 * @author tang
 * 
 */
public class AlbumListActivity extends BaseListActivity<Album> {

	private String TAG = this.getClass().getSimpleName();
	private Class<XHKAction> clazz;
	private Method method;
	private Object dataSource;
	private String playListId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent().getBooleanExtra(Contants.INTENT_EXTRA_LOADDATA_NOFRESH,
				false)) {
			setRefreshMode(Mode.DISABLED);
		}
		// initView();
		showDialog(1);
		initDataSource();
		loadDataList();
	}

	@SuppressWarnings("unchecked")
	private void initDataSource() {
		playListId = getIntent().getStringExtra(Contants.INTENT_EXTRA_LIST_ID);
		clazz = (Class<XHKAction>) getIntent().getSerializableExtra(
				Contants.INTENT_EXTRA_LOADDATA_CLASS);
		String methodName = getIntent().getStringExtra(
				Contants.INTENT_EXTRA_LOADDATA_METHOD_NAME);
		try {
			method = clazz.getDeclaredMethod(methodName, String.class,
					int.class, int.class);
			dataSource = clazz.getConstructor(Context.class).newInstance(this);
		} catch (NoSuchMethodException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		} catch (IllegalAccessException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		} catch (InstantiationException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		} catch (InvocationTargetException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}

	}

	@Override
	public BaseAdapter<Album> createAdapter() {
		return new AlbumListAdapter(this, R.layout.xm_albums_item, getList(),
				(Class) getIntent().getSerializableExtra(
						Contants.INTENT_EXTRA_LOADDATA_CLASS));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xhk.wifibox.activity.BaseListActivity#loadDataList()
	 */
	@Override
	public void loadDataList() {
		new Thread() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				List<Album> temp;
				try {
					temp = (List<Album>) method.invoke(dataSource, playListId,
							20, currentPage());
					getList().addAll(temp);
				} catch (IllegalAccessException e) {
					Log.e(TAG, e.getLocalizedMessage(), e);
				} catch (IllegalArgumentException e) {
					Log.e(TAG, e.getLocalizedMessage(), e);
				} catch (InvocationTargetException e) {
					Log.e(TAG, e.getLocalizedMessage(), e);
				}
				getHandler().sendEmptyMessage(MSG_DATA_GETTED);
				dismissDialog(1);
			}
		}.start();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xhk.wifibox.activity.BasePlayerActivity#getActivityTitle()
	 */
	@Override
	protected String getActivityTitle() {
		return getIntent().getStringExtra(Contants.INTENT_EXTRA_LIST_NAME);
	}


}
