/* 
 * @Title:  TTFMAction.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-11 下午4:05:21 
 * @version:  V1.0 
 */
package com.xhk.wifibox.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xhk.wifibox.model.ParterTag;
import com.xhk.wifibox.partner.PartnerUtils;
import com.xhk.wifibox.track.Album;
import com.xhk.wifibox.track.TrackMeta;
import com.xhk.wifibox.utils.Contants;
import com.xhk.wifibox.utils.JSONUtil;
import com.xhk.wifibox.utils.MD5;
import com.xhk.wifibox.utils.NetUtils;
import com.xhk.wifibox.utils.Util;

/**
 * @author tang
 * 
 */
public class TTFMAction implements XHKAction {
	private final String TAG = this.getClass().getSimpleName();

	private int subCategoryType = 0;

	public TTFMAction(Context ctx) {

	}

	public TTFMAction() {

	}

	public List<ParterTag> getSubCateogryList(String api, String id) {
		List<ParterTag> result = new ArrayList<ParterTag>();
		Map<String, String> params = new TreeMap<String, String>();
		params.put("key", PartnerUtils.TTFM_KEY);
		if (!"0".equals(id)) {
			params.put("id", id);
		}
		String strParams = Util.getParamsStr(params);
		String sign = MD5
				.getMessageDigest((api + "_" + strParams + "_" + PartnerUtils.TTFM_SECRET)
						.getBytes());
		String strResult = NetUtils.getInstance().getJSONDataByGet(
				PartnerUtils.TTFM_API_URL + api + "?" + strParams + "&sign="
						+ sign);

		try {
			JSONObject json = new JSONObject(strResult);
			JSONObject data = JSONUtil.getJSONObject(json, "data");
			JSONArray arrCategoryList = getSubCategoryArr(data);
			if (arrCategoryList != null) {
				int type = getTypeByApi(api);
				for (int i = 0; i < arrCategoryList.length(); i++) {
					JSONObject cate = arrCategoryList.getJSONObject(i);
					ParterTag tag = new ParterTag();
					tag.id = JSONUtil.getString(cate, "id");
					tag.name = JSONUtil.getString(cate, "name");
					tag.type = type;
					result.add(tag);
				}
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}

		return result;
	}

	private int getTypeByApi(String api) {
		if (PartnerUtils.TTFM_TING_AREACATEGORY.equals(api)) {
			return Contants.TTFM_TAG_TYPE_LIVE;
		} else if (PartnerUtils.TTFM_TING_LIVENETWORK.equals(api)) {
			return Contants.TTFM_TAG_TYPE_LIVE;
		} else if (PartnerUtils.TTFM_TING_PODCASTCATEGORY.equals(api)) {
			return Contants.TTFM_TAG_TYPE_PODCAST;
		} else {
			return Contants.TTFM_TAG_TYPE_CATEGORY;
		}
	}

	/**
	 * @param data
	 * @return
	 */
	private JSONArray getSubCategoryArr(JSONObject data) {

		JSONArray arrCategoryList = null;
		if (data.has("area_category_list")) {
			arrCategoryList = JSONUtil.getJSONArray(data, "area_category_list");
		} else if (data.has("podcast_category")) {
			arrCategoryList = JSONUtil.getJSONArray(data, "podcast_category");
		} else if (data.has("sub_category_list")) {
			arrCategoryList = JSONUtil.getJSONArray(data, "sub_category_list");
		}
		return arrCategoryList;
	}

	public List<ParterTag> getMusicCategoryList() {
		List<ParterTag> result = new ArrayList<ParterTag>();
		Map<String, String> params = new TreeMap<String, String>();
		params.put("key", PartnerUtils.TTFM_KEY);
		String strParams = Util.getParamsStr(params);
		String sign = MD5.getMessageDigest((PartnerUtils.TTFM_MUSIC_CATEGORY
				+ "_" + strParams + "_" + PartnerUtils.TTFM_SECRET).getBytes());
		String strResult = NetUtils.getInstance().getJSONDataByGet(
				PartnerUtils.TTFM_API_URL + PartnerUtils.TTFM_MUSIC_CATEGORY
						+ "?" + strParams + "&sign=" + sign);

		try {
			JSONObject json = new JSONObject(strResult);
			JSONObject data = JSONUtil.getJSONObject(json, "data");
			JSONArray arrCategoryList = JSONUtil.getJSONArray(data,
					"category_list");
			for (int i = 0; i < arrCategoryList.length(); i++) {
				JSONObject cate = arrCategoryList.getJSONObject(i);
				ParterTag tag = new ParterTag();
				tag.id = JSONUtil.getString(cate, "id");
				tag.name = JSONUtil.getString(cate, "name");
				tag.type = Contants.TTFM_TAG_TYPE_MUSIC;
				result.add(tag);
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}
		return result;
	}

	public List<TrackMeta> getMusicList(String listId, int pageSize,
			int pageIndex) {
		List<TrackMeta> result = new ArrayList<TrackMeta>();
		Map<String, String> params = new TreeMap<String, String>();
		params.put("key", PartnerUtils.TTFM_KEY);
		params.put("id", listId);
		String strParams = Util.getParamsStr(params);
		String sign = MD5.getMessageDigest((PartnerUtils.TTFM_MUSIC_LIVELIST
				+ "_" + strParams + "_" + PartnerUtils.TTFM_SECRET).getBytes());
		String strResult = NetUtils.getInstance().getJSONDataByGet(
				PartnerUtils.TTFM_API_URL + PartnerUtils.TTFM_MUSIC_LIVELIST
						+ "?" + strParams + "&sign=" + sign);

		handleResult(result, strResult);
		return result;

	}

	public List<TrackMeta> getLiveAreaList(String listId, int pageSize,
			int pageIndex) {
		List<TrackMeta> result = new ArrayList<TrackMeta>();
		Map<String, String> params = new TreeMap<String, String>();
		params.put("key", PartnerUtils.TTFM_KEY);
		params.put("area_id", listId);
		params.put("page", "1");
		String strParams = Util.getParamsStr(params);
		String sign = MD5.getMessageDigest((PartnerUtils.TTFM_TING_LIVEAREA
				+ "_" + strParams + "_" + PartnerUtils.TTFM_SECRET).getBytes());
		String strResult = NetUtils.getInstance().getJSONDataByGet(
				PartnerUtils.TTFM_API_URL + PartnerUtils.TTFM_TING_LIVEAREA
						+ "?" + strParams + "&sign=" + sign);

		handleResult(result, strResult);
		return result;

	}

	public List<TrackMeta> getLiveNetWorkList(String listId, int pageSize,
			int pageIndex) {
		List<TrackMeta> result = new ArrayList<TrackMeta>();
		Map<String, String> params = new TreeMap<String, String>();
		params.put("key", PartnerUtils.TTFM_KEY);
		params.put("page",String.valueOf(pageIndex));
		String strParams = Util.getParamsStr(params);
		String sign = MD5.getMessageDigest((PartnerUtils.TTFM_TING_LIVENETWORK
				+ "_" + strParams + "_" + PartnerUtils.TTFM_SECRET).getBytes());
		String strResult = NetUtils.getInstance().getJSONDataByGet(
				PartnerUtils.TTFM_API_URL + PartnerUtils.TTFM_TING_LIVENETWORK
						+ "?" + strParams + "&sign=" + sign);

		handleResult(result, strResult);
		return result;

	}

	public List<Album> getCategoryAlbumList(String listId, int pageSize,
			int pageIndex) {
		List<Album> result = new ArrayList<Album>();
		Map<String, String> params = new TreeMap<String, String>();
		params.put("key", PartnerUtils.TTFM_KEY);
		params.put("id", listId);
		params.put("page", String.valueOf(pageIndex));
		String strParams = Util.getParamsStr(params);
		String sign = MD5.getMessageDigest((PartnerUtils.TTFM_TING_CATEGORY_ALBUM
				+ "_" + strParams + "_" + PartnerUtils.TTFM_SECRET).getBytes());
		String strResult = NetUtils.getInstance().getJSONDataByGet(
				PartnerUtils.TTFM_API_URL + PartnerUtils.TTFM_TING_CATEGORY_ALBUM
						+ "?" + strParams + "&sign=" + sign);

		handleAlbumResult(result, strResult);
	
		return result;

	}
	
	public List<Album> getPodcastAlbumList(String listId, int pageSize,
			int pageIndex) {
		List<Album> result = new ArrayList<Album>();
		Map<String, String> params = new TreeMap<String, String>();
		params.put("key", PartnerUtils.TTFM_KEY);
		params.put("id", listId);
		params.put("page", String.valueOf(pageIndex));
		String strParams = Util.getParamsStr(params);
		String sign = MD5.getMessageDigest((PartnerUtils.TTFM_TING_PODCAST_ALBUM
				+ "_" + strParams + "_" + PartnerUtils.TTFM_SECRET).getBytes());
		String strResult = NetUtils.getInstance().getJSONDataByGet(
				PartnerUtils.TTFM_API_URL + PartnerUtils.TTFM_TING_PODCAST_ALBUM
						+ "?" + strParams + "&sign=" + sign);

		handleAlbumResult(result, strResult);
	
		return result;

	}

	/**
	 * @param result
	 * @param strResult
	 */
	private void handleAlbumResult(List<Album> result, String strResult) {
		try {
			JSONObject json = new JSONObject(strResult);
			JSONObject data = JSONUtil.getJSONObject(json, "data");
			JSONArray arrFMList = JSONUtil.getJSONArray(data, "album_list");
			for (int i = 0; i < arrFMList.length(); i++) {
				JSONObject fm = arrFMList.getJSONObject(i);
				Album bean = new Album();
				bean.setId(JSONUtil.getLong(fm, "album_id"));
				bean.setDesc(JSONUtil.getString(fm, "recommendation"));
				bean.setTitle(JSONUtil.getString(fm, "name"));
				bean.setLogoUrl(JSONUtil.getString(fm, "cover_url"));
				result.add(bean);
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}
	}
	
	/**
	 * @param result
	 * @param strResult
	 */
	private void handleResult(List<TrackMeta> result, String strResult) {
		try {
			JSONObject json = new JSONObject(strResult);
			JSONObject data = JSONUtil.getJSONObject(json, "data");
			JSONArray arrFMList = JSONUtil.getJSONArray(data, "fm_list");
			for (int i = 0; i < arrFMList.length(); i++) {
				JSONObject fm = arrFMList.getJSONObject(i);
				TrackMeta bean = new TrackMeta();
				bean.setPlayUrl(JSONUtil.getString(fm, "live_url"));
				bean.setName(JSONUtil.getString(fm, "name"));
				bean.setCoverUrl(JSONUtil.getString(fm, "cover_url"));
				result.add(bean);
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}
	}

	public List<ParterTag> getTingCategoryList() {
		List<ParterTag> result = new ArrayList<ParterTag>();
		Map<String, String> params = new TreeMap<String, String>();
		params.put("key", PartnerUtils.TTFM_KEY);
		String strParams = Util.getParamsStr(params);
		String sign = MD5.getMessageDigest((PartnerUtils.TTFM_TING_CATEGORY
				+ "_" + strParams + "_" + PartnerUtils.TTFM_SECRET).getBytes());
		String strResult = NetUtils.getInstance().getJSONDataByGet(
				PartnerUtils.TTFM_API_URL + PartnerUtils.TTFM_TING_CATEGORY
						+ "?" + strParams + "&sign=" + sign);

		try {
			JSONObject json = new JSONObject(strResult);
			JSONObject data = JSONUtil.getJSONObject(json, "data");
			JSONArray arrCategoryList = JSONUtil.getJSONArray(data,
					"category_list");
			for (int i = 0; i < arrCategoryList.length(); i++) {
				JSONObject cate = arrCategoryList.getJSONObject(i);
				ParterTag tag = new ParterTag();
				tag.id = JSONUtil.getString(cate, "id");
				tag.name = JSONUtil.getString(cate, "name");
				String type = JSONUtil.getString(cate, "category");
				tag.nextApi = JSONUtil.getString(cate, "next_api");
				if (PartnerUtils.TTFM_TING_AREACATEGORY.equals(tag.nextApi)) {
					tag.type = Contants.TTFM_TAG_TYPE_LIVE;
				} else if (PartnerUtils.TTFM_TING_LIVENETWORK.equals(tag.nextApi)) {
					tag.type = Contants.TTFM_TAG_TYPE_NETLIVE;
				}  else if (PartnerUtils.TTFM_TING_LIVEAREA.equals(tag.nextApi)) {
					tag.type = Contants.TTFM_TAG_TYPE_LIVE;
				} else if (PartnerUtils.TTFM_TING_SUBCATEGORY.equals(tag.nextApi)) {
					tag.type = Contants.TTFM_TAG_TYPE_CATEGORY;
				} else if ("userfm".equals(type)) {
					tag.type = Contants.TTFM_TAG_TYPE_USERFM;
				}
				result.add(tag);
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}
		return result;
	}



	public List<TrackMeta> getCollectSongs(String collectId, int pageSize,
			int pageIndex) {
		List<TrackMeta> result = new ArrayList<TrackMeta>();
		Map<String, String> params = new TreeMap<String, String>();
		params.put("key", PartnerUtils.TTFM_KEY);
		params.put("album_id", collectId);
		String strParams = Util.getParamsStr(params);
		String sign = MD5.getMessageDigest((PartnerUtils.TTFM_TING_ALBUM_AUDIOS
				+ "_" + strParams + "_" + PartnerUtils.TTFM_SECRET).getBytes());
		String strResult = NetUtils.getInstance().getJSONDataByGet(
				PartnerUtils.TTFM_API_URL + PartnerUtils.TTFM_TING_ALBUM_AUDIOS
						+ "?" + strParams + "&sign=" + sign);
		try {
			JSONObject json = new JSONObject(strResult);
			JSONObject data = JSONUtil.getJSONObject(json, "data");
			JSONArray arrAudioList = JSONUtil.getJSONArray(data, "audio_list");
			for (int i = 0; i < arrAudioList.length(); i++) {
				JSONObject audio = arrAudioList.getJSONObject(i);
				TrackMeta bean = new TrackMeta();
				bean.setName(JSONUtil.getString(audio, "name"));
				bean.setCoverUrl(JSONUtil.getString(audio, "cover_url"));
				bean.setDuration(JSONUtil.getInt(audio, "duration"));
				bean.setPlayUrl(JSONUtil.getString(audio, "hls"));
				if(TextUtils.isEmpty(bean.getPlayUrl())){	//先优先m3u8,没有就m4a
					bean.setPlayUrl(JSONUtil.getString(audio, "m4a"));
				}
				bean.setId(JSONUtil.getString(audio, "vod_id"));
				result.add(bean);
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}
	
		return result;
		
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xhk.wifibox.action.XHKAction#searchSong(java.lang.String, int,
	 * int)
	 */
	@Override
	public List<TrackMeta> searchSong(String key, int pageSize, int pageIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.xhk.wifibox.action.XHKAction#getSong()
	 */
	@Override
	public TrackMeta getSong() {
		// TODO Auto-generated method stub
		return null;
	}

}
