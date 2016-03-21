/* 
 * @Title:  PlayListListActivity.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-11-9 下午6:20:16 
 * @version:  V1.0 
 */
package com.xhk.wifibox.activity;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.xhk.wifibox.R;
import com.xhk.wifibox.action.PlayerAction;
import com.xhk.wifibox.adapter.PlayListListAdapter;
import com.xhk.wifibox.box.BoxControler;
import com.xhk.wifibox.model.MediaDatabase;
import com.xhk.wifibox.utils.Contants;

/**
 * @author tang
 * 
 */
public class PlayListListActivity extends BasePlayerActivity implements
		OnClickListener, OnItemClickListener, OnItemLongClickListener {

	private View llAddPlayList, llOperArea, llEditArea, tvEdit, tvDel, tvQuite;
	private ListView lvPlaylistList;
	private int cnt;
	private PlayListListAdapter adapter = null;
	private MediaDatabase mdb = null;
	private List<String> playlist = null;
	private static final int DIALOG_TYPE_ADDPLAYLIST = 100;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xhk.wifibox.activity.BasePlayerActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCustomContentView(R.layout.activity_playlist);
		mdb = MediaDatabase.getInstance(this);

		llAddPlayList = findViewById(R.id.ll_add_playlist);
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
		llAddPlayList.setOnClickListener(this);

		lvPlaylistList = (ListView) findViewById(R.id.lv_playlist);
		playlist = mdb.getPlaylists();
		adapter = new PlayListListAdapter(this, R.layout.playlistlist_item,
				playlist);
		lvPlaylistList.setAdapter(adapter);
		lvPlaylistList.setOnItemClickListener(this);
		lvPlaylistList.setOnItemLongClickListener(this);
		closeMiniPlayer();
		showOperArea();
	}

	@Override
	protected String getActivityTitle() {

		return getString(R.string.my_playLIst);
	}

	private class MoreBtnListener implements OnClickListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			showOperArea();
		}

	}

	private void showOperArea() {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xhk.wifibox.activity.BasePlayerActivity#onCreateDialog(int)
	 */
	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_TYPE_ADDPLAYLIST) {

			final EditText inputServer = new EditText(this);
			inputServer.setHint("请输入歌单名称");
			inputServer.setSingleLine();
			return new AlertDialog.Builder(this)
					.setTitle("创建新的歌单")
					.setView(inputServer)
					.setNegativeButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									MediaDatabase mdb = MediaDatabase
											.getInstance(PlayListListActivity.this);
									String name = String.valueOf(inputServer
											.getText());
									if (!TextUtils.isEmpty(name)) {
										if (mdb.addPlaylist(name) < 0) {
											Toast.makeText(PlayListListActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
										} else {
											adapter.add(inputServer.getText()
													.toString());
											Toast.makeText(PlayListListActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
										}
									}
								}
							}).setPositiveButton("取消", null).create();
		}
		return super.onCreateDialog(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_add_playlist:
			showDialog(DIALOG_TYPE_ADDPLAYLIST);
			break;
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

	private void del() {
		if (adapter.getFlush().isEmpty()) {
			Toast.makeText(this, "请选择你要删除的歌单", Toast.LENGTH_SHORT).show();
		} else {
			new AlertDialog.Builder(this)
					.setMessage("确定要删除选中歌单吗?")
					.setTitle("操作确认")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									List<String> list = adapter.getFlush();
									MediaDatabase mdb = MediaDatabase
											.getInstance(PlayListListActivity.this);
									Log.d(TAG,"lll===>"+list.toString());
									for (String name : list) {
										Log.d(TAG,"name===>"+name);
										mdb.deletePlaylist(name);
										adapter.remove(name);
									}
									Toast.makeText(PlayListListActivity.this,
											"操作成功", Toast.LENGTH_SHORT).show();
									adapter.clearFlush();
									adapter.setEditMode(false);
									hidenEditArea();
								}
							}).setNegativeButton("取消", null).show();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String listName = (String) parent.getItemAtPosition(position);

		Intent toLocal = new Intent(this, PlayListActivity.class);
		toLocal.putExtra(Contants.INTENT_EXTRA_LOADDATA_CLASS,
				PlayerAction.class);
		toLocal.putExtra(Contants.INTENT_EXTRA_LOADDATA_METHOD_NAME,
				"getMyPlayList");
		toLocal.putExtra(Contants.INTENT_EXTRA_LIST_ID, listName);
		toLocal.putExtra(Contants.INTENT_EXTRA_LOADDATA_NOFRESH, true);
		toLocal.putExtra(Contants.INTENT_EXTRA_LIST_NAME, listName);
		startActivity(toLocal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android
	 * .widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		final String listName = (String) parent.getItemAtPosition(position);
		new AlertDialog.Builder(this).setTitle("操作提醒")
				.setMessage("确定要删除歌单\"" + listName + "\"")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mdb.deletePlaylist(listName);
						adapter.remove(listName);
						Toast.makeText(PlayListListActivity.this, "操作成功",
								Toast.LENGTH_SHORT).show();
					}
				}).setNegativeButton("取消", null).show();
		return false;
	}

	/**
	 * 
	 */
	private void flushData() {
		playlist = mdb.getPlaylists();
		Log.d(TAG, "playlist lEN2==>" + playlist.size());
		adapter.notifyDataSetChanged();
	}
}
