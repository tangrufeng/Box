/* 
 * @Title:  XMLYMainActivity.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-21 下午9:09:32 
 * @version:  V1.0 
 */
package com.xhk.wifibox.activity.xmly;

import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.widget.ScrollView;

import com.xhk.wifibox.R;
import com.xhk.wifibox.action.XMLYAction;
import com.xhk.wifibox.activity.BasePlayerActivity;
import com.xhk.wifibox.activity.ttfm.SubCategoryActivity;
import com.xhk.wifibox.adapter.ttfm.CategoryAdapater;
import com.xhk.wifibox.adapter.xmly.CategoryAdapter;
import com.xhk.wifibox.adapter.xmly.TagsAdapter;
import com.xhk.wifibox.model.ParterTag;
import com.xhk.wifibox.view.MyGridView;

/**
 * @author tang
 *
 */
public class XMLYTagsActivity extends BasePlayerActivity {
	
	private MyGridView gv;

	private final int MSG_GET_DATA = 1;

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_GET_DATA:
				@SuppressWarnings("unchecked")
				List<ParterTag> list = (List<ParterTag>) msg.obj;
				gv.setAdapter(new TagsAdapter(XMLYTagsActivity.this, R.layout.xmly_tag_item, list));
				break;

			}
			dismissDialog(DAILOG_LOADDATE);
			return false;
		}
	});
	/* (non-Javadoc)
	 * @see com.xhk.wifibox.activity.BasePlayerActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ScrollView sv=new ScrollView(getBaseContext());
		sv.setBackgroundColor(Color.WHITE);
		gv=new MyGridView(this);
		gv.setNumColumns(2);
//		gv.setHorizontalSpacing(1);
//		gv.setVerticalSpacing(1);
		gv.setBackgroundColor(Color.TRANSPARENT);
		gv.setVerticalScrollBarEnabled(false);
		gv.setSelector(R.drawable.xmly_list_selector);
		sv.addView(gv);
		setCustomContentView(sv);
		showDialog(DAILOG_LOADDATE);
		loadData();
		
	}
	/* (non-Javadoc)
	 * @see com.xhk.wifibox.activity.BasePlayerActivity#getActivityTitle()
	 */
	@Override
	protected String getActivityTitle() {
		return getIntent().getStringExtra("category_name");
	}
	
	private void loadData(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				XMLYAction action=new XMLYAction(getBaseContext());
				List<ParterTag> list=action.getCategoryTag(getIntent().getStringExtra("category_id"));
				Message msg=new Message();
				msg.what=MSG_GET_DATA;
				msg.obj=list;
				handler.sendMessage(msg);
			}
		}).start();
	}

}
