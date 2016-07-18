package com.xhk.wifibox.fragment;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

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
		TextView tv = (TextView) mView.findViewById(R.id.tv_version);
		try {
			PackageManager pm = getActivity().getPackageManager();
			PackageInfo pi = pm.getPackageInfo(getActivity().getPackageName(), 0);
			tv.setText("版本号:" + pi.versionName);
		} catch (NameNotFoundException e) {
			Log.e("MenuLeftFragment", e.getLocalizedMessage(), e);
		}
		
	}
	
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.listener=(OnLeftItemClickListener) activity;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		listener.onLeftItemClick(v);
	}

	public interface OnLeftItemClickListener {
		void onLeftItemClick(View v);
	}
}
