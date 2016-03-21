/* 
 * @Title:  SearchActivity.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-4 上午11:58:03 
 * @version:  V1.0 
 */
package com.xhk.wifibox.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.xhk.wifibox.R;
import com.xhk.wifibox.action.XHKActionHelper;
import com.xhk.wifibox.adapter.PlayListAdapter;
import com.xhk.wifibox.track.TrackMeta;
import com.xhk.wifibox.utils.Util;

/**
 * @author tang
 * 
 */
public class SearchActivity extends BasePlayerActivity implements
		OnRefreshListener2<ListView> {

	private final static String TAG = SearchActivity.class.getSimpleName();;
	private EditText etKey;
	private PullToRefreshListView refreshList;
	private ArrayAdapter<TrackMeta> adapter;
	private int currentPage = 1;
	private String key="";

	protected final int MSG_DATA_GETTED = 1;
	private List<TrackMeta> list = new ArrayList<TrackMeta>();
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_DATA_GETTED:
				Log.d("BaseListActivity", "====="
						+ "========MSG_DATA_GETTED==========");
				adapter.notifyDataSetChanged();
				refreshList.onRefreshComplete();
				break;

			default:
				break;
			}
			return false;
		}

	});

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCustomContentView(R.layout.activity_search);
		initView();
	}

	private void initView() {
		etKey = (EditText) findViewById(R.id.et_key);
		etKey.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode==KeyEvent.KEYCODE_SEARCH || keyCode==KeyEvent.KEYCODE_ENTER){
					if(!key.equals(etKey.getText().toString())){
						key=etKey.getText().toString();
						list.clear();
						loadDate();

						//关掉键盘
				    	InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						if (getCurrentFocus() != null) {
							im.hideSoftInputFromWindow(getCurrentFocus()
									.getApplicationWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
						}
					}
				}
				return false;
			}
		});
		
		etKey.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Log.d(TAG, "onTextChanged========="+s);
				key=String.valueOf(s);
				list.clear();
				loadDate();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				Log.d(TAG, "afterTextChanged========="+s);
			}
		});
		refreshList = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		Util.setPullToRefreshListViewLabel(refreshList);
		refreshList.setOnRefreshListener(this);
		refreshList.setMode(Mode.PULL_FROM_END);
		adapter = new PlayListAdapter(this, R.layout.playlist_item, list);
		refreshList.setAdapter(adapter);
	}

	public void loadDate(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if (TextUtils.isEmpty(key)) {
					list.clear();
				} else {
					list.addAll(XHKActionHelper.getIntance(SearchActivity.this)
							.searchSong(key, 10, currentPage));
				}
				handler.sendEmptyMessage(MSG_DATA_GETTED);
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
		return getString(R.string.search);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2
	 * #onPullDownToRefresh
	 * (com.handmark.pulltorefresh.library.PullToRefreshBase)
	 */
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2
	 * #onPullUpToRefresh(com.handmark.pulltorefresh.library.PullToRefreshBase)
	 */
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		currentPage++;
		loadDate();

	}

}
