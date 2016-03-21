/* 
 * @Title:  XHKAction.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-9-10 下午5:22:38 
 * @version:  V1.0 
 */
package com.xhk.wifibox.action;

import java.util.List;

import com.xhk.wifibox.track.TrackMeta;

/**
 * @author tang
 * 
 */
public interface XHKAction {

//	private List<TrackMeta> trackFlush = new ArrayList<TrackMeta>();
//	private ReadWriteLock rwl = new ReentrantReadWriteLock();
//
//	public void clearFlush() {
//		rwl.writeLock().lock();
//		try {
//			trackFlush.clear();
//		} finally {
//			rwl.writeLock().unlock();
//		}
//	}
//
//	public void putFlush(List<TrackMeta> list) {
//		if (list == null) {
//			return;
//		}
//		rwl.writeLock().lock();
//		try {
//			trackFlush.clear();
//		} finally {
//			trackFlush.addAll(list);
//		}
//	}
//
//	public void putFlush(TrackMeta track) {
//		if (track == null) {
//			return;
//		}
//		rwl.writeLock().lock();
//		try {
//			trackFlush.clear();
//		} finally {
//			trackFlush.add(track);
//		}
//	}

	public List<TrackMeta> getCollectSongs(String collectId,int pageSize,
			int pageIndex);

	public List<TrackMeta> searchSong(String key, int pageSize, int pageIndex);

	public TrackMeta getSong();
}
