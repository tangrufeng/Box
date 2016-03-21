/* 
 * @Title:  BoxView.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-11-9 上午12:43:04 
 * @version:  V1.0 
 */
package com.xhk.wifibox.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.xhk.wifibox.R;
import com.xhk.wifibox.box.Box;
import com.xhk.wifibox.box.BoxCache;
import com.xhk.wifibox.box.BoxControler;
import com.xhk.wifibox.box.BoxControler.OnBoxPlayStateListener;
import com.xhk.wifibox.box.BoxPlayerState;
import com.xhk.wifibox.box.DFVManager;
import com.xhk.wifibox.box.DFVManager.OnNewVersionFileReadyListener;
import com.xhk.wifibox.box.UDiskInfo;
import com.xhk.wifibox.utils.AlertUtils;
import com.xhk.wifibox.utils.UDPHelper;

/**
 * @author tang
 * 
 */
public class BoxView extends LinearLayout implements OnCheckedChangeListener,
		OnClickListener, OnSeekBarChangeListener, OnBoxPlayStateListener,
		OnLongClickListener, OnNewVersionFileReadyListener {

	private static final String TAG = BoxView.class.getSimpleName();
	private View llBoxOpts, ivPlayStatus;
	ImageButton ibDown;
	private TextView tvName;
	private SeekBar seekBar;
	private ImageButton ibVoice;
	private RadioGroup rgMode, rgDSP;
	private RadioButton rbDSPMusic,rbDSPSub,rbDSPRadio;
	private Button btnReName, btnInfo, btnSetup, btnNewVersion;
	private RadioButton rbs[] = null;
	private Context ctx;
	private Box box = null;

	private BoxControler boxControler = null;
	private static final int MSG_RENAME_SUCCESS = 1;
	private static final int MSG_RENAME_FAILURE = 2;
	private static final int MSG_INFO_GET = 3;
	private static final int MSG_OPT_SUCCESS = 4;
	private static final int MSG_OPT_FAILURE = 5;
	private static final int MSG_STATE_FLUSH = 6;
	private static final int MSG_UPGRADE_SUCCESS = 7;
	private static final int MSG_UPGRADEING = 8;
	private static final int MSG_UPGRADE_FAIL = 9;
	private BoxPlayerState state = null;
	// private ProgressDialog msgTips;

	private OnBoxViewOpenListener listener = null;
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RENAME_SUCCESS:
				// tvName.setText(boxCache.getCurrent().deviceName);
				Toast.makeText(ctx, "设备重命名成功", Toast.LENGTH_SHORT).show();
				break;
			case MSG_RENAME_FAILURE:
				Toast.makeText(ctx, "设备重命名失败", Toast.LENGTH_SHORT).show();
				break;
			case MSG_OPT_SUCCESS:
				Toast.makeText(ctx, "操作成功", Toast.LENGTH_SHORT).show();
				break;
			case MSG_OPT_FAILURE:
				Toast.makeText(ctx, "操作失败", Toast.LENGTH_SHORT).show();
				break;

			case MSG_UPGRADEING:
				AlertUtils.showLoadingDialog(ctx, "正在升级音响，请等待...", false);
				break;
			case MSG_UPGRADE_SUCCESS:
				AlertUtils.showLoadingDialog(ctx, "升级成功,正在重启音响...", false);
				break;
			case MSG_INFO_GET:
				showInfo((List<String>) msg.obj);
				break;
			case MSG_STATE_FLUSH:
				// Log.d(TAG,"========"+String.valueOf(state)+"+==="+String.valueOf(box));
				if (box != null && box.equals(BoxCache.getCache().getCurrent())) {
					flushState();
					BoxView.this.requestLayout();
				}
				break;
			default:
				break;
			}

			return false;

		}
	});

	/**
	 * @param context
	 */
	public BoxView(Context context) {
		super(context);
		this.ctx = context;
		boxControler = boxControler.getInstance();
		boxControler.setContext(context);
		initView();
		boxControler.addOnBoxPlayStateChangedListener(this);
		// flushState();
	}

	public void setOnBoxViewOpenListenter(OnBoxViewOpenListener listener) {
		this.listener = listener;
	}

	public void setBox(Box box) {
		if (box != null) {
			this.box = box;
			tvName.setText(box.deviceName);
			if (box.isCurrent) {
				ivPlayStatus.setVisibility(View.VISIBLE);
			} else {
				ivPlayStatus.setVisibility(View.INVISIBLE);
			}
			if (box.haveNewVersion && DFVManager.getManager().isReady()) {
				btnNewVersion.setVisibility(View.VISIBLE);
			} else {
				btnNewVersion.setVisibility(View.GONE);
			}
		}
	}

	public Box getBox() {
		return box;
	}

	private void initView() {
		LayoutInflater.from(ctx).inflate(R.layout.box_list_item, this);
		tvName = (TextView) findViewById(R.id.tv_boxName);
		ibDown = (ImageButton) findViewById(R.id.ib_box_open);
		llBoxOpts = findViewById(R.id.ll_box_opts);
		seekBar = (SeekBar) findViewById(R.id.sb_voice);
		btnReName = (Button) findViewById(R.id.btn_rename);
		btnInfo = (Button) findViewById(R.id.btn_info);
		btnSetup = (Button) findViewById(R.id.btn_setting);
		ivPlayStatus = findViewById(R.id.iv_boxPlayStatus);
		rgDSP = (RadioGroup) findViewById(R.id.rg_box_dsp);
		rgMode = (RadioGroup) findViewById(R.id.rg_box_model);
		btnNewVersion = (Button) findViewById(R.id.btnNewVersion);
		rbDSPMusic=(RadioButton) findViewById(R.id.rb_dsp_1);
		rbDSPRadio=(RadioButton) findViewById(R.id.rb_dsp_2);
		rbDSPSub=(RadioButton) findViewById(R.id.rb_dsp_3);
		seekBar.setOnSeekBarChangeListener(this);
		btnInfo.setOnClickListener(this);
		btnReName.setOnClickListener(this);
		btnSetup.setOnClickListener(this);
		btnNewVersion.setOnClickListener(this);
		rbDSPMusic.setOnClickListener(this);
		rbDSPRadio.setOnClickListener(this);
		rbDSPSub.setOnClickListener(this);
		rbs = new RadioButton[] {
				(RadioButton) rgMode.findViewById(R.id.rb_aux),
				(RadioButton) rgMode.findViewById(R.id.rb_usb),
				(RadioButton) rgMode.findViewById(R.id.rb_wifi) };
		
		rgMode.setOnCheckedChangeListener(this);
		ibDown.setOnClickListener(this);
	}

	private void flushState() {
		if (state != null) {
			seekBar.setProgress(state.currentVolume);
		}
		// for (int i = 0; i < rbs.length; i++) {
		// if (rbs[i].getText().equals(state.audioSource)) {
		// rgMode.check(rbs[i].getId());
		// }
		// }
	}

	private void setDSPMode(final String mode){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean rst=boxControler.setAudioDSP(mode, box);
				Message msg=handler.obtainMessage();
				msg.what=rst?MSG_OPT_SUCCESS:MSG_OPT_FAILURE;
				handler.sendMessage(msg);
			}
		}).start();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rb_dsp_1:
			setDSPMode("1");
			break;
		case R.id.rb_dsp_2:
			setDSPMode("3");
			break;
		case R.id.rb_dsp_3:
			setDSPMode("2");
			break;
		case R.id.ib_box_open:
			if (!box.isOperating) {
				expandBoxView();
			} else {
				shrinkBoxView();
			}
			break;
		case R.id.btnNewVersion:
			new AlertDialog.Builder(ctx)
					.setTitle("音响固件升级")
					.setMessage("发现有音响的新版本固件，是否现在升级")
					.setPositiveButton("现在升级",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									new Thread(new Runnable() {

										@Override
										public void run() {
											if (boxControler
													.upgradeFirmware(box)) {
												Log.d(TAG, "正在升级音响，请等待...");
												handler.sendEmptyMessage(MSG_UPGRADEING);
												// showMsgTips("正在升级音响，请等待...");
												while (true) { // 等待重启
													try {
														Thread.sleep(3000);
													} catch (InterruptedException e) {
													}
													int status = boxControler
															.getUpgadeStatus(box);
													if (status == 0) {
														handler.sendEmptyMessage(MSG_UPGRADE_SUCCESS);
														boxControler
																.restart(box);
														try {
															Thread.sleep(3000); // 等待音响关闭
														} catch (InterruptedException e) {
														}
														UDPHelper
																.getHelper(ctx)
																.scanBoxByName(
																		box.deviceName);
														break;
													} else if (status == 2004) {
														handler.sendEmptyMessage(MSG_UPGRADE_FAIL);
														break;
													}

												}
											}

										}
									}).start();
								}
							}).setNegativeButton("取消", null).show();
			break;
		case R.id.btn_rename:
			final EditText etName = new EditText(ctx);
			etName.setHint("请输入新的名称");
			etName.setSingleLine(true);
			new AlertDialog.Builder(ctx)
					.setTitle("重命名音响")
					.setIcon(android.R.drawable.ic_dialog_dialer)
					.setView(etName)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									final String newName = etName.getText() != null ? etName
											.getText().toString() : "";
									if (!TextUtils.isEmpty(newName.trim())) {
										new Thread(new Runnable() {
											@Override
											public void run() {
												if (boxControler.renameBox(
														newName.trim(), box)) {
													box.deviceName = newName
															.trim();
													handler.sendEmptyMessage(MSG_RENAME_SUCCESS);
												} else {
													handler.sendEmptyMessage(MSG_RENAME_FAILURE);
												}
											}
										}).start();
									}
								}
							}).setNegativeButton("取消", null).show();
			break;
		case R.id.btn_info:
			getBoxInfo();
			break;
		case R.id.btn_setting:
			new AlertDialog.Builder(ctx)
					.setTitle("音箱设置")
					.setPositiveButton("重启音响",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									new AlertDialog.Builder(ctx)
											.setTitle("确定要重启音箱吗？")
											.setIcon(
													android.R.drawable.ic_dialog_alert)
											.setPositiveButton(
													"确定",
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {

															new Thread(
																	new Runnable() {

																		@Override
																		public void run() {
																			if (boxControler
																					.restart(box)) {
																				handler.sendEmptyMessage(MSG_OPT_SUCCESS);
																			} else {
																				handler.sendEmptyMessage(MSG_OPT_FAILURE);
																			}

																		}
																	}).start();

														}
													})
											.setNegativeButton("取消", null)
											.show();
								}
							})
					.setNegativeButton("取消", null)
					.setNeutralButton("恢复出厂设置",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									new AlertDialog.Builder(ctx)
											.setTitle("确定要恢复音响的出厂设置吗？")
											.setIcon(
													android.R.drawable.ic_dialog_alert)
											.setPositiveButton(
													"确定",
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {

															new Thread(
																	new Runnable() {

																		@Override
																		public void run() {
																			if (boxControler
																					.resumeFactorySettings(box)) {
																				
																				handler.sendEmptyMessage(MSG_OPT_SUCCESS);
																			} else {
																				handler.sendEmptyMessage(MSG_OPT_FAILURE);
																			}

																		}
																	}).start();

														}
													})
											.setNegativeButton("取消", null)
											.show();
								}
							}).show();
			break;
		default:
			shrinkBoxView();
			break;
		}

	}

	/**
	 * 
	 */
	public void expandBoxView() {
		box.isOperating = true;
		ibDown.setImageResource(R.drawable.icon_arrow_up);
		rgMode.setVisibility(View.VISIBLE);
		rgDSP.setVisibility(View.VISIBLE);
		llBoxOpts.setVisibility(View.VISIBLE);
		if (listener != null) {
			listener.change(box);
		}
	}

	public void shrinkBoxView() {
		ibDown.setImageResource(R.drawable.icon_arrow_down);
		box.isOperating = false;
		rgMode.setVisibility(View.GONE);
		rgDSP.setVisibility(View.GONE);
		llBoxOpts.setVisibility(View.GONE);
		ibDown.setVisibility(View.VISIBLE);

	}

	private void getBoxInfo() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<String> infos = new ArrayList<String>();
				String[] basic = boxControler.getDeiviceBasicInfo(box);
				infos.add("音响基本信息");
				infos.add("———名称:" + basic[0]);
				infos.add("———版本:" + basic[1]);
				UDiskInfo u = boxControler.getUdiskInfo(box);
				infos.add("U盘(SD卡)信息");
				switch (u.state) {
				case 0:
					infos.add("———状态:正常");
					infos.add("———大小:"
							+ new BigDecimal(u.size / 1024).intValue() + "M");
					infos.add("———已用:"
							+ new BigDecimal(u.used / 1024).intValue() + "M");
					break;
				case 1:
					infos.add("———状态:没有插入");
					break;
				case 2:
					infos.add("———状态:不能识别");
					break;
				default:
					infos.add("———设备异常");
					break;
				}

				String[] state = boxControler.getNetworkState(box);
				infos.add("音响网络信息");
				if (state == null) {
					infos.add("———状态:异常");
				} else if ("0".equals(state[0])) {
					infos.add("———状态:断开");

				} else {
					infos.add("———状态:正常");
					infos.add("———地址:" + state[1]);
					infos.add("———网关:" + state[2]);
					infos.add("———掩码:" + state[3]);
					infos.add("———DNS:" + state[4]);
				}

				Message msg = Message.obtain();
				msg.what = MSG_INFO_GET;
				msg.obj = infos;
				handler.sendMessage(msg);

			}
		}).start();
	}

	private void showInfo(List<String> list) {

		String arrAPs[] = new String[list.size()];
		new AlertDialog.Builder(ctx).setTitle("音响信息")
				.setItems(list.toArray(arrAPs), null)
				.setPositiveButton("确定", null).show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.LinearLayout#onLayout(boolean, int, int, int, int)
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		// Log.d(TAG, "========box=======" + (box.deviceName) + "==is==>"
		// + box.isCurrent);
		if (box.isCurrent) {
			ivPlayStatus.setVisibility(View.VISIBLE);
		} else {
			ivPlayStatus.setVisibility(View.INVISIBLE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.RadioGroup.OnCheckedChangeListener#onCheckedChanged(android
	 * .widget.RadioGroup, int)
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// Log.d(TAG,"==========="+checkedId);
		if (checkedId == View.NO_ID) {
			return;
		}
		final RadioButton rb = (RadioButton) group.findViewById(checkedId);
		new Thread(new Runnable() {
			@Override
			public void run() {
				boxControler.switchAudioSource(rb.getText().toString(), box);
			}
		}).start();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.SeekBar.OnSeekBarChangeListener#onProgressChanged(android
	 * .widget.SeekBar, int, boolean)
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (fromUser) {
			final int p = progress;
			new Thread(new Runnable() {
				@Override
				public void run() {
					boxControler.setBoxVolume(p, box);
				}
			}).start();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.SeekBar.OnSeekBarChangeListener#onStartTrackingTouch(android
	 * .widget.SeekBar)
	 */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		boxControler.stopSyncBoxPlayState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.SeekBar.OnSeekBarChangeListener#onStopTrackingTouch(android
	 * .widget.SeekBar)
	 */
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		try {
			boxControler.startSyncBoxPlayState();
		} catch (Exception e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xhk.wifibox.box.BoxControler.OnBoxPlayStateListener#OnStateChanged
	 * (com.xhk.wifibox.box.BoxPlayerState)
	 */
	@Override
	public void OnStateChanged(BoxPlayerState state) {
		this.state = state;

		handler.sendEmptyMessage(MSG_STATE_FLUSH);
	}

	public interface OnBoxViewOpenListener {
		public void change(Box box);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnLongClickListener#onLongClick(android.view.View)
	 */
	@Override
	public boolean onLongClick(View v) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.xhk.wifibox.box.DFVManager.OnNewVersionFileReadyListener#onReady()
	 */
	@Override
	public void onReady() {
		if (box != null && box.haveNewVersion) {
			btnNewVersion.setVisibility(View.VISIBLE);
		} else {
			btnNewVersion.setVisibility(View.GONE);
		}
	}

}
