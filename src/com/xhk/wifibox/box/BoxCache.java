/* 
 * @Title:  BoxCache.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-31 下午6:02:11 
 * @version:  V1.0 
 */
package com.xhk.wifibox.box;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.eryiche.frame.util.PreferenceUtils;
import com.xhk.wifibox.utils.Contants;

import android.util.Log;

/**
 * @author tang
 * 
 */
public class BoxCache {

	private static final String TAG = BoxCache.class.getSimpleName();

	private final static BoxCache cache = new BoxCache();

	private List<Box> boxes = new ArrayList<Box>();

	private BoxCache() {
		// Box b1=new Box();
		// b1.deviceFiremwareVersion="V1.0.0-15111212130-CLOUD-T011";
		// b1.deviceIpAddr="192.168.1.10";
		// b1.httpApiPort=80;
		// b1.deviceName="测试1";
		// b1.isCurrent=true;
		//
		// Box b2=new Box();
		// b2.deviceFiremwareVersion="V1.0.0-15111212130-CLOUD-T011";
		// b2.deviceIpAddr="192.168.1.10";
		// b2.httpApiPort=80;
		// b2.deviceName="测试1";
		//
		// boxes.add(b1);
		// boxes.add(b2);

	}

	public static BoxCache getCache() {
		return cache;
	}

	public void setCurrentBox(Box box) {
		if (box == null) {
			return;
		}
		for (Box b : boxes) {
			if (b.equals(box)) {
				b.isCurrent = true;
			} else {
				b.isCurrent = false;
			}
		}
	}

	public synchronized void addBox(final Box box) {
		if (boxes.contains(box)) {
			boxes.remove(box);
		}
		for (Box b : boxes) {
			b.isCurrent = false;
		}
		box.isCurrent = true;
		boxes.add(box);
		new Thread(new Runnable() {

			@Override
			public void run() {
				DFVManager.getManager().checkVersion(box);
			}
		}).start();
	}

	// public void clear() {
	// boxes.clear();
	// }

	public Box getBox(int index) {
		if (index >= 0 && index < boxes.size()) {
			return boxes.get(index);
		} else {
			return null;
		}
	}

	public Box getCurrent() {
		for (Iterator<Box> iter = boxes.iterator(); iter.hasNext();) {
			Box box = iter.next();
			if (box.isCurrent) {
				return box;
			}
		}
		if (boxes.size() > 0) { // 默认第一个
			return boxes.get(0);
		}
		return null;
	}

	public Box getPlaying() {
		for (Iterator<Box> iter = boxes.iterator(); iter.hasNext();) {
			Box box = iter.next();
			if (box.isPlaying) {
				return box;
			}
		}

		return null;
	}

	public int getBoxCount() {
		return boxes.size();
	}

	public List<Box> getAll() {
		return boxes;
	}

}
