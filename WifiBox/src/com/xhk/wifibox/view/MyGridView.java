/* 
 * @Title:  MyGridView.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-16 下午5:46:30 
 * @version:  V1.0 
 */
package com.xhk.wifibox.view;

import android.content.Context;
import android.widget.GridView;

/**
 * @author tang
 * 
 */
public class MyGridView extends GridView {

	
	public MyGridView(Context context) {
		super(context);
	}

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
