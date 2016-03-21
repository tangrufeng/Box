/* 
 * @Title:  AlbumListActivity.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-5 下午9:36:13 
 * @version:  V1.0 
 */
package com.xhk.wifibox.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xhk.wifibox.R;
import com.xhk.wifibox.adapter.BaseAdapter;
import com.xhk.wifibox.model.MediaDatabase;
import com.xhk.wifibox.track.TrackMeta;
import com.xhk.wifibox.utils.Contants;
import com.xhk.wifibox.utils.Util;

/**
 * @author tang
 * @param <T>
 * 
 */
public abstract class BaseListActivity<T> extends BasePlayerActivity implements
		OnRefreshListener2<ListView>, OnClickListener {

	private View llOperArea, llEditArea, tvEdit, tvDel, tvQuite;
	private int currentPage = 1;

	private BaseAdapter<T> adapter;

	private PullToRefreshListView refreshList;

	protected final int MSG_DATA_GETTED = 1;

	private List<T> list = new ArrayList<T>();

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_DATA_GETTED:
				Log.d("BaseListActivity", "====="
						+ "========MSG_DATA_GETTED==========");
				adapter.notifyDataSetChanged();
				refreshList.onRefreshComplete();
				setMoreBtn();
				break;

			default:
				break;
			}
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
		setCustomContentView(R.layout.album_list);

		refreshList = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		Util.setPullToRefreshListViewLabel(refreshList);
		refreshList.setOnRefreshListener(this);
		setRefreshMode(Mode.BOTH);
		adapter = createAdapter();
		refreshList.setAdapter(adapter);
		intOperArea();
	}

	public void setRefreshMode(Mode mode) {
		refreshList.setMode(mode);
	}

	public final Handler getHandler() {
		return handler;
	}

	public final int currentPage() {
		return currentPage;
	}

	public final List<T> getList() {
		return list;
	}

	public void intOperArea() {
		llOperArea = findViewById(R.id.ll_operArea);
		llEditArea = findViewById(R.id.ll_editArea);
		tvEdit = findViewById(R.id.tv_edit);
		tvDel = findViewById(R.id.tv_del);
		// tvAll = findViewById(R.id.tv_all);
		tvQuite = findViewById(R.id.tv_quite);
		tvEdit.setOnClickListener(this);
		tvDel.setOnClickListener(this);
		// tvAll.setOnClickListener(this);
		tvQuite.setOnClickListener(this);
	}

	protected void showOperArea() {
		if (llOperArea != null) {
			llOperArea.setVisibility(View.VISIBLE);
			tvEdit.setVisibility(View.VISIBLE);
			llEditArea.setVisibility(View.GONE);
		}
	}

	private void hidenOperArea() {
		if (llOperArea != null) {
			llOperArea.setVisibility(View.GONE);
			tvEdit.setVisibility(View.VISIBLE);
			llEditArea.setVisibility(View.GONE);
		}
	}

	private void showEditArea() {
		tvEdit.setVisibility(View.GONE);
		llEditArea.setVisibility(View.VISIBLE);
	}

	private void hidenEditArea() {
		tvEdit.setVisibility(View.VISIBLE);
		llEditArea.setVisibility(View.GONE);
	}

	public abstract BaseAdapter<T> createAdapter();

	public abstract void loadDataList();

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
			dialog.setCancelable(false);
			return dialog;
		}
		}
		return null;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		currentPage = 1;
		list.clear();
		loadDataList();

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		currentPage++;
		loadDataList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xhk.wifibox.activity.BasePlayerActivity#getActivityTitle()
	 */
	@Override
	protected String getActivityTitle() {
		// TODO Auto-generated method stub
		return getIntent().getStringExtra(Contants.INTENT_EXTRA_LIST_NAME);
	}

	/**
	 * 处理”更多“按钮的操作
	 */
	public void setMoreBtn() {

	}

	private void del() {
		if (adapter.getFlush().isEmpty()) {
			Toast.makeText(this, "请选择你要移除选定歌曲！", Toast.LENGTH_SHORT).show();
		} else {
			new AlertDialog.Builder(this)
					.setMessage("确定要移除选定歌曲吗?")
					.setTitle("操作确认")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									List<T> list = adapter.getFlush();
									MediaDatabase mdb = MediaDatabase
											.getInstance(BaseListActivity.this);
									Log.d(TAG, "lll===>" + list.toString());
									for (T t : list) {
										Log.d(TAG, "name===>" + t);
										if (t instanceof TrackMeta) {
											onDeleteConfirm(mdb, t);
											adapter.remove(t);
										}
									}
									Toast.makeText(BaseListActivity.this,
											"操作成功", Toast.LENGTH_SHORT).show();
									adapter.clearFlush();
									adapter.setEditMode(false);
									hidenEditArea();
								}
							}).setNegativeButton("取消", null).show();
		}

	}

	protected void onDeleteConfirm(MediaDatabase db, T t) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_operArea:
			break;
		case R.id.ll_editArea:
			break;
		case R.id.tv_edit:
			showEditArea();
			adapter.setEditMode(true);
			break;
		case R.id.tv_del:
			del();
			break;
		case R.id.tv_all:
			break;
		case R.id.tv_quite:
			hidenEditArea();
			adapter.clearFlush();
			adapter.setEditMode(false);
			break;
		}

	}

}
