/* 
 * @Title:  ReadyActivity.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-20 下午5:28:30 
 * @version:  V1.0 
 */
package com.xhk.wifibox.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.eryiche.frame.util.PreferenceUtils;
import com.xhk.wifibox.R;
import com.xhk.wifibox.XHKApplication;
import com.xhk.wifibox.box.Box;
import com.xhk.wifibox.box.BoxCache;
import com.xhk.wifibox.box.BoxControler;
import com.xhk.wifibox.dialog.UpdatingDialog;
import com.xhk.wifibox.dialog.UpdatingDialog.OnCancelUpdateClickListener;
import com.xhk.wifibox.dialog.UpdatingDialog.OnUpdateCompleteListener;
import com.xhk.wifibox.dialog.UpdatingDialog.OnUpdateErrorListener;
import com.xhk.wifibox.model.UpdateInfo;
import com.xhk.wifibox.utils.Contants;
import com.xhk.wifibox.utils.Device;
import com.xhk.wifibox.utils.JSONUtil;
import com.xhk.wifibox.utils.NetUtils;
import com.xhk.wifibox.utils.UDPHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

/**
 * @author tang
 * 
 */
public class ReadyActivity extends BaseActivity {

	private final String TAG = this.getClass().getSimpleName();
	private final int MSG_HAVE_NEWVERSION = 1;
	private final int MSG_TAGRET_BOX = 3;
	private UDPHelper udpHelper;
	private TextView tvReadyTips = null;
	private BoxCache boxCache = BoxCache.getCache();
	private BoxControler bControler = BoxControler.getInstance();
	private Box currentBox = null;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_HAVE_NEWVERSION:
				final UpdateInfo u = (UpdateInfo) msg.obj;
				new AlertDialog.Builder(ReadyActivity.this)
						.setTitle("版本更新")
						.setMessage("有新版本啦，升个级呗！")
						.setNegativeButton("立即升级",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										beginUpdage(u);
									}
								}).setCancelable(false).show();
				break;
			case MSG_TAGRET_BOX:
				bControler.setBoxApiAddress(currentBox.deviceIpAddr,
						currentBox.httpApiPort);

				Toast.makeText(ReadyActivity.this, "音响配置成功", Toast.LENGTH_SHORT)
						.show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						bControler.getCurrentPlayListFromBox(); // 刷新一下播放列表
						bControler.startSyncBoxPlayState(); // 开始监听音响播放状态
					}
				}).start();
				Intent i = new Intent(ReadyActivity.this, MainActivity.class);
				startActivity(i);
				ReadyActivity.this.finish();
				break;

			default:
				break;
			}
			return false;
		}
	});

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		XHKApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_ready);
		tvReadyTips = (TextView) findViewById(R.id.tv_readyTips);

		bControler.setContext(this);

		udpHelper = UDPHelper.getHelper(this);

		tvReadyTips.setText(R.string.box_scan_box);

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (!checkVersion()) {
					ready();
				}
			}
		}).start();
	}

	private boolean checkVersion() {
		Log.d(TAG, "============checkVersion========");
		String strVersion = NetUtils.getInstance().getJSONDataByGet(
				getString(R.string.url_upgrade)+"?serl="+System.currentTimeMillis());
		Log.d(TAG, "===checkVersion====>" + strVersion);

		if (TextUtils.isEmpty(strVersion)) {
			return false;
		} else {
			try {
				String id = getString(R.string.custome_id);
				JSONObject jsonVer = new JSONObject(strVersion);
				JSONArray arrVer = JSONUtil.getJSONArray(jsonVer, "CustItems");
				for (int i = 0; i < arrVer.length(); i++) {
					JSONObject jo = JSONUtil.getJSONObject(arrVer, i);
					if (id.equals(JSONUtil.getString(jo, "Id"))) {
						UpdateInfo u = new UpdateInfo();
						u.id = id;
						u.version = JSONUtil.getString(jo, "Version");
						u.url = JSONUtil.getString(jo, "Url");
						u.appVersionCode = JSONUtil
								.getInt(jo, "AppVersionCode");
						u.appUrl = JSONUtil.getString(jo, "AppUrl");
						u.appReleaseNote = JSONUtil.getString(jo,
								"AppReleaseNote");
						PreferenceUtils.putString(Contants.PREF_VERSION_NAME,
								u.version);
						PreferenceUtils.putString(Contants.PREF_VERSION_URL,
								u.url);
						if (Device.getCurrAppVersionCode(this) < u.appVersionCode) {
							Message msg = Message.obtain();
							msg.obj = u;
							msg.what = MSG_HAVE_NEWVERSION;
							handler.sendMessage(msg);
							return true;
						}
					}

				}
			} catch (Exception e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
			}
		}
		return false;
	}

	/**
	 * 
	 */
	private void ready() {
		scanBoxes(); // 通过UDP广播扫描
		Log.d(TAG, "===BoxCache.getCache().getBoxCount()==="
				+ BoxCache.getCache().getBoxCount());
		if (boxCache.getBoxCount() == 0) { // 没有扫到音响，则开始配置
			Intent it = new Intent(ReadyActivity.this, ConfigBoxActivity.class);
			startActivity(it);
		} else { // 找到后就配置网络环境
			currentBox = boxCache.getAll().get(0);
			handler.sendEmptyMessage(MSG_TAGRET_BOX);
			// Box temp = boxCache.getAll().get(0); // 获取第一个
			// currentBox = temp;
			// bControler.setBoxApiAddress(temp.deviceIpAddr, temp.httpApiPort);
			// handler.sendEmptyMessage(MSG_TAGRET_BOX);
		}
	}

	/**
	 * 扫描所有的音响设备
	 */
	private void scanBoxes() {
		udpHelper.isThreadDisable = true;
		udpHelper.scanAllBox();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
		Log.d(TAG, "设备扫描完毕");
		udpHelper.isThreadDisable = false; // 5秒后停止扫描

	}

	/**
	 * @param v
	 */
	private void beginUpdage(final UpdateInfo v) {
		UpdatingDialog mUpdatingDialog = new UpdatingDialog(this, v);

		// 强制升级时，用户点击取消
		mUpdatingDialog
				.setOnCancelUpdateClickListener(new OnCancelUpdateClickListener() {
					@Override
					public void onCancelUpdateClick(String url) {
						XHKApplication.getInstance().exit();
					}
				});

		// 下载完毕安装apk
		mUpdatingDialog
				.setOnUpdateCompleteListener(new OnUpdateCompleteListener() {
					@Override
					public void onUpdateComplete(String url, File localFile) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(Uri.fromFile(localFile),
								"application/vnd.android.package-archive");
						startActivity(intent);
						XHKApplication.getInstance().exit();
					}
				});

		// 升级错误
		mUpdatingDialog.setOnUpdateErrorListener(new OnUpdateErrorListener() {
			@Override
			public void OnUpdateError(String url, File localFile) {
				XHKApplication.getInstance().exit();
			}
		});
		mUpdatingDialog.setCancelable(false);
		mUpdatingDialog.show();
		mUpdatingDialog.startUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		XHKApplication.getInstance().removeActivity(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

	}
}
