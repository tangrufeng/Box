/* 
 * @Title:  CategoryAdapater.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-15 下午2:39:56 
 * @version:  V1.0 
 */
package com.xhk.wifibox.adapter.ttfm;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.xhk.wifibox.R;
import com.xhk.wifibox.action.TTFMAction;
import com.xhk.wifibox.activity.AlbumListActivity;
import com.xhk.wifibox.activity.PlayListActivity;
import com.xhk.wifibox.activity.ttfm.SubCategoryActivity;
import com.xhk.wifibox.model.ParterTag;
import com.xhk.wifibox.partner.PartnerUtils;
import com.xhk.wifibox.utils.Contants;

/**
 * @author tang
 * 
 */
public class CategoryAdapater extends ArrayAdapter<ParterTag> {

	private Context context;

	private int resource;

	/**
	 * @param context
	 * @param resource
	 * @param objects
	 */
	public CategoryAdapater(Context context, int resource, List<ParterTag> objects) {
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
		Button btn;
		if (convertView == null) {
			convertView = View.inflate(context, resource, null);
		}
		btn = (Button) convertView.findViewById(R.id.btn_cate);
		final ParterTag bean = getItem(position);
		btn.setText(bean.name);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBtnClick(bean);
			}
		});
		return convertView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	private void onBtnClick(ParterTag bean) {
		switch (bean.type) {
		case Contants.TTFM_TAG_TYPE_MUSIC:
			toList(bean, "getMusicList");
			break;
		case Contants.TTFM_TAG_TYPE_NETLIVE:
			toList(bean, "getLiveNetWorkList");
			break;
		case Contants.TTFM_TAG_TYPE_CATEGORY:
			if (PartnerUtils.TTFM_TING_SUBCATEGORY.equals(bean.nextApi)) {
				toSub(bean);
			} else {
				toAlbum(bean);
			}
			break;
		case Contants.TTFM_TAG_TYPE_LIVE:
			if (PartnerUtils.TTFM_TING_AREACATEGORY.equals(bean.nextApi)) {
				toSub(bean);
			}else if(PartnerUtils.TTFM_TING_LIVENETWORK.equals(bean.nextApi)){
				toList(bean,"getLiveNetWorkList");
			}else {
				toList(bean, "getLiveAreaList");
			}
			break;
		case Contants.TTFM_TAG_TYPE_PODCAST:
			if (PartnerUtils.TTFM_TING_PODCASTCATEGORY.equals(bean.nextApi)) {
				toSub(bean);
			} else {
				toAlbum(bean);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * @param bean
	 */
	private void toSub(ParterTag bean) {
		Intent intent2 = new Intent(context, SubCategoryActivity.class);
		intent2.putExtra("sub_category_api", bean.nextApi);
		intent2.putExtra("sub_category_id", bean.id);
		intent2.putExtra(Contants.INTENT_EXTRA_LIST_NAME, bean.name);
		context.startActivity(intent2);
	}

	private void toAlbum(ParterTag bean) {
		Intent intent = new Intent(context, AlbumListActivity.class);
		intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_CLASS, TTFMAction.class);
		if (bean.type == Contants.TTFM_TAG_TYPE_CATEGORY) {
			intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_METHOD_NAME,
					"getCategoryAlbumList");
		} else {
			intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_METHOD_NAME,
					"getPodcastAlbumList");
		}
		intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_NOFRESH, true);
		intent.putExtra(Contants.INTENT_EXTRA_LIST_ID, String.valueOf(bean.id));
		intent.putExtra(Contants.INTENT_EXTRA_LIST_NAME, bean.name);
		context.startActivity(intent);

	}

	/**
	 * @param bean
	 */
	private void toList(ParterTag bean, String loadDataMethod) {
		Intent intent = new Intent(context, PlayListActivity.class);
		intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_CLASS, TTFMAction.class);

		intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_METHOD_NAME,
				loadDataMethod);

		intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_NOFRESH, true);
		intent.putExtra(Contants.INTENT_EXTRA_LIST_ID, String.valueOf(bean.id));
		intent.putExtra(Contants.INTENT_EXTRA_LIST_NAME, bean.name);
		context.startActivity(intent);
	}

}
