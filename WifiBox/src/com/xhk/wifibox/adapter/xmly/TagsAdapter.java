/* 
 * @Title:  CategoryAdapter.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-21 下午9:34:03 
 * @version:  V1.0 
 */
package com.xhk.wifibox.adapter.xmly;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xhk.wifibox.R;
import com.xhk.wifibox.action.TTFMAction;
import com.xhk.wifibox.action.XMLYAction;
import com.xhk.wifibox.activity.AlbumListActivity;
import com.xhk.wifibox.activity.PlayListActivity;
import com.xhk.wifibox.model.ParterTag;
import com.xhk.wifibox.utils.Contants;

/**
 * @author tang
 * 
 */
public class TagsAdapter extends ArrayAdapter<ParterTag> {

	private Context context;

	private int resource;

	/**
	 * @param context
	 * @param resource
	 * @param textViewResourceId
	 * @param objects
	 */
	public TagsAdapter(Context context, int resource, List<ParterTag> objects) {
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
		ImageView iv;
		TextView tv;
		if (convertView == null) {
			convertView = View.inflate(context, resource, null);
		}
		tv = (TextView) convertView.findViewById(R.id.xmly_tag_name);
		iv = (ImageView) convertView.findViewById(R.id.xmly_tag_icon);
		final ParterTag bean = getItem(position);
		tv.setText(bean.name);
		ImageLoader.getInstance().displayImage(bean.coverlURL, iv);
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(context, AlbumListActivity.class);
				intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_CLASS, XMLYAction.class);
					intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_METHOD_NAME,
							"getCategoryTagAlbums");
				intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_NOFRESH, false);
				intent.putExtra(Contants.INTENT_EXTRA_LIST_ID, String.valueOf(bean.id)+"||"+bean.name);
				intent.putExtra(Contants.INTENT_EXTRA_LIST_NAME, bean.name);
				context.startActivity(intent);
			}
		});
		return convertView;
	}
}
