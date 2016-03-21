/* 
 * @Title:  CollectListActivity.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-7 下午1:55:57 
 * @version:  V1.0 
 */
package com.xhk.wifibox.activity.xm;

import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.xhk.wifibox.R;
import com.xhk.wifibox.action.XMAction;
import com.xhk.wifibox.activity.BaseListActivity;
import com.xhk.wifibox.adapter.BaseAdapter;
import com.xhk.wifibox.adapter.xm.RadioListAdapter;
import com.xiami.sdk.entities.OnlineRadio;
import com.xiami.sdk.entities.RadioCategoryNew;

/**
 * @author tang
 * 
 */
public class RadioListActivity extends BaseListActivity<OnlineRadio> {

	private XMAction action = null;

	private List<RadioCategoryNew> rcns = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xhk.wifibox.activity.BaseListActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		action = new XMAction(this);
		getList().clear();
		showDialog(1);
		new Thread() {
			@Override
			public void run() {
				rcns = action.fetchRadioCategorySync();
				loadDataList();
			}
		}.start();
		setRefreshMode(Mode.PULL_FROM_END);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1: {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("好音乐马上就来...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			return dialog;
		}
		}
		return null;
	}


	/* (non-Javadoc)
	 * @see com.xhk.wifibox.activity.BasePlayerActivity#getActivityTitle()
	 */
	@Override
	protected String getActivityTitle() {
		return getString(R.string.xm_main_radio);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xhk.wifibox.activity.BaseListActivity#createAdapter()
	 */
	@Override
	public BaseAdapter<OnlineRadio> createAdapter() {
		return new RadioListAdapter(this, R.layout.xm_radios_item, getList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xhk.wifibox.activity.BaseListActivity#loadAlbumList()
	 */
	@Override
	public void loadDataList() {
		new Thread() {
			@Override
			public void run() {
				if (currentPage() <= rcns.size()) {
					List<OnlineRadio> temp = action.getRaidoList(
							rcns.get(currentPage() - 1), 50, 1);
					getList().addAll(temp);
					Log.d(TAG,getList().toString());
					getHandler().sendEmptyMessage(MSG_DATA_GETTED);
				}
				dismissDialog(1);
			}
		}.start();

	}
}
