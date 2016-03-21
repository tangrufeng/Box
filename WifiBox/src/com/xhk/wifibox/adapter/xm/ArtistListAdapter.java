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
import android.sax.StartElementListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xhk.wifibox.R;
import com.xhk.wifibox.action.XMAction;
import com.xhk.wifibox.activity.PlayListActivity;
import com.xhk.wifibox.adapter.BaseAdapter;
import com.xhk.wifibox.utils.Contants;
import com.xiami.sdk.entities.OnlineArtist;

/**
 * @author tang
 * 
 */
public class ArtistListAdapter extends BaseAdapter<OnlineArtist> {

	private Context context;

	private ViewHolder holder;

	private int resource;

	/**
	 * @param context
	 * @param resource
	 * @param objects
	 */
	public ArtistListAdapter(Context context, int resource,
			List<OnlineArtist> objects) {
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
					.findViewById(R.id.iv_radio_logo);
			holder.tvDesc = (TextView) convertView
					.findViewById(R.id.tv_radio_desc);
			holder.tvTitle = (TextView) convertView
					.findViewById(R.id.tv_radio_name);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final OnlineArtist bean = getItem(position);
		ImageLoader.getInstance().displayImage(bean.getImageUrl(),
				holder.ivLogo);
		String name=bean.getName();
		if(!TextUtils.isEmpty(bean.getEnglish_name())){
			name=name+"("+bean.getEnglish_name()+")";
		}
		holder.tvTitle.setText(name);

		holder.tvDesc.setText(bean.getCountLikes()+"位粉丝");
		
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent =new Intent(context,PlayListActivity.class);
				intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_CLASS, XMAction.class);
				intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_METHOD_NAME, "getArtistSongs");
				intent.putExtra(Contants.INTENT_EXTRA_LIST_ID, String.valueOf(bean.getId()));
				intent.putExtra(Contants.INTENT_EXTRA_LIST_NAME, bean.getName());
				
				context.startActivity(intent);
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView tvTitle, tvDesc;
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
	public List<OnlineArtist> getFlush() {
		// TODO Auto-generated method stub
		return null;
	}

}
