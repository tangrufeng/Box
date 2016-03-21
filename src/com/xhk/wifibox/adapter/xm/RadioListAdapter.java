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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xhk.wifibox.R;
import com.xhk.wifibox.adapter.BaseAdapter;
import com.xiami.sdk.entities.OnlineRadio;

/**
 * @author tang
 * 
 */
public class RadioListAdapter extends BaseAdapter<OnlineRadio> {

	private Context context;

	private ViewHolder holder;

	private int resource;

	/**
	 * @param context
	 * @param resource
	 * @param objects
	 */
	public RadioListAdapter(Context context, int resource,
			List<OnlineRadio> objects) {
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

		OnlineRadio bean = getItem(position);
		Log.d("AlbumListAdapter", bean.toString());
		ImageLoader.getInstance().displayImage(bean.getRadioLogo(),
				holder.ivLogo);
		holder.tvTitle.setText(bean.getRadioName());

		holder.tvDesc.setText(context.getString(
				R.string.xm_raido_list_item_tip, bean.getPlayCount()));
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
	public List<OnlineRadio> getFlush() {
		// TODO Auto-generated method stub
		return null;
	}

}
