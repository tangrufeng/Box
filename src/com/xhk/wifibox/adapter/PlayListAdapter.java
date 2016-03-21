/* 
 * @Title:  PlayListAdapter.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-9-19 下午11:56:15 
 * @version:  V1.0 
 */
package com.xhk.wifibox.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xhk.wifibox.R;
import com.xhk.wifibox.activity.PlayerActivity;
import com.xhk.wifibox.box.BoxControler;
import com.xhk.wifibox.track.TrackMeta;
import com.xhk.wifibox.view.CircleImageView;

/**
 * @author tang
 * 
 */
public class PlayListAdapter extends BaseAdapter<TrackMeta> {
	private final static String TAG = PlayListAdapter.class.getSimpleName();
	private Context context;
	private ViewHolder holder = null;

	private static final int ITEM_HEAD = 0;

	private static final int ITEM_SONG = 1;

	private boolean isEdit=false;
	private List<TrackMeta> playList = null;

	private List<TrackMeta> flush=new ArrayList<TrackMeta>();
	/**
	 * @param context
	 * @param resource
	 * @param objects
	 */
	public PlayListAdapter(Context context, int resource,
			List<TrackMeta> objects) {
		super(context, resource, objects);
		this.context = context;
		this.playList = objects;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseAdapter#getViewTypeCount()
	 */
	// @Override
	// public int getViewTypeCount() {
	// return 2;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseAdapter#getItemViewType(int)
	 */
	// @Override
	// public int getItemViewType(int position) {
	// if (position == 0) {
	// return ITEM_HEAD;
	// } else {
	// return ITEM_SONG;
	// }
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// int viewType = getItemViewType(position);
		if (convertView == null) {
			holder = new ViewHolder();
			// switch (viewType) {
			// case ITEM_SONG:
			convertView = View.inflate(context, R.layout.playlist_item, null);
			holder.civSongLogo = (CircleImageView) convertView
					.findViewById(R.id.ci_song_logo);
			holder.tvArtist = (TextView) convertView
					.findViewById(R.id.tv_artist);
			holder.tvSongName = (TextView) convertView
					.findViewById(R.id.tv_songName);
			holder.cbCheck=(CheckBox)convertView.findViewById(R.id.cb_check);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// if (ITEM_SONG == viewType) {
		final TrackMeta tm = getItem(position);
		holder.tvSongName.setText(tm.getName());
		if (tm.getArtist() == null || "null".equals(tm.getArtist())) {
			holder.tvArtist.setText("");
		} else {
			holder.tvArtist.setText(tm.getArtist());

		}
		if(isEdit){
			holder.cbCheck.setVisibility(View.VISIBLE);
		}else{
			holder.cbCheck.setVisibility(View.GONE);
		}

		if(flush.contains(tm)){
			holder.cbCheck.setChecked(true);
		}else{
			holder.cbCheck.setChecked(false);
		}
		ImageLoader.getInstance().displayImage(tm.getCoverUrl(),
				holder.civSongLogo);
		
		holder.cbCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d(TAG,"===holder.cbCheck==="+isChecked);
				if(isChecked){
					flush.add(tm);
				}else{
					flush.remove(tm);
				}
			}
		});
		
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isEdit){
					BoxControler.getInstance().setContext(context);
					BoxControler.getInstance().setPlayList(playList, tm);
					Intent i = new Intent(context, PlayerActivity.class);
					context.startActivity(i);
				}else{
					holder.cbCheck.setChecked(!holder.cbCheck.isChecked());
				}
			}
		});
		// holder.ibMoreOpts.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// }
		// });
		// } else {
		// holder.ibMoreOpts.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// }
		// });
		// }
		return convertView;
	}

	class ViewHolder {
		CircleImageView civSongLogo;
		TextView tvSongName, tvArtist;
		CheckBox cbCheck;
	}

	/* (non-Javadoc)
	 * @see com.xhk.wifibox.adapter.BaseAdapter#setEditMode(boolean)
	 */
	@Override
	public void setEditMode(boolean isEdit) {
		this.isEdit=isEdit;
		this.notifyDataSetChanged();
	}

	/* (non-Javadoc)
	 * @see com.xhk.wifibox.adapter.BaseAdapter#clearFlush()
	 */
	@Override
	public void clearFlush() {
		this.flush.clear();
	}

	/* (non-Javadoc)
	 * @see com.xhk.wifibox.adapter.BaseAdapter#getFlush()
	 */
	@Override
	public List<TrackMeta> getFlush() {
		return this.flush;
	}

}
