package com.xhk.wifibox.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xhk.wifibox.R;
import com.xhk.wifibox.action.PlayerAction;
import com.xhk.wifibox.box.BoxControler;
import com.xhk.wifibox.box.BoxControler.OnBoxPlayStateListener;
import com.xhk.wifibox.box.BoxPlayerState;
import com.xhk.wifibox.model.MediaDatabase;
import com.xhk.wifibox.track.TrackMeta;
import com.xhk.wifibox.utils.Contants;
import com.xhk.wifibox.utils.Util;
import com.xhk.wifibox.view.CircleImageView;

public class PlayerActivity extends BasePlayerActivity implements
		OnClickListener, OnSeekBarChangeListener, OnBoxPlayStateListener {

	private final BoxControler mControler = BoxControler.getInstance();
	private TextView tvTrackName, tvArter, tvCurentLength, tvTotalLength;
	private ImageButton ibPlayer, ibPrevious, ibNext, ibLove, ibAdd, ibBack,
			ibMore, ibPlayList;
	private View rlCover,llControl;
	private CircleImageView civ_cover;
	private SeekBar seekBar;
	private MediaDatabase mDb = null;
	private Animation operatingAnim = null;
	private final int MSG_STATUS_CHARGED = 0;
	private final int MSG_LOVE_ADD = 1;
	private final int MSG_LOVE_DEL = 2;
	private final int MSG_BIND_HOTKEY = 3;
	private final int MSG_ADD_PLAYLIST = 4;
	private final int MSG_ADD_PLAYLIST_SUCCESS = 5;
	private final int MSG_BIND_HOTKEY_RST = 6;
	private final int MSG_STOP_ANIM = 7;
	private final int MSG_START_ANIM = 8;

	final List<TrackMeta> toHotkeyList = new ArrayList<TrackMeta>();
	private BoxPlayerState state = new BoxPlayerState();

	private String[] hotKeys = new String[] { "热键1", "热键2", "热键3", "热键4",
			"热键5", "热键6" };
	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_STATUS_CHARGED:
				resetTrackView();
				break;
			case MSG_LOVE_ADD:
				Toast.makeText(PlayerActivity.this, "已添加至\"我的最爱\"",
						Toast.LENGTH_SHORT).show();
				ibLove.setImageResource(R.drawable.player_btn_favorited_normal);
				break;
			case MSG_LOVE_DEL:
				Toast.makeText(PlayerActivity.this, "已从\"我的最爱\"中移除",
						Toast.LENGTH_SHORT).show();
				ibLove.setImageResource(R.drawable.player_btn_favorite_normal);
				break;
			case MSG_BIND_HOTKEY:
				bindHotKey();
				break;
			case MSG_ADD_PLAYLIST:
				addPlaylist();
				break;
			case MSG_ADD_PLAYLIST_SUCCESS:
				String list = String.valueOf(msg.obj);
				Toast.makeText(PlayerActivity.this, "已添加至\"" + list + "\"",
						Toast.LENGTH_SHORT).show();
				break;
			case MSG_STOP_ANIM:
				rlCover.clearAnimation();
				break;
			case MSG_START_ANIM:
				operatingAnim.startNow();
				break;
			case MSG_BIND_HOTKEY_RST:
				if(msg.arg1==1){
					Toast.makeText(PlayerActivity.this, "绑定成功",
							Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(PlayerActivity.this, "绑定失败",
							Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
			return false;
		}
	});

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setCustomContentView(R.layout.activity_player);
		initView();
		mDb = MediaDatabase.getInstance(this);
		mControler.addOnBoxPlayStateChangedListener(this);
		mControler.startSyncBoxPlayState();
		resetTrackView();
		closeMiniPlayer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mControler.addOnBoxPlayStateChangedListener(this);
		mControler.startSyncBoxPlayState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xhk.wifibox.activity.BasePlayerActivity#getActivityTitle()
	 */
	@Override
	protected String getActivityTitle() {
		return getString(R.string.app_name);
	}

	private void initView() {
		hidenTitle();
		tvTrackName = (TextView) findViewById(R.id.tv_player_songname);
		rlCover = findViewById(R.id.rl_cover);
		llControl=findViewById(R.id.ll_player_controler);
		civ_cover = (CircleImageView) findViewById(R.id.civ_cover);
		tvArter = (TextView) findViewById(R.id.tv_player_arter);
		tvCurentLength = (TextView) findViewById(R.id.tv_player_curentLength);
		tvTotalLength = (TextView) findViewById(R.id.tv_player_totalLength);
		ibPlayer = (ImageButton) findViewById(R.id.ib_player_play);
		ibPrevious = (ImageButton) findViewById(R.id.ib_player_previous);
		ibNext = (ImageButton) findViewById(R.id.ib_player_next);
		ibLove = (ImageButton) findViewById(R.id.ib_player_love);
		ibAdd = (ImageButton) findViewById(R.id.ib_player_add);
		ibBack = (ImageButton) findViewById(R.id.ib_player_back);
		ibMore = (ImageButton) findViewById(R.id.ib_player_more);
		ibPlayList = (ImageButton) findViewById(R.id.ib_player_playlist);
		seekBar = (SeekBar) findViewById(R.id.sb_player_process);
		seekBar.setOnSeekBarChangeListener(this);
		ibPlayer.setOnClickListener(this);
		ibPrevious.setOnClickListener(this);
		ibNext.setOnClickListener(this);
		ibLove.setOnClickListener(this);
		ibBack.setOnClickListener(this);
		ibMore.setOnClickListener(this);
		ibAdd.setOnClickListener(this);
		operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		operatingAnim.setInterpolator(new LinearInterpolator());
		rlCover.setAnimation(operatingAnim);
		operatingAnim.startNow();

		ibPlayList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PlayerActivity.this,
						PlayListActivity.class);
				intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_CLASS,
						PlayerAction.class);
				intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_METHOD_NAME,
						"getCurrentPlayList");
				intent.putExtra(Contants.INTENT_EXTRA_LIST_ID, "");
				intent.putExtra(Contants.INTENT_EXTRA_LOADDATA_NOFRESH, true);
				intent.putExtra(Contants.INTENT_EXTRA_LIST_NAME, "当前播放列表");

				startActivity(intent);

			}
		});
	}

	private void resetTrackView() {
		TrackMeta track = mControler.getCurrentTrack();
		handleLove(track);
		ImageLoader.getInstance().displayImage(track.getCoverUrl(), civ_cover);

		if (state != null
				&& BoxPlayerState.BOX_PLAYING.equals(state.transportState)) {
			operatingAnim.reset();
			operatingAnim.startNow();
			rlCover.setAnimation(operatingAnim);
			ibPlayer.setImageResource(R.drawable.pause);
		} else {
			rlCover.clearAnimation();
			ibPlayer.setImageResource(R.drawable.play);
		}
		tvTotalLength.setText(state == null ? "00:00:00"
				: state.currentTrackDuration);
		tvCurentLength.setText(state == null ? "00:00:00"
				: state.relativeTimePosition);
		tvTrackName.setText(track.getName());
		tvTrackName.setSelected(true);
		String artist = track.getArtist();
		if (null == artist || "null".equals(artist)
				|| "\"null\"".equals(artist)) {
			artist = "";
		}
		tvArter.setText(artist);
		if (track.getDuration() != 0) {
			llControl.setVisibility(View.VISIBLE);
			seekBar.setProgress(track.getCurDuration() * 100
					/ track.getDuration());
		} else {
			llControl.setVisibility(View.INVISIBLE);
			seekBar.setProgress(100);
		}

	}

	private void handleLove(final TrackMeta track) {
		if (mDb.isExistInLove(track)) {
			ibLove.setImageResource(R.drawable.player_btn_favorited_normal);
		} else {
			ibLove.setImageResource(R.drawable.player_btn_favorite_normal);
		}
	}

	private void bindHotKey() {

		new AlertDialog.Builder(PlayerActivity.this)
				.setIconAttribute(android.R.drawable.ic_dialog_dialer)
				.setTitle("请选择你要绑定的热键")
				.setItems(hotKeys, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, final int which) {
						toHotkeyList.clear(); // 一次只绑定一首
						toHotkeyList.add(mControler.getCurrentTrack());
						new Thread(new Runnable() {

							@Override
							public void run() {
								boolean rst=BoxControler.getInstance().bindHotKey(
										which + 1, toHotkeyList);
								Message msg=handler.obtainMessage();
								msg.what=MSG_BIND_HOTKEY_RST;
								if(rst)
									msg.arg1=1;
								handler.sendMessage(msg);
								
							}
						}).start();
					}
				}).show();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		final View view = v;
		new Thread(new Runnable() {

			@Override
			public void run() {
				switch (view.getId()) {
				case R.id.ib_player_play:
					if (state != null) {
						if (BoxPlayerState.BOX_PLAYING
								.equals(state.transportState)) {
							mControler.pause();
							handler.sendEmptyMessage(MSG_STOP_ANIM);
						} else {
							mControler.play();
							handler.sendEmptyMessage(MSG_START_ANIM);
//							rlCover.setAnimation(operatingAnim);
						}
						handler.sendEmptyMessage(MSG_STATUS_CHARGED);
					}
					break;
				case R.id.ib_player_next:
					mControler.next();
					handler.sendEmptyMessage(MSG_STATUS_CHARGED);
					break;
				case R.id.ib_player_previous:
					mControler.previous();
					handler.sendEmptyMessage(MSG_STATUS_CHARGED);
					break;
				case R.id.ib_player_love:
					TrackMeta track = mControler.getCurrentTrack();
					if (mDb.isExistInLove(track)) {
						mDb.delLove(track);
						handler.sendEmptyMessage(MSG_LOVE_DEL);
					} else {
						mDb.addLove(track);
						handler.sendEmptyMessage(MSG_LOVE_ADD);
					}
					break;
				case R.id.ib_player_back:
					PlayerActivity.this.finish();
					break;
				case R.id.ib_player_more:
					handler.sendEmptyMessage(MSG_BIND_HOTKEY);
					break;
				case R.id.ib_player_add:
					handler.sendEmptyMessage(MSG_ADD_PLAYLIST);
					break;
				default:
					break;
				}

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
			TrackMeta m = mControler.getCurrentTrack();
			int length = Util.fromHHmmSS(state.currentTrackDuration);
			int cur = (length / 100 * progress) * 1000;
			final String seek = Util.formatHHmmSS(cur);
			Log.d(TAG,
					"==CUR==>" + cur + "===m.getDuration()==>"
							+ m.getDuration() + "===prog---" + progress
							+ "====>" + seek);
			new Thread(new Runnable() {

				@Override
				public void run() {
					mControler.seek(seek);
				}
			}).start();
		}
	}

	private void addPlaylist() {
		final String[] playlist = mDb.getLocalPlaylist();
		new AlertDialog.Builder(PlayerActivity.this).setTitle("请选择歌单")
				.setItems(playlist, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mDb.addMediaToPlaylist(playlist[which],
								mControler.getCurrentTrack());
						Message msg = Message.obtain();
						msg.obj = playlist[which];
						msg.what = MSG_ADD_PLAYLIST_SUCCESS;
						handler.sendMessage(msg);
					}
				}).show();
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
		mControler.stopSyncBoxPlayState();
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
			mControler.startSyncBoxPlayState();
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
		handler.sendEmptyMessage(MSG_STATUS_CHARGED);

	}
}
