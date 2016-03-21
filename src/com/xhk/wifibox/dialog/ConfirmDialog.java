/* 
 * @Title:  ConfirmDailog.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-8-15 上午10:20:48 
 * @version:  V1.0 
 */
package com.xhk.wifibox.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xhk.wifibox.R;

/**
 * @author tang
 * 
 */
public class ConfirmDialog extends Dialog {

	private boolean isShow;
	private Context context;
	private String title;
	private String message;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @param context
	 */
	public ConfirmDialog(Context context) {
		super(context);
		this.context = context;
	}

	public static class Builder {
		private Context context;
		private String title;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		// private View contentView;

		private DialogInterface.OnClickListener positiveButtonClickListener,
				negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		// public Builder setContentView(View v) {
		// this.contentView = v;
		// return this;
		// }

		public Builder setPositiveButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		
		public ConfirmDialog show() {
			ConfirmDialog dialog = create();
			dialog.show();
			return dialog;
		}

		public ConfirmDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			final ConfirmDialog dialog = new ConfirmDialog(context);
			dialog.setCanceledOnTouchOutside(false);// android
													// 4.0以上dialog点击其他地方也会消失false以后就只能点击按钮消失
			View layout = inflater.inflate(R.layout.dailog_confirm, null);
			dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(layout);/*
										 * .addContentView(layout, new
										 * LayoutParams
										 * (LayoutParams.WRAP_CONTENT,
										 * LayoutParams.WRAP_CONTENT));
										 */
			// set the dialog title
			TextView titleText = (TextView) layout
					.findViewById(R.id.tv_dialogTitle);
			titleText.setText(title);
			titleText.setTextColor(Color.rgb(2, 179, 198));

			// set the confirm button
			if (positiveButtonText != null) {

				((Button) layout.findViewById(R.id.btn_positive))
						.setText(positiveButtonText);

				if (positiveButtonClickListener != null) {

					((Button) layout.findViewById(R.id.btn_positive))
							.setOnClickListener(new View.OnClickListener() {

								public void onClick(View v) {

									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
									dialog.dismiss();
								}
							});
				}
			} else {
				layout.findViewById(R.id.btn_positive).setVisibility(View.GONE);
			}
			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.btn_negative))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {

					((Button) layout.findViewById(R.id.btn_negative))
							.setOnClickListener(new View.OnClickListener() {

								public void onClick(View v) {

									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);

									dialog.dismiss();
								}
							});
				}
			} else {
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ((Button) layout
						.findViewById(R.id.btn_positive)).getLayoutParams();

				((Button) layout.findViewById(R.id.btn_positive))
						.setLayoutParams(params);

				layout.findViewById(R.id.btn_negative).setVisibility(View.GONE);
			}

			if (message != null) {

				((TextView) layout.findViewById(R.id.tv_dialogTips))
						.setText(message);

			}
			// dialog.setContentView(layout);
			return dialog;
		}
	}
}
