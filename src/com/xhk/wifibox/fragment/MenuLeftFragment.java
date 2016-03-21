package com.xhk.wifibox.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.xhk.wifibox.R;

public class MenuLeftFragment extends Fragment implements OnClickListener
{
	private View mView;
	
	private OnLeftItemClickListener listener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		if (mView == null)
		{
			initView(inflater, container);
		}
		return mView;
	}

	private void initView(LayoutInflater inflater, ViewGroup container)
	{
		mView = inflater.inflate(R.layout.my_music, container, false);
		mView.findViewById(R.id.ll_left_searchSong).setOnClickListener(this);
		mView.findViewById(R.id.ll_left_localSong).setOnClickListener(this);
		mView.findViewById(R.id.ll_left_myLove).setOnClickListener(this);
		mView.findViewById(R.id.ll_left_playlist).setOnClickListener(this);
		mView.findViewById(R.id.ll_left_xmly).setOnClickListener(this);
		mView.findViewById(R.id.ll_left_xiami).setOnClickListener(this);
		mView.findViewById(R.id.ll_left_ttfm).setOnClickListener(this);
		mView.findViewById(R.id.btn_exit).setOnClickListener(this);
	}
	
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.listener=(OnLeftItemClickListener) activity;
	}

	
	
	public interface OnLeftItemClickListener {
		public void onLeftItemClick(View v);
	}


	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		listener.onLeftItemClick(v);
	}
}
