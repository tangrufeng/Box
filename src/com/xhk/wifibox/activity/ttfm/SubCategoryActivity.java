/* 
 * @Title:  SubCategoryActivity.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-20 上午12:16:24 
 * @version:  V1.0 
 */
package com.xhk.wifibox.activity.ttfm;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.widget.LinearLayout;

import com.xhk.wifibox.R;
import com.xhk.wifibox.action.TTFMAction;
import com.xhk.wifibox.activity.BasePlayerActivity;
import com.xhk.wifibox.adapter.ttfm.CategoryAdapater;
import com.xhk.wifibox.model.ParterTag;
import com.xhk.wifibox.utils.Contants;
import com.xhk.wifibox.view.MyGridView;

/**
 * @author tang
 * 
 */
public class SubCategoryActivity extends BasePlayerActivity {

	private TTFMAction action;

	private String id = "";

	private String api = "";

	private final int MSG_GET_MUSIC_TAGS = 1;

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_GET_MUSIC_TAGS:
				@SuppressWarnings("unchecked")
				List<ParterTag> musics = (List<ParterTag>) msg.obj;
				if (musics.size() > 0) {
					MyGridView gv = new MyGridView(SubCategoryActivity.this);
					gv.setNumColumns(4);
					gv.setVerticalScrollBarEnabled(false);
					gv.setAdapter(new CategoryAdapater(
							SubCategoryActivity.this,
							R.layout.ttfm_cate_button, musics));
					setCustomContentView(gv);
				}
				break;

			}
			dismissDialog(DAILOG_LOADDATE);
			return false;
		}
	});

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xhk.wifibox.activity.BasePlayerActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		action = new TTFMAction();
		showDialog(DAILOG_LOADDATE);
		id = getIntent().getStringExtra("sub_category_id");
		api = getIntent().getStringExtra("sub_category_api");
		loadDate();
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

	private void loadDate() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<ParterTag> list = action.getSubCateogryList(api,id);
				Message msg = new Message();
				msg.what = MSG_GET_MUSIC_TAGS;
				msg.obj = list;
				handler.sendMessage(msg);
			}
		}).start();
	}
}
