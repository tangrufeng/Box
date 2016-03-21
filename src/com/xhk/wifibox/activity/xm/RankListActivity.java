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

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.xhk.wifibox.R;
import com.xhk.wifibox.action.XMAction;
import com.xhk.wifibox.activity.BaseListActivity;
import com.xhk.wifibox.adapter.BaseAdapter;
import com.xhk.wifibox.adapter.xm.RankListAdapter;
import com.xiami.sdk.entities.RankListItem;

/**
 * @author tang
 *
 */
public class RankListActivity extends BaseListActivity<RankListItem> {

	private XMAction action=null;
	
	/* (non-Javadoc)
	 * @see com.xhk.wifibox.activity.BaseListActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		action=new XMAction(this);
		getList().clear();
		showDialog(1);
		setRefreshMode(Mode.DISABLED);
		loadDataList();
		
	}
	
	/* (non-Javadoc)
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
		return getString(R.string.xm_main_list);
	}

	/* (non-Javadoc)
	 * @see com.xhk.wifibox.activity.BaseListActivity#createAdapter()
	 */
	@Override
	public BaseAdapter<RankListItem> createAdapter() {
		return new RankListAdapter(this, R.layout.xm_rank_item, getList());
	}

	/* (non-Javadoc)
	 * @see com.xhk.wifibox.activity.BaseListActivity#loadAlbumList()
	 */
	@Override
	public void loadDataList() {
		new Thread() {
			@Override
			public void run() {
				List<RankListItem> temp = action.getRankListsSync();
				getList().addAll(temp);
				getHandler().sendEmptyMessage(MSG_DATA_GETTED);
				dismissDialog(1);
			}
		}.start();

	}

}
