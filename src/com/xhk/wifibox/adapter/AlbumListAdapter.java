/* 
 * @Title:  AlbumListAdapter.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-7 上午10:58:48 
 * @version:  V1.0 
 */
package com.xhk.wifibox.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xhk.wifibox.R;
import com.xhk.wifibox.activity.PlayListActivity;
import com.xhk.wifibox.track.Album;
import com.xhk.wifibox.utils.Contants;

/**
 * @author tang
 * 
 */
public class AlbumListAdapter extends BaseAdapter<Album> {

	private Context context;

	
	private ViewHolder holder;

	private int resource;

	private Class actionClass;
	/**
	 * @param context
	 * @param resource
	 * @param objects
	 */
	public AlbumListAdapter(Context context, int resource, List<Album> objects,Class actionClass) {
		super(context, resource, objects);
		this.context = context;
		this.resource = resource;
		this.actionClass=actionClass;
	}

	
	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			holder = new ViewHolder();
			convertView=View.inflate(context, resource, null);
			holder.ivLogo=(ImageView)convertView.findViewById(R.id.iv_album_logo);
			holder.tvAuthor=(TextView)convertView.findViewById(R.id.tv_album_author);
			holder.tvDesc=(TextView)convertView.findViewById(R.id.tv_album_desc);
			holder.tvTag=(TextView)convertView.findViewById(R.id.tv_album_tags);
			holder.tvTitle=(TextView)convertView.findViewById(R.id.tv_album_title);
			
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		
		final Album bean=getItem(position);
		Log.d("AlbumListAdapter", bean.toString());
		ImageLoader.getInstance().displayImage(bean.getLogoUrl(), holder.ivLogo);
		holder.tvAuthor.setText(bean.getAuthor());
		holder.tvDesc.setText(bean.getDesc());
		holder.tvTag.setText(bean.getTag());
		holder.tvTitle.setText(bean.getTitle());
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent =new Intent(context,PlayListActivity.class);
				intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_CLASS,actionClass);
				intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_METHOD_NAME, "getCollectSongs");
				intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_NOFRESH, true);
				intent.putExtra(Contants.INTENT_EXTRA_LIST_ID, String.valueOf(bean.getId()));
				intent.putExtra(Contants.INTENT_EXTRA_LIST_TAG, bean.getTag());
				intent.putExtra(Contants.INTENT_EXTRA_LIST_NAME, bean.getTitle());
				context.startActivity(intent);
			}
		});
		return convertView;
	}


	class ViewHolder {
		TextView tvTitle, tvDesc, tvTag, tvAuthor;
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
	public List<Album> getFlush() {
		// TODO Auto-generated method stub
		return null;
	}

}
