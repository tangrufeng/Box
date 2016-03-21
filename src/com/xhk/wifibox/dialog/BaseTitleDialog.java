package com.xhk.wifibox.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xhk.wifibox.R;

public class BaseTitleDialog extends BaseDialog {

	private LinearLayout frContent;

	private TextView tvTitle;

	public BaseTitleDialog(Context context) {
		super(context);

		super.setContentView(R.layout.dialog_base);

		Window window = getWindow();
		WindowManager.LayoutParams attributesParams = window.getAttributes();
		attributesParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		attributesParams.dimAmount = 0.4f;

		@SuppressWarnings("deprecation")
		int sreemWidth = window.getWindowManager().getDefaultDisplay().getWidth();
		int windowWidth = (int) (sreemWidth * 0.8);

		window.setLayout(windowWidth, LayoutParams.WRAP_CONTENT);

		frContent = (LinearLayout) findViewById(R.id.fr_content);
		tvTitle = (TextView) findViewById(R.id.tv_title);
	}

	@Override
	public void setContentView(View view) {
		frContent.removeAllViews();
		frContent.addView(view, new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1));
	}
	
	@Override
	public void setContentView(View view, ViewGroup.LayoutParams lp) {
		frContent.removeAllViews();
		frContent.addView(view, lp);
	}

	@Override
	public void setContentView(int layoutResID) {
		View view = View.inflate(getContext(), layoutResID, null);
		setContentView(view);
	}

	/**
	 * 设置对话框的标题
	 * @param title
	 */
	public void setTitle(String title) {
		tvTitle.setText(title);
	}

}