/* 
 * @Title:  AlbumListAdapter.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-7 上午10:58:48 
 * @version:  V1.0 
 */
package com.xhk.wifibox.adapter.xm;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xhk.wifibox.R;
import com.xhk.wifibox.action.XMAction;
import com.xhk.wifibox.activity.PlayListActivity;
import com.xhk.wifibox.adapter.BaseAdapter;
import com.xhk.wifibox.utils.Contants;
import com.xiami.sdk.entities.OnlineRadio;
import com.xiami.sdk.entities.OnlineSong;
import com.xiami.sdk.entities.RankListItem;

/**
 * @author tang
 * 
 */
public class RankListAdapter extends BaseAdapter<RankListItem> {

	private Context context;

	private ViewHolder holder;

	private int resource;
	

	/**
	 * @param context
	 * @param resource
	 * @param objects
	 */
	public RankListAdapter(Context context, int resource,
			List<RankListItem> objects) {
		super(context, resource, objects);
		this.context = context;
		this.resource = resource;

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
			holder = new ViewHolder();
			convertView = View.inflate(context, resource, null);
			holder.ivLogo = (ImageView) convertView
					.findViewById(R.id.iv_list_logo);
			holder.tvSong1 = (TextView) convertView
					.findViewById(R.id.tv_list_song1);
			holder.tvSong2 = (TextView) convertView
					.findViewById(R.id.tv_list_song2);
			holder.tvSong3 = (TextView) convertView
					.findViewById(R.id.tv_list_song3);
			holder.tvSong4 = (TextView) convertView
					.findViewById(R.id.tv_list_song4);
			holder.tvTitle = (TextView) convertView
					.findViewById(R.id.tv_list_title);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final RankListItem bean = getItem(position);
		
		List<OnlineSong> songs = bean.getSongs();
		ImageLoader.getInstance().displayImage(bean.getLogoMiddle(), holder.ivLogo);
		holder.tvTitle.setText(bean.getTitle());
		
		if (songs != null) {
			try {
				holder.tvSong1.setText("1."+songs.get(0).getSongName());
				holder.tvSong2.setText("2."+songs.get(1).getSongName());
				holder.tvSong3.setText("3."+songs.get(2).getSongName());
				holder.tvSong4.setText("4."+songs.get(3).getSongName());
			} catch (NullPointerException e) {
			} catch (IndexOutOfBoundsException e) {
			}
		}

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent =new Intent(context,PlayListActivity.class);
				intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_CLASS, XMAction.class);
				intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_METHOD_NAME, "getRankSongsSync");
				intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_NOFRESH, true);
				intent.putExtra(Contants.INTENT_EXTRA_LIST_NAME, bean.getTitle());
				intent.putExtra(Contants.INTENT_EXTRA_LIST_ID, String.valueOf(bean.getType()));
				context.startActivity(intent);
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView tvTitle, tvSong1, tvSong2, tvSong3, tvSong4;
		ImageView ivLogo;
	}

	/* (non-Javadoc)
	 * @see com.xhk.wifibox.adapter.BaseAdapter#setEditMode(boolean)
	 */
	@Override
	public void setEditMode(boolean isEdit) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.xhk.wifibox.adapter.BaseAdapter#clearFlush()
	 */
	@Override
	public void clearFlush() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.xhk.wifibox.adapter.BaseAdapter#getFlush()
	 */
	@Override
	public List<RankListItem> getFlush() {
		// TODO Auto-generated method stub
		return null;
	}

}
