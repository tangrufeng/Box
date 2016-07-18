package com.xhk.wifibox.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.xhk.wifibox.R;
import com.xhk.wifibox.XHKApplication;
import com.xhk.wifibox.activity.ConfigBoxActivity;
import com.xhk.wifibox.adapter.BoxListAdapter;
import com.xhk.wifibox.box.Box;
import com.xhk.wifibox.box.BoxCache;
import com.xhk.wifibox.box.BoxControler;
import com.xhk.wifibox.utils.UDPHelper;
import com.xhk.wifibox.utils.UDPHelper.OnFindBoxListener;

import java.util.ArrayList;
import java.util.List;

public class MenuRightFragment extends Fragment implements OnFindBoxListener {
	private static final String TAG = MenuRightFragment.class.getSimpleName();
	private final static int MSG_NEED_RESTARTAPP = 0;
	BoxControler bControler = BoxControler.getInstance();
	List<Box> boxes = new ArrayList<Box>();
	private View mView = null;
	private BoxListAdapter adapter = null;

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_NEED_RESTARTAPP:
				new AlertDialog.Builder(getActivity())
						.setTitle("音响升级成功")
						.setMessage("音响升级成功，需要重启客户端")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										XHKApplication.getInstance().restartApplication();

									}
								}).show();
				break;
			default:
				break;
			}
			return false;

		}
	});

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (mView == null) {
			mView = inflater.inflate(R.layout.my_boxes, container, false);
		}
		final ListView lv = (ListView) mView.findViewById(R.id.lv_boxList);
		boxes.addAll(BoxCache.getCache().getAll());
		adapter = new BoxListAdapter(getActivity(), 0, boxes);
		lv.setAdapter(adapter);

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (view.getId() == R.id.ll_ritht_box) {
					Box box = BoxCache.getCache().getBox(position);
					BoxCache.getCache().setCurrentBox(box);
					Log.d(TAG, "========box=======" + (box.deviceName));
					int cnt = lv.getChildCount();
					for (int i = 0; i < cnt; i++) {
						lv.getChildAt(i).requestLayout();
					}
					// adapter.clear();
					boxes.clear();
					boxes.addAll(BoxCache.getCache().getAll());
					adapter.notifyDataSetChanged();
					lv.refreshDrawableState();
					// MenuRightFragment.this.onResume();
					new Thread(new Runnable() {
						@Override
						public void run() {
							bControler.getCurrentPlayListFromBox(); // 刷新一下播放列表
							bControler.startSyncBoxPlayState(); // 开始监听音响播放状态
						}
					}).start();
				}
				return false;
			}
		});
		adapter.setListView(lv);

		mView.findViewById(R.id.ib_addBox).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent i = new Intent(getActivity(),
								ConfigBoxActivity.class);
						getActivity().startActivity(i);
					}
				});

		return mView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		bControler.setContext(getActivity());
		UDPHelper.getHelper(getActivity()).setOnFindBoxListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xhk.wifibox.utils.UDPHelper.OnFindBoxListener#afterFound()
	 */
	@Override
	public void afterFound(Box box) {
		Log.d(TAG, "===find the box==>" + box);
//		boxes.clear();
		handler.sendEmptyMessage(MSG_NEED_RESTARTAPP);
//		AlertUtils.dismissLoadingDialog();
	}

}
