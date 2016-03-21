package com.xhk.wifibox.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.xhk.wifibox.R;
import com.xhk.wifibox.XHKApplication;
import com.xhk.wifibox.action.PlayerAction;
import com.xhk.wifibox.activity.ttfm.TTFMMainActivity;
import com.xhk.wifibox.activity.xm.XMMainActivity;
import com.xhk.wifibox.activity.xmly.XMLYMainActivity;
import com.xhk.wifibox.box.BoxCache;
import com.xhk.wifibox.box.BoxControler;
import com.xhk.wifibox.box.BoxPlayerState;
import com.xhk.wifibox.fragment.MenuLeftFragment;
import com.xhk.wifibox.fragment.MenuLeftFragment.OnLeftItemClickListener;
import com.xhk.wifibox.fragment.MenuRightFragment;
import com.xhk.wifibox.fragment.MiniPlayerFragment;
import com.xhk.wifibox.fragment.MiniPlayerFragment.OnMiniPlayerClickListener;
import com.xhk.wifibox.model.MediaDatabase;
import com.xhk.wifibox.utils.Contants;

public abstract class BasePlayerActivity extends SlidingActivity implements
		OnMiniPlayerClickListener, OnLeftItemClickListener {

	public final String TAG = getClass().getSimpleName();
	private SlidingMenu menu = null;
	private BoxControler mControler = BoxControler.getInstance();

	private FrameLayout flContent;
	private Button btnMusic, btnBoxes;
	private ImageButton ibBack, ibMore;
	private TextView tvTitle;
	private View contentView, titleView;
	private MiniPlayerFragment miniPlayer;
	protected final int DAILOG_LOADDATE = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		XHKApplication.getInstance().addActivity(this);
		BoxControler.getInstance().setContext(this);
		contentView = getLayoutInflater().inflate(R.layout.base_player, null);
		titleView = contentView.findViewById(R.id.rl_baseTitle);
		flContent = (FrameLayout) contentView
				.findViewById(R.id.base_content_frame);
		setContentView(contentView);
		menu = getSlidingMenu();
		initTitleView();
		initMenu();
		initMiniPlayer();

		mControler.startSyncBoxPlayState();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		// mControler.startSyncBoxPlayState();
	}

	public void hidenTitle() {
		if (titleView != null) {
			titleView.setVisibility(View.GONE);
		}
	}

	private void initTitleView() {
		btnMusic = (Button) findViewById(R.id.ib_myMusicBtn);
		btnBoxes = (Button) findViewById(R.id.ib_myBoxesBtn);
		ibBack = (ImageButton) findViewById(R.id.ib_home);
		ibMore = (ImageButton) findViewById(R.id.ib_list_more);
		tvTitle = (TextView) findViewById(R.id.tv_activity_title);
		tvTitle.setText(getActivityTitle());
		tvTitle.setSelected(true);
		btnMusic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showMenu();
			}
		});
		btnBoxes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showSecondaryMenu();
			}
		});
		ibBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(BasePlayerActivity.this,
						MainActivity.class);
				startActivity(i);
			}
		});
	}

	public void setMoreBtnListener(OnClickListener listener) {
		ibMore.setVisibility(View.VISIBLE);
		ibMore.setOnClickListener(listener);
	}

	protected abstract String getActivityTitle();

	protected void showTitleBtn(boolean showBack) {
		if (showBack) {
			ibBack.setVisibility(View.VISIBLE);
			btnMusic.setVisibility(View.GONE);
			btnBoxes.setVisibility(View.GONE);
		} else {
			ibBack.setVisibility(View.GONE);
			btnMusic.setVisibility(View.VISIBLE);
			btnBoxes.setVisibility(View.VISIBLE);
		}
	}

	private void initMiniPlayer() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		miniPlayer = (MiniPlayerFragment) getSupportFragmentManager()
				.findFragmentByTag("mini_player");
		if (miniPlayer == null) {
			miniPlayer = new MiniPlayerFragment();
			ft.add(R.id.fragment_mini_player, miniPlayer, "mini_player");
		} else {
			ft.show(miniPlayer);
		}
		ft.commit();

	}

	private void initMenu() {
		// menu.setContent(view)
		Fragment leftMenuFragment = new MenuLeftFragment();
		setBehindContentView(R.layout.left_menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.id_left_menu_frame, leftMenuFragment).commit();
		menu.setMode(SlidingMenu.LEFT_RIGHT);
		// 设置触摸屏幕的模式
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow_left);
		// 设置滑动菜单视图的宽度
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// 设置渐入渐出效果的值
		menu.setFadeDegree(0.35f);
		// menu.setBehindScrollScale(1.0f);
		menu.setSecondaryShadowDrawable(R.drawable.shadow_right);
		// 设置右边（二级）侧滑菜单
		menu.setSecondaryMenu(R.layout.right_menu_frame);
		Fragment rightMenuFragment = new MenuRightFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.id_right_menu_frame, rightMenuFragment).commit();
	}

	public void setCustomContentView(int resId) {
		setCustomContentView(getLayoutInflater().inflate(resId, null));
	}

	public View getCustomContentView() {
		return flContent.getChildAt(0);
	}

	public void setCustomContentView(View v) {
		flContent.removeAllViews();
		flContent.addView(v);
		flContent.requestLayout();
	}

	@Override
	public void onMiniPlayerClick() {
		Intent i = new Intent(this, PlayerActivity.class);
		startActivity(i);

	}

	public void closeMiniPlayer() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (miniPlayer != null) {
			ft.remove(miniPlayer);
		}
		ft.commit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int)
	 */
	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DAILOG_LOADDATE: {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("好音乐马上就来...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			return dialog;
		}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xhk.wifibox.fragment.MenuLeftFragment.OnLeftItemClickListener#
	 * onLeftItemClick(android.view.View)
	 */
	@Override
	public void onLeftItemClick(View v) {
		switch (v.getId()) {
		case R.id.ll_left_searchSong:
			Intent i = new Intent(this, SearchActivity.class);
			startActivity(i);
			break;
		case R.id.ll_left_myLove:
			Intent toLove = new Intent(BasePlayerActivity.this,
					PlayListActivity.class);
			toLove.putExtra(Contants.INTENT_EXTRA_LOADDATA_CLASS,
					PlayerAction.class);
			toLove.putExtra(Contants.INTENT_EXTRA_LOADDATA_METHOD_NAME,
					"getLovePlayList");
			toLove.putExtra(Contants.INTENT_EXTRA_LIST_ID, "");
			toLove.putExtra(Contants.INTENT_EXTRA_LOADDATA_NOFRESH, true);
			toLove.putExtra(Contants.INTENT_EXTRA_LIST_NAME, "我的最爱");
			toLove.putExtra(Contants.INTENT_EXTRA_LOVE_LIST,
					true);
			toLove.putExtra(Contants.INTENT_EXTRA_LOCAL_PLAYLIST,
					true);
			startActivity(toLove);
			break;
		case R.id.ll_left_localSong:
			int cnt = MediaDatabase.getInstance(this).getLocalSongTotal();
			Intent toLocal;
			if (cnt > 0) {
				toLocal = new Intent(this, PlayListActivity.class);
				toLocal.putExtra(Contants.INTENT_EXTRA_LOADDATA_CLASS,
						PlayerAction.class);
				toLocal.putExtra(Contants.INTENT_EXTRA_LOADDATA_METHOD_NAME,
						"getLocalSongs");
				toLocal.putExtra(Contants.INTENT_EXTRA_LOCAL_SONGS, true);
				toLocal.putExtra(Contants.INTENT_EXTRA_LIST_ID, "");
				toLocal.putExtra(Contants.INTENT_EXTRA_LOADDATA_NOFRESH, true);
				toLocal.putExtra(Contants.INTENT_EXTRA_LIST_NAME, "本地歌曲");
			} else {
				toLocal = new Intent(this, ScanActivity.class);
			}
			startActivity(toLocal);
			break;
		case R.id.ll_left_xiami:
			Intent toXM = new Intent(this, XMMainActivity.class);
			startActivity(toXM);
			break;
		case R.id.ll_left_ttfm:
			Intent toTT = new Intent(this, TTFMMainActivity.class);
			startActivity(toTT);
			break;
		case R.id.ll_left_xmly:
			Intent toXMLY = new Intent(this, XMLYMainActivity.class);
			startActivity(toXMLY);
			break;
		case R.id.ll_left_playlist:
			Intent toMyPlayList = new Intent(this, PlayListListActivity.class);
			startActivity(toMyPlayList);
			break;
		case R.id.btn_exit:
			XHKApplication.getInstance().exit();
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("退出系统").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {

						XHKApplication.getInstance().exit();
						return true;
					}
				});
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		// mControler.stopSyncBoxPlayState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		XHKApplication.getInstance().removeActivity(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			new Thread(new Runnable() {

				@Override
				public void run() {
					BoxControler mControler = BoxControler.getInstance();
					mControler.stopSyncBoxPlayState();
					synchronized (BoxCache.getCache().getCurrent()) {
						BoxPlayerState bps = mControler.getBoxPlayerState();
						BoxControler.getInstance().setBoxVolume(

						bps.currentVolume < 0 ? 0 : bps.currentVolume - 5,
								BoxCache.getCache().getCurrent());

					}
					mControler.startSyncBoxPlayState();
				}
			}).start();
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			new Thread(new Runnable() {

				@Override
				public void run() {
					BoxControler mControler = BoxControler.getInstance();
					mControler.stopSyncBoxPlayState();
					synchronized (BoxCache.getCache().getCurrent()) {
						BoxPlayerState bps = mControler.getBoxPlayerState();
						BoxControler.getInstance().setBoxVolume(
								bps.currentVolume > 100 ? 100
										: bps.currentVolume + 5,
								BoxCache.getCache().getCurrent());

					}
					mControler.startSyncBoxPlayState();
				}
			}).start();
			return true;
		case KeyEvent.KEYCODE_VOLUME_MUTE:
			new Thread(new Runnable() {

				@Override
				public void run() {
					BoxControler mControler = BoxControler.getInstance();
					mControler.stopSyncBoxPlayState();
					synchronized (BoxCache.getCache().getCurrent()) {
						BoxControler.getInstance().setBoxVolume(0,
								BoxCache.getCache().getCurrent());

					}
					mControler.startSyncBoxPlayState();
				}
			}).start();
			return true;
		case KeyEvent.KEYCODE_MENU:
			openOptionsMenu();
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);

	}
}
