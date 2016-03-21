/* 
 * @Title:  TTFMMainActivity.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-14 上午10:56:40 
 * @version:  V1.0 
 */
package com.xhk.wifibox.activity.ttfm;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.Log;
import android.util.Pair;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.xhk.wifibox.R;
import com.xhk.wifibox.action.TTFMAction;
import com.xhk.wifibox.activity.BasePlayerActivity;
import com.xhk.wifibox.adapter.ttfm.CategoryAdapater;
import com.xhk.wifibox.model.ParterTag;
import com.xhk.wifibox.view.MyGridView;

/**
 * @author tang
 * 
 */
public class TTFMMainActivity extends BasePlayerActivity {

	// private AsymmetricGridView agvMusic;

	private TTFMAction action;

	private LinearLayout gvMusic, gvTing;

	private final int MSG_GET_MUSIC_TAGS = 1;
	private final int MSG_GET_TING_TAGS = 2;
	private int threadFinished = 0;
	private int threadsCnt = 2;

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_GET_MUSIC_TAGS:
				@SuppressWarnings("unchecked")
				List<ParterTag> musics = (List<ParterTag>) msg.obj;
				if (musics.size() > 0) {
					MyGridView gv = new MyGridView(TTFMMainActivity.this);
					gv.setNumColumns(4);
					gv.setVerticalScrollBarEnabled(false);
					gv.setAdapter(new CategoryAdapater(TTFMMainActivity.this,
							R.layout.ttfm_cate_button, musics));
					gvMusic.addView(gv, new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT));
				}
				threadFinished++;
				break;
			case MSG_GET_TING_TAGS:
				@SuppressWarnings("unchecked")
				List<ParterTag> tings = (List<ParterTag>) msg.obj;
				if (tings.size() > 0) {
					MyGridView gv = new MyGridView(TTFMMainActivity.this);
					gv.setNumColumns(4);
					gv.setHorizontalSpacing(2);
					gv.setVerticalSpacing(2);
					gv.setVerticalScrollBarEnabled(false);
					gv.setAdapter(new CategoryAdapater(TTFMMainActivity.this,
							R.layout.ttfm_cate_button, tings));
					gvTing.addView(gv, new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT));
				}
				threadFinished++;
				break;

			default:
				break;
			}
			if (threadFinished == threadsCnt) {
				dismissDialog(DAILOG_LOADDATE);
			}
			return false;
		}

	});

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		action = new TTFMAction();
		setCustomContentView(R.layout.ttfm_main_activity);
		gvMusic = (LinearLayout) findViewById(R.id.ttfm_gvMusic);
		gvTing = (LinearLayout) findViewById(R.id.ttfm_gvFM);
		showDialog(DAILOG_LOADDATE);
		loadDate();
		//
	}

	private void loadDate() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<ParterTag> list = action.getMusicCategoryList();
				Message msg = new Message();
				msg.what = MSG_GET_MUSIC_TAGS;
				msg.obj = list;
				handler.sendMessage(msg);
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<ParterTag> list = action.getTingCategoryList();
				Message msg = new Message();
				msg.what = MSG_GET_TING_TAGS;
				msg.obj = list;
				handler.sendMessage(msg);
			}
		}).start();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xhk.wifibox.activity.BasePlayerActivity#getActivityTitle()
	 */
	@Override
	protected String getActivityTitle() {

		return getString(R.string.net_tingting);
	}

}
