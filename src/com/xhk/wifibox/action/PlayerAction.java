/* 
 * @Title:  PlayerAction.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-11-2 上午2:52:14 
 * @version:  V1.0 
 */
package com.xhk.wifibox.action;

import android.content.Context;
import android.util.Log;

import com.xhk.wifibox.box.BoxControler;
import com.xhk.wifibox.model.Media;
import com.xhk.wifibox.model.MediaDatabase;
import com.xhk.wifibox.track.Source;
import com.xhk.wifibox.track.TrackMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author tang
 *
 */
public class PlayerAction {
	private final static String TAG = PlayerAction.class.getSimpleName();
	BoxControler mControler=null;
	private Context ctx;
	public PlayerAction(Context ctx) {
		this.ctx=ctx;
		mControler=BoxControler.getInstance();
		mControler.setContext(ctx);
	}
	
	public List<TrackMeta> getCurrentPlayList(String arg0,int arg1,int arg2){
		return BoxControler.getInstance().getCurrentPlayListFromBox();
	}

	public List<TrackMeta> getLovePlayList(String arg0,int arg1,int arg2){
		return MediaDatabase.getInstance(ctx).getLoveList();
	}


	public List<TrackMeta> getMyPlayList(String arg0,int arg1,int arg2){
		MediaDatabase mdb = MediaDatabase.getInstance(ctx);
		return mdb.getMediaByPlaylist(arg0);
	}
	
	public List<TrackMeta> getLocalSongs(String arg0,int arg1,int arg2){
		MediaDatabase mdb = MediaDatabase.getInstance(ctx);
		Map<String, Media> map = mdb.getMedias();
		Set<String> keys = map.keySet();
		List<TrackMeta> list = new ArrayList<TrackMeta>();
		for (Iterator<String> key = keys.iterator(); key.hasNext();) {
			TrackMeta tm = new TrackMeta();
			String url = key.next();
			Media m = map.get(url);
			tm.setArtist(m.getArtist());
			tm.setPlayUrl(mControler.getLocalSongPath() + url);
			tm.setSource(Source.SOURCE_LOCAL);
			tm.setName(m.getTitle());
			Log.d(TAG, "-------->" + tm);
			list.add(tm);
		}
		return list;
	}
}
