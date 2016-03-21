package com.xhk.wifibox.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xhk.wifibox.R;
import com.xhk.wifibox.box.BoxCache;
import com.xhk.wifibox.box.BoxControler;
import com.xhk.wifibox.box.BoxControler.OnBoxPlayStateListener;
import com.xhk.wifibox.box.BoxPlayerState;
import com.xhk.wifibox.view.CircleImageView;

public class MiniPlayerFragment extends Fragment implements OnClickListener,
		OnBoxPlayStateListener {

	private View miniPlayer;
	private CircleImageView civ;
	private TextView tvBoxName, tvSongName;
	private ImageButton ibPlay;
	private Animation operatingAnim = null;
	private final BoxControler mControler = BoxControler.getInstance();
	private OnMiniPlayerClickListener listener;

	private final Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == 100) {
				resetTrackView();
			}
			return false;
		}
	});

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.mini_player, null);
		mControler.setContext(getActivity());
		initView(view);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		if (activity instanceof OnMiniPlayerClickListener) {
			listener = (OnMiniPlayerClickListener) activity;
		} else {
			throw new IllegalStateException(
					"the activity must be implements OnMiniPlayerClickListener");
		}
		super.onAttach(activity);
	}

	private void initView(View v) {
		miniPlayer = v.findViewById(R.id.mini_player);
		civ = (CircleImageView) v.findViewById(R.id.mini_player_image);
		operatingAnim = AnimationUtils.loadAnimation(getActivity(),
				R.anim.rotate);
		operatingAnim.setInterpolator(new LinearInterpolator());
		civ.setAnimation(operatingAnim);
		tvBoxName = (TextView) v.findViewById(R.id.mini_player_boxname);
		tvSongName = (TextView) v.findViewById(R.id.mini_player_songname);
		ibPlay = (ImageButton) v.findViewById(R.id.mini_player_play);
		miniPlayer.setOnClickListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		mControler.addOnBoxPlayStateChangedListener(this);
		super.onStart();
	}

	private void resetTrackView() {

		if (mControler.getCurrentTrack() != null) {
			tvSongName.setText(mControler.getCurrentTrack().getName());
			ImageLoader.getInstance().displayImage(
					mControler.getCurrentTrack().getCoverUrl(), civ);
		}
		if (BoxCache.getCache().getCurrent() != null) {
			tvBoxName.setText(BoxCache.getCache().getCurrent().deviceName);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		resetTrackView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mini_player:
			if (mControler.getCurrentTrack() != null
					&& !TextUtils.isEmpty(mControler.getCurrentTrack()
							.getPlayUrl())) {
				listener.onMiniPlayerClick();
			}
			break;

		default:
			break;
		}

	}

	public interface OnMiniPlayerClickListener {
		public void onMiniPlayerClick();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
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
		handler.sendEmptyMessage(100);
	}
}
