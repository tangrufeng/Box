package com.xhk.wifibox.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xhk.wifibox.R;
import com.xhk.wifibox.XHKApplication;
import com.xhk.wifibox.action.PlayerAction;
import com.xhk.wifibox.model.MediaDatabase;
import com.xhk.wifibox.utils.Contants;
import com.xhk.wifibox.vlc.MediaLibrary;

public class ScanActivity extends BasePlayerActivity implements OnClickListener {

	private Button btnScan;
	private CheckBox cbScanScope;
	private MediaLibrary library;
	private TextView tvProcessing;
	private int total = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCustomContentView(R.layout.activity_scan_local);
		initView();
		library = MediaLibrary.getInstance(this);
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(Contants.BROADCAST_SCAN_COUNT);
		iFilter.addAction(Contants.BROADCAST_SCAN_START);
		iFilter.addAction(Contants.BROADCAST_SCAN_FINISH);
		iFilter.addAction(Contants.BROADCAST_SCAN_TOTAL);
		registerReceiver(new Receiver(), iFilter);
	}

	private void initView() {
		btnScan = (Button) findViewById(R.id.btnScan);
		btnScan.setOnClickListener(this);
		cbScanScope = (CheckBox) findViewById(R.id.cbScanScope);
		cbScanScope.setChecked(true);
		tvProcessing = (TextView) findViewById(R.id.tvProcessing);
	}

	@Override
	public void onClick(View v) {
		if (R.id.btnScan == v.getId()) {
			MediaDatabase.getInstance(this).clearMedia();
			if (cbScanScope.isChecked()) {
				library.loadMediaItems(this, 60000l);
			} else {
				library.loadMediaItems(this);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.xhk.wifibox.activity.BasePlayerActivity#getActivityTitle()
	 */
	@Override
	protected String getActivityTitle() {
		return getString(R.string.my_localMusic);
	}

	public class Receiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d("ScanActivity", action);
			if (Contants.BROADCAST_SCAN_COUNT.equals(action)) {
				int count = intent.getIntExtra(Contants.EXTRA_SCAN_COUNT, 0);
				Log.d("ScanActivity", "正在扫描...." + count + "----" + total
						+ "----" + (count * 100) / total + "%");
				tvProcessing.setText("正在扫描...." + (count * 100) / total
						+ "%");
			} else if (Contants.BROADCAST_SCAN_START.equals(action)) {
				tvProcessing.setText("正在扫描....");
			} else if (Contants.BROADCAST_SCAN_TOTAL.equals(action)) {
				total = intent.getIntExtra(Contants.EXTRA_SCAN_TOTAL, 0);
				tvProcessing.setText("正在扫描....0%");
			} else if (Contants.BROADCAST_SCAN_FINISH.equals(action)) {
				MediaDatabase db = MediaDatabase.getInstance(XHKApplication
						.getAppContext());
				int cnt = db.getLocalSongTotal();
				tvProcessing.setText("扫描结束,共添加歌曲" + cnt + "首");

				Intent toLocal;
				if (cnt > 0) {
					toLocal = new Intent(ScanActivity.this,
							PlayListActivity.class);
					toLocal.putExtra(Contants.INTENT_EXTRA_LOADDATA_CLASS,
							PlayerAction.class);
					toLocal.putExtra(
							Contants.INTENT_EXTRA_LOADDATA_METHOD_NAME,
							"getLocalSongs");
					toLocal.putExtra(Contants.INTENT_EXTRA_LIST_ID, "");
					toLocal.putExtra(Contants.INTENT_EXTRA_LOADDATA_NOFRESH,
							true);
					toLocal.putExtra(Contants.INTENT_EXTRA_LOCAL_SONGS,
							true);
					toLocal.putExtra(Contants.INTENT_EXTRA_LIST_NAME, "本地歌曲");
					startActivity(toLocal);
				}
			}
		}

	}
}
