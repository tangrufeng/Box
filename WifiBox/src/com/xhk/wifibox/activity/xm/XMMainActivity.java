/* 
 * @Title:  XMMainActivity.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-5 下午1:34:28 
 * @version:  V1.0 
 */
package com.xhk.wifibox.activity.xm;

import com.xhk.wifibox.R;
import com.xhk.wifibox.activity.BasePlayerActivity;
import com.xiami.sdk.entities.ArtistRegion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author tang
 *
 */
public class XMMainActivity extends BasePlayerActivity implements OnClickListener{

	/* (non-Javadoc)
	 * @see com.xhk.wifibox.activity.BasePlayerActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCustomContentView(R.layout.xm_activity_main);
		findViewById(R.id.ll_xm_collect).setOnClickListener(this);
		findViewById(R.id.ll_xm_list).setOnClickListener(this);
		findViewById(R.id.ll_xm_radio).setOnClickListener(this);
		findViewById(R.id.ll_xm_chinese_M).setOnClickListener(this);
		findViewById(R.id.ll_xm_chinese_F).setOnClickListener(this);
		findViewById(R.id.ll_xm_chinese_B).setOnClickListener(this);
		findViewById(R.id.ll_xm_japanese_M).setOnClickListener(this);
		findViewById(R.id.ll_xm_japanese_F).setOnClickListener(this);
		findViewById(R.id.ll_xm_japanese_M).setOnClickListener(this);
		findViewById(R.id.ll_xm_english_B).setOnClickListener(this);
		findViewById(R.id.ll_xm_english_F).setOnClickListener(this);
		findViewById(R.id.ll_xm_english_M).setOnClickListener(this);
		findViewById(R.id.ll_xm_korea_B).setOnClickListener(this);
		findViewById(R.id.ll_xm_korea_F).setOnClickListener(this);
		findViewById(R.id.ll_xm_korea_M).setOnClickListener(this);
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.ll_xm_collect:
			intent=new Intent(this,CollectListActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_xm_list:
			intent=new Intent(this,RankListActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_xm_radio:
			intent=new Intent(this,RadioListActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_xm_chinese_M:
			intent=new Intent(this,ArtistListActivity.class);
			intent.putExtra("ArtistRegion", ArtistRegion.chinese_M);
			intent.putExtra("titleName", (R.string.xm_chinese_M));
			startActivity(intent);
			break;
		case R.id.ll_xm_chinese_B:
			intent=new Intent(this,ArtistListActivity.class);
			intent.putExtra("ArtistRegion", ArtistRegion.chinese_B);
			intent.putExtra("titleName", (R.string.xm_chinese_B));
			startActivity(intent);
			break;
		case R.id.ll_xm_chinese_F:
			intent=new Intent(this,ArtistListActivity.class);
			intent.putExtra("ArtistRegion", ArtistRegion.chinese_F);
			intent.putExtra("titleName", (R.string.xm_chinese_F));
			startActivity(intent);
			break;
		case R.id.ll_xm_japanese_B:
			intent=new Intent(this,ArtistListActivity.class);
			intent.putExtra("ArtistRegion", ArtistRegion.japanese_B);
			intent.putExtra("titleName", (R.string.xm_japanese_B));
			startActivity(intent);
			break;
		case R.id.ll_xm_japanese_M:
			intent=new Intent(this,ArtistListActivity.class);
			intent.putExtra("ArtistRegion", ArtistRegion.japanese_M);
			intent.putExtra("titleName", (R.string.xm_japanese_M));
			startActivity(intent);
			break;
		case R.id.ll_xm_japanese_F:
			intent=new Intent(this,ArtistListActivity.class);
			intent.putExtra("ArtistRegion", ArtistRegion.japanese_F);
			intent.putExtra("titleName", (R.string.xm_japanese_F));
			startActivity(intent);
			break;
		case R.id.ll_xm_english_B:
			intent=new Intent(this,ArtistListActivity.class);
			intent.putExtra("ArtistRegion", ArtistRegion.english_B);
			intent.putExtra("titleName", (R.string.xm_english_B));
			startActivity(intent);
			break;
		case R.id.ll_xm_english_M:
			intent=new Intent(this,ArtistListActivity.class);
			intent.putExtra("ArtistRegion", ArtistRegion.english_M);
			intent.putExtra("titleName", (R.string.xm_english_M));
			startActivity(intent);
			break;
		case R.id.ll_xm_english_F:
			intent=new Intent(this,ArtistListActivity.class);
			intent.putExtra("ArtistRegion", ArtistRegion.english_F);
			intent.putExtra("titleName", (R.string.xm_english_F));
			startActivity(intent);
			break;
		case R.id.ll_xm_korea_B:
			intent=new Intent(this,ArtistListActivity.class);
			intent.putExtra("ArtistRegion", ArtistRegion.korea_B);
			intent.putExtra("titleName", (R.string.xm_korea_B));
			startActivity(intent);
			break;
		case R.id.ll_xm_korea_M:
			intent=new Intent(this,ArtistListActivity.class);
			intent.putExtra("ArtistRegion", ArtistRegion.korea_M);
			intent.putExtra("titleName", R.string.xm_korea_M);
			startActivity(intent);
			break;
		case R.id.ll_xm_korea_F:
			intent=new Intent(this,ArtistListActivity.class);
			intent.putExtra("ArtistRegion", ArtistRegion.korea_F);
			intent.putExtra("titleName", R.string.xm_korea_F);
			startActivity(intent);
			break;
		default:
			break;
		}
		
	}

	/* (non-Javadoc)
	 * @see com.xhk.wifibox.activity.BasePlayerActivity#getActivityTitle()
	 */
	@Override
	protected String getActivityTitle() {
		return getString(R.string.net_xiami);
	}
}
