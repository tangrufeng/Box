/* 
 * @Title:  PlayListActivity.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-9-19 下午10:55:57 
 * @version:  V1.0 
 */
package com.xhk.wifibox.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.xhk.wifibox.action.XHKAction;
import com.xhk.wifibox.adapter.BaseAdapter;
import com.xhk.wifibox.adapter.PlayListAdapter;
import com.xhk.wifibox.box.BoxControler;
import com.xhk.wifibox.model.MediaDatabase;
import com.xhk.wifibox.track.TrackMeta;
import com.xhk.wifibox.utils.Contants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author tang
 * 
 */
public class PlayListActivity extends BaseListActivity<TrackMeta> {

	private final int MSG_BIND_HOTKEY_RST = 6;
	private String TAG = this.getClass().getSimpleName();
	private Class<XHKAction> clazz;
	private Method method;
	private Object dataSource;
	private String playListId;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_BIND_HOTKEY_RST:
				if (msg.arg1 == 1) {
					Toast.makeText(PlayListActivity.this, "绑定成功",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(PlayListActivity.this, "绑定失败",
							Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
			return false;
		}
	});

	private String[] hotKeys = null;

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
		if (getIntent().getBooleanExtra(Contants.INTENT_EXTRA_LOCAL_SONGS,
				false)) {
			hotKeys = new String[] { "热键1", "热键2", "热键3", "热键4", "热键5", "热键6",
					"重新扫描" };
		} else {
			hotKeys = new String[] { "热键1", "热键2", "热键3", "热键4", "热键5", "热键6" };
		}

		if (getIntent().getBooleanExtra(Contants.INTENT_EXTRA_LOCAL_PLAYLIST,
				false)) {
			closeMiniPlayer();
			showOperArea();
		}
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
	public BaseAdapter<TrackMeta> createAdapter() {
		return new PlayListAdapter(this, 0, getList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xhk.wifibox.activity.BaseListActivity#onDeleteConfirm(com.xhk.wifibox
	 * .model.MediaDatabase, java.lang.Object)
	 */
	@Override
	protected void onDeleteConfirm(MediaDatabase db, TrackMeta t) {
		if (getIntent().getBooleanExtra(Contants.INTENT_EXTRA_LOVE_LIST, false)) {
			db.delLove(t);
		} else {
			db.removeMediaFromPlaylist(
					getIntent().getStringExtra(Contants.INTENT_EXTRA_LIST_NAME),
					t.getPlayUrl());
		}
	}

	@Override
	public void setMoreBtn() {
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(PlayListActivity.this)
						.setIconAttribute(android.R.drawable.ic_dialog_dialer)
						.setItems(hotKeys,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											final int which) {
										if (which < 6) {
											new Thread(new Runnable() {
												@Override
												public void run() {
													boolean rst = BoxControler
															.getInstance()
															.bindHotKey(
																	which + 1,
																	getList());
													Message msg = handler
															.obtainMessage();
													msg.what = MSG_BIND_HOTKEY_RST;
													if (rst)
														msg.arg1 = 1;
													handler.sendMessage(msg);
												}
											}).start();
										} else {
											Intent i = new Intent(
													PlayListActivity.this,
													ScanActivity.class);
											startActivity(i);
										}
									}
								}).show();
			}
		};
		setMoreBtnListener(listener);
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
				List<TrackMeta> temp;
				try {
					temp = (List<TrackMeta>) method.invoke(dataSource,
							playListId, 20, currentPage());
					Message msg = getHandler().obtainMessage(MSG_DATA_GETTED, temp);
					getHandler().sendMessage(msg);
					dismissDialog(1);
				} catch (IllegalAccessException e) {
					Log.e(TAG, e.getLocalizedMessage(), e);
				} catch (IllegalArgumentException e) {
					Log.e(TAG, e.getLocalizedMessage(), e);
				} catch (InvocationTargetException e) {
					Log.e(TAG, e.getLocalizedMessage(), e);
				}
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
