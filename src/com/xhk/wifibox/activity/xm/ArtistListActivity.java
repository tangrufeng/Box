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
import android.widget.ArrayAdapter;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.xhk.wifibox.R;
import com.xhk.wifibox.action.XMAction;
import com.xhk.wifibox.activity.BaseListActivity;
import com.xhk.wifibox.adapter.BaseAdapter;
import com.xhk.wifibox.adapter.xm.ArtistListAdapter;
import com.xiami.sdk.entities.ArtistRegion;
import com.xiami.sdk.entities.OnlineArtist;

/**
 * @author tang
 * 
 */
public class ArtistListActivity extends BaseListActivity<OnlineArtist> {

	private XMAction action = null;
    private ArtistRegion currentAR;

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
		showDialog(DAILOG_LOADDATE);
		setRefreshMode(Mode.PULL_FROM_END);
		currentAR=(ArtistRegion)getIntent().getSerializableExtra("ArtistRegion");
		loadDataList();
		
	}

	



	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xhk.wifibox.activity.BaseListActivity#createAdapter()
	 */
	@Override
	public BaseAdapter<OnlineArtist> createAdapter() {
		return new ArtistListAdapter(this, R.layout.xm_artists_item, getList());
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
				List<OnlineArtist> temp = action.getArtistsSync(currentAR, 200, currentPage());
				getList().addAll(temp);
				getHandler().sendEmptyMessage(MSG_DATA_GETTED);
				dismissDialog(1);
			}
		}.start();

	}





	/* (non-Javadoc)
	 * @see com.xhk.wifibox.activity.BasePlayerActivity#getActivityTitle()
	 */
	@Override
	protected String getActivityTitle() {
		return getString(getIntent().getIntExtra("titleName",R.string.net_xiami));
	}
}
