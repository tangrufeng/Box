package com.xhk.wifibox.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eryiche.frame.util.LOG;
import com.xhk.wifibox.R;

public class AlertUtils {

	static Handler handler = new Handler();


	static ProgressDialog progressDialog;

	/**
	 * 弹出加载框
	 * 
	 * @param ctx
	 * @param msg
	 */
	public static void showLoadingDialog(final Context ctx, final String msg) {
		showLoadingDialog(ctx, msg, true);
	}

	public static void showLoadingDialog(final Context ctx, final String msg,
			final boolean cancelable) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (ctx == null) {
					return;
				}

				if (progressDialog == null) {
					progressDialog = new ProgressDialog(ctx);
					progressDialog.setIndeterminate(true);
					progressDialog.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							progressDialog = null;
						}
					});
					progressDialog.setOnDismissListener(new OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							progressDialog = null;
						}
					});

				}

				progressDialog.setCancelable(cancelable);
				progressDialog.setMessage(msg);
				if (!progressDialog.isShowing()) {
					progressDialog.show();
				}
			}
		});

	}

	public static void setLoadingDialogText(final String msg) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.setMessage(msg);
				}
			}
		});
	}

	static final String TAG = AlertUtils.class.getSimpleName();

	/**
	 * 关闭加载框
	 */
	public static void dismissLoadingDialog() {
		LOG.i(TAG, "dismissDialog");
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (progressDialog != null) {
					progressDialog.cancel();
				}
				progressDialog = null;
			}
		});
	}

	public static void alertBusinessError(Context ctx) {
		if (ctx != null) {
			Toast.makeText(ctx, "网络好像出问题了", Toast.LENGTH_LONG).show();
		}
	}

}
