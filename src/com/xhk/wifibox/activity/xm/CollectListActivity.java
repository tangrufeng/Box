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
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.xhk.wifibox.R;
import com.xhk.wifibox.action.XMAction;
import com.xhk.wifibox.activity.BaseListActivity;
import com.xhk.wifibox.adapter.AlbumListAdapter;
import com.xhk.wifibox.adapter.BaseAdapter;
import com.xhk.wifibox.track.Album;

/**
 * @author tang
 *
 */
public class CollectListActivity extends BaseListActivity<Album> {

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
		return getString(R.string.xm_main_collect);
	}

	/* (non-Javadoc)
	 * @see com.xhk.wifibox.activity.BaseListActivity#createAdapter()
	 */
	@Override
	public BaseAdapter<Album> createAdapter() {
		return new AlbumListAdapter(this, R.layout.xm_albums_item, getList(),XMAction.class);
	}

	/* (non-Javadoc)
	 * @see com.xhk.wifibox.activity.BaseListActivity#loadAlbumList()
	 */
	@Override
	public void loadDataList() {
		new Thread() {
			@Override
			public void run() {
				Log.d("CollectListActivity", "=============CollectListActivity==========");
				List<Album> temp = action.getCollectsRecommendSync(10,currentPage());
				Log.d("CollectListActivity", temp.toString());
				getList().addAll(temp);
				getHandler().sendEmptyMessage(MSG_DATA_GETTED);
				dismissDialog(1);
			}
		}.start();

	}

}
