/* 
 * @Title:  XMAction.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-9-10 下午4:31:22 
 * @version:  V1.0 
 */
package com.xhk.wifibox.action;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.xhk.wifibox.partner.PartnerUtils;
import com.xhk.wifibox.track.Album;
import com.xhk.wifibox.track.Source;
import com.xhk.wifibox.track.TrackMeta;
import com.xiami.sdk.XiamiSDK;
import com.xiami.sdk.entities.ArtistBook;
import com.xiami.sdk.entities.ArtistRegion;
import com.xiami.sdk.entities.OnlineArtist;
import com.xiami.sdk.entities.OnlineCollect;
import com.xiami.sdk.entities.OnlineRadio;
import com.xiami.sdk.entities.OnlineSong;
import com.xiami.sdk.entities.QueryInfo;
import com.xiami.sdk.entities.RadioCategoryNew;
import com.xiami.sdk.entities.RankList;
import com.xiami.sdk.entities.RankListItem;
import com.xiami.sdk.entities.RankType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author tang
 * 
 */
public class XMAction implements XHKAction {
	private final static String TAG = XMAction.class.getSimpleName();
	private XiamiSDK sdk;

	public XMAction(Context ctx) {
		sdk = new XiamiSDK(ctx, PartnerUtils.XMSDK_KEY,
				PartnerUtils.XMSDK_SECRET);
	}

	@Override
	public List<TrackMeta> searchSong(String key, int pageSize, int pageIndex) {
		Pair<QueryInfo, List<OnlineSong>> results = sdk.searchSongSync(key,
				pageSize, pageIndex);
		if (results == null) {
			return null;
		}
		QueryInfo info = results.first;
		Log.d(TAG,
				"searchSong===QueryInfo.getResultCount()========="
						+ info.getResultCount());
		List<OnlineSong> list = results.second;
		if (list != null) {
			List<TrackMeta> metas = new ArrayList<TrackMeta>(list.size());
			transList(metas, list);
			return metas;
		} else {
			return null;
		}
	}

	public List<Album> getCollectsRecommendSync(int pageSize, int pageIndex) {
		Pair<QueryInfo, java.util.List<OnlineCollect>> results = sdk
				.getCollectsRecommendSync(pageSize, pageIndex);
		List<Album> list = new ArrayList<Album>();
		if (results != null) {
			QueryInfo info = results.first;
			if (info.getResultCount() > 0) {
				List<OnlineCollect> collects = results.second;
				for (Iterator<OnlineCollect> iter = collects.iterator(); iter
						.hasNext(); ) {
					Album album = new Album();
					OnlineCollect temp = iter.next();
					album.setAuthor(temp.getUserName());
					album.setTitle(temp.getCollectName());
					album.setDesc(temp.getDescription());
					album.setId(temp.getListId());
					album.setLogoUrl(temp.getImageUrl());
					list.add(album);
				}
			}
		}

		return list;
	}

	public List<RankListItem> getRankListsSync() {
		List<RankListItem> result = new ArrayList<RankListItem>();
		List<RankList> list = sdk.getRankListsSync();
		if (list != null && list.iterator() != null) {
			for (Iterator<RankList> iter = list.iterator(); iter.hasNext(); ) {
				RankList rl = iter.next();
				if (rl != null && rl.getItems() != null
						&& rl.getItems().iterator() != null) {
					for (Iterator<RankListItem> iter2 = rl.getItems()
							.iterator(); iter2.hasNext(); ) {
						result.add(iter2.next());
					}
				}
			}
		}

		return result;
	}

	public List<RadioCategoryNew> fetchRadioCategorySync() {
		List<RadioCategoryNew> result = new ArrayList<RadioCategoryNew>();
		result.addAll(sdk.fetchRadioCategorySync());
		Log.d(TAG, "=========result.size()========" + result.size());

		return sdk.fetchRadioCategorySync();
	}

	public List<OnlineRadio> getRaidoList(RadioCategoryNew ocn, int pageSize,
			int pageIndex) {
		List<OnlineRadio> result = new ArrayList<OnlineRadio>();
		Pair<QueryInfo, List<OnlineRadio>> pairs = sdk.fetchRadioListSync(ocn,
				pageSize, pageIndex);
		if (pairs.first.getResultCount() > 0) {
			result.addAll(pairs.second);
		}
		return result;

	}

	public List<OnlineArtist> getArtistsSync(ArtistRegion ar, int pageSize,
			int pageIndex) {
		List<OnlineArtist> result = new ArrayList<OnlineArtist>();
		try {
			ArtistBook ab = sdk.fetchArtistBookSync(ar, pageSize, pageIndex);
			result.addAll(ab.getArtists());
		} catch (Exception e) {
			Log.e(TAG, "", e);
		}
		return result;
	}

	public List<TrackMeta> getCollectSongs(String collectId, int pageSize,
			int pageIndex) {
		List<TrackMeta> result = new ArrayList<TrackMeta>();
		OnlineCollect collect = sdk.getCollectDetailSync(Long
				.parseLong(collectId));
		if (collect != null) {
			transList(result, collect.getSongs());
		}
		return result;

	}

	public List<TrackMeta> getRankSongsSync(String rankListItem, int pageSize,
			int pageIndex) {
		List<TrackMeta> result = new ArrayList<TrackMeta>();
		List<OnlineSong> list = sdk.getRankSongsSync(RankType
				.valueOf(rankListItem));
		transList(result, list);
		return result;

	}

	public List<TrackMeta> getArtistSongs(String artistId, int pageSize,
			int pageIndex) {
		List<TrackMeta> result = new ArrayList<TrackMeta>();
		Pair<QueryInfo, List<OnlineSong>> pair = sdk.fetchSongsByArtistIdSync(
				Long.parseLong(artistId), pageSize, pageIndex);

		if (pair.first.getResultCount() > 0) {
			List<OnlineSong> list = pair.second;
			transList(result, list);
		}
		return result;

	}

	/**
	 * @param result
	 * @param list
	 */
	private void transList(List<TrackMeta> result, List<OnlineSong> list) {
		try {
			List<OnlineSong> list2 = sdk.getSongDetailSync(list);
			for (Iterator<OnlineSong> iter = list2.iterator(); iter.hasNext();) {
				TrackMeta t = new TrackMeta();
				OnlineSong song = iter.next();
				song = sdk.findSongByIdSync(song.getSongId(),
						OnlineSong.Quality.H);
				t.setName(song.getSongName());
				t.setArtist(song.getArtistName());
				t.setCoverUrl(song.getImageUrl());
				t.setPlayUrl(song.getListenFile());
				t.setSource(Source.SOURCE_XMYY);
				t.setDuration(song.getLength());
				t.setId(String.valueOf(song.getSongId()));
				result.add(t);
			}
		} catch (Exception e) {
			Log.e(TAG, "", e);
		}
	}

	@Override
	public TrackMeta getSong() {
		return null;
	}
}
