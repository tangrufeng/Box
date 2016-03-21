package com.xhk.wifibox.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;

public class BaseDialog extends Dialog {

	@SuppressLint("InlinedApi")
	public BaseDialog(Context context) {
		super(context);
		getContext().setTheme(android.R.style.Theme_Holo_InputMethod);
	}

}