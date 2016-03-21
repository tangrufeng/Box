/* 
 * @Title:  PlayListListAdapter.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-11-9 下午2:50:10 
 * @version:  V1.0 
 */
package com.xhk.wifibox.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import com.xhk.wifibox.R;
import com.xhk.wifibox.action.PlayerAction;
import com.xhk.wifibox.activity.PlayListActivity;
import com.xhk.wifibox.utils.Contants;

/**
 * @author tang
 *
 */
public class PlayListListAdapter extends ArrayAdapter<String> {
	private final static String TAG = PlayListListAdapter.class.getSimpleName();
	private boolean isEdit = false;
	private int resource;
	private Context context;

	private List<String> checkedList=new ArrayList<String>();
	/**
	 * @param context
	 * @param resource
	 * @param objects
	 */
	public PlayListListAdapter(Context context, int resource, List<String> list) {
		super(context, resource, list);
		this.resource = resource;
		this.context = context;
		
	}

	public void setEditMode(boolean isEdit) {
		this.isEdit = isEdit;
		this.notifyDataSetChanged();
	}

	public void clearFlush(){
		checkedList.clear();
	}
	
	public List<String> getFlush(){
		return checkedList;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(context, resource, null);
		}
		final String playlistName = getItem(position);
		final CheckedTextView tvName = (CheckedTextView) convertView
				.findViewById(R.id.tv_playlist_name);
		tvName.setText(playlistName);
		if (isEdit) {
			int[] attrs = { android.R.attr.listChoiceIndicatorMultiple };
			TypedArray ta = getContext().getTheme().obtainStyledAttributes(attrs);
			Drawable indicator = ta.getDrawable(0);
			tvName.setCheckMarkDrawable(indicator);
			ta.recycle();
		} else {
			tvName.setCheckMarkDrawable(0);
		}
		if(checkedList.contains(playlistName)){
			tvName.setChecked(true);
		}else{
			tvName.setChecked(false);
		}
		convertView.setClickable(true);
		convertView.setLongClickable(true);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!isEdit) {
					Intent toLocal = new Intent(context, PlayListActivity.class);
					toLocal.putExtra(Contants.INTENT_EXTRA_LOADDATA_CLASS,
							PlayerAction.class);
					toLocal.putExtra(
							Contants.INTENT_EXTRA_LOADDATA_METHOD_NAME,
							"getMyPlayList");
					toLocal.putExtra(Contants.INTENT_EXTRA_LIST_ID,
							playlistName);
					toLocal.putExtra(Contants.INTENT_EXTRA_LOADDATA_NOFRESH,
							true);
					toLocal.putExtra(Contants.INTENT_EXTRA_LIST_NAME,
							playlistName);
					toLocal.putExtra(Contants.INTENT_EXTRA_LOCAL_PLAYLIST,
							true);
					context.startActivity(toLocal);
				} else {
					tvName.setChecked(!tvName.isChecked());
					if(tvName.isChecked()){
						checkedList.add(tvName.getText().toString());
					}else{
						checkedList.remove(tvName.getText().toString());
					}
				}
			}
		});
		return convertView;
	}

}
