/* 
 * @Title:  XMLYAction.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-9-10 下午4:29:00 
 * @version:  V1.0 
 */
package com.xhk.wifibox.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;
import com.xhk.wifibox.R;
import com.xhk.wifibox.model.ParterTag;
import com.xhk.wifibox.partner.PartnerUtils;
import com.xhk.wifibox.track.Album;
import com.xhk.wifibox.track.Source;
import com.xhk.wifibox.track.TrackMeta;
import com.xhk.wifibox.utils.Device;
import com.xhk.wifibox.utils.JSONUtil;
import com.xhk.wifibox.utils.NetUtils;

/**
 * @author tang
 * 
 */
public class XMLYAction implements XHKAction {
	private final String TAG = this.getClass().getSimpleName();

	private Context ctx = null;

	/**
	 * @param httpDataHandler
	 */
	public XMLYAction(Context ctx) {
		this.ctx = ctx;
	}

	public List<ParterTag> getCategories() {
		List<ParterTag> result = new ArrayList<ParterTag>();
		String queryUrl = ctx.getString(R.string.url_xmly_categories,
				PartnerUtils.XMLY_I_AM, Device.getDeviceId(ctx));
		String strResult = NetUtils.getInstance().getJSONDataByGet(queryUrl);
		try {
			JSONObject jsonData = new JSONObject(strResult);
			JSONArray jsonArr = JSONUtil.getJSONArray(jsonData, "categories");
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject json = jsonArr.getJSONObject(i);
				ParterTag tag = new ParterTag();
				tag.id = JSONUtil.getString(json, "id");
				tag.name = JSONUtil.getString(json, "title");
				tag.coverlURL = JSONUtil.getString(json, "cover_url");
				result.add(tag);
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}
		return result;
	}

	public List<Album> getCategoryTagAlbums(String cateIdAndTag, int pageSize,
			int pageIndex) {
		String[] strs = cateIdAndTag.split("\\|\\|");
		String cateId = strs[0];
		String tag = strs[1];
		List<Album> list = new ArrayList<Album>();
		String queryUrl = "";
		try {
			queryUrl = ctx.getString(R.string.url_xmly_categories_hot_albums,
					cateId, PartnerUtils.XMLY_I_AM,
					URLEncoder.encode(tag, "UTF-8"), Device.getDeviceId(ctx),
					pageIndex, pageSize);
		} catch (UnsupportedEncodingException e1) {
			Log.e(TAG, e1.getLocalizedMessage(), e1);
		}
		String strResult = NetUtils.getInstance().getJSONDataByGet(queryUrl);
		try {
			JSONObject jsonData = new JSONObject(strResult);
			JSONArray jsonArr = JSONUtil.getJSONArray(jsonData, "albums");
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject json = jsonArr.getJSONObject(i);
				Album bean = new Album();
				bean.setId(JSONUtil.getLong(json, "id"));
				bean.setTitle(JSONUtil.getString(json, "title"));
				bean.setAuthor(JSONUtil.getString(json, "nickname"));
				bean.setLogoUrl(JSONUtil.getString(json, "cover_url_middle"));
				bean.setDesc(JSONUtil.getString(json, "intro"));
				bean.setTag(JSONUtil.getString(json, "tags"));
				bean.setSource(Source.SOURCE_XMLY);
				list.add(bean);
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}

		return list;

	}

	public List<TrackMeta> getCollectSongs(String playListId, int pageSize,
			int pageIndex) {

		List<TrackMeta> list = new ArrayList<TrackMeta>();
		String queryUrl = ctx.getString(R.string.url_xmly_albums_tracks,
				playListId, PartnerUtils.XMLY_I_AM, Device.getDeviceId(ctx),
				pageIndex, pageSize);
		String strResult = NetUtils.getInstance().getJSONDataByGet(queryUrl);
		try {
			JSONObject jsonData = new JSONObject(strResult);
			JSONObject jsonAlbum = JSONUtil.getJSONObject(jsonData, "album");
			JSONArray jsonArr = JSONUtil.getJSONArray(jsonAlbum, "tracks");
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject json = jsonArr.getJSONObject(i);
				TrackMeta bean = new TrackMeta();
				bean.setId(JSONUtil.getString(json, "id"));
				bean.setName(JSONUtil.getString(json, "title"));
				bean.setArtist(JSONUtil.getString(json, "nickname"));
				bean.setCoverUrl(JSONUtil.getString(json, "cover_url_middle"));
				bean.setPlayUrl(JSONUtil.getString(json, "play_url_32"));
				bean.setSource(Source.SOURCE_XMLY);
				bean.setDuration(JSONUtil.getInt(json, "play_size_32"));
				list.add(bean);
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}

		return list;
	}

	public List<ParterTag> getCategoryTag(String cateId) {
		List<ParterTag> result = new ArrayList<ParterTag>();
		String queryUrl = ctx.getString(R.string.url_xmly_categories_tag,
				cateId, PartnerUtils.XMLY_I_AM, Device.getDeviceId(ctx));
		String strResult = NetUtils.getInstance().getJSONDataByGet(queryUrl);
		try {
			JSONObject jsonData = new JSONObject(strResult);
			JSONArray jsonArr = JSONUtil.getJSONArray(jsonData, "tags");
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject json = jsonArr.getJSONObject(i);
				ParterTag tag = new ParterTag();
				tag.id = cateId;
				tag.name = JSONUtil.getString(json, "name");
				tag.coverlURL = JSONUtil.getString(json, "cover_url_small");
				result.add(tag);
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

		List<TrackMeta> list = new ArrayList<TrackMeta>();
		String queryUrl = ctx.getString(R.string.url_xmly_search,
				PartnerUtils.XMLY_I_AM, Device.getDeviceId(ctx), key, pageIndex,
				pageSize);
		Log.d(TAG,queryUrl);
		String strResult = NetUtils.getInstance().getJSONDataByGet(queryUrl);
		Log.d(TAG,strResult);
		if(TextUtils.isEmpty(strResult)||"null".equals(strResult)){
			return list;
		}
		try {
			JSONObject jsonData = new JSONObject(strResult);
			JSONArray jsonArr = JSONUtil.getJSONArray(jsonData, "tracks");
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject json = jsonArr.getJSONObject(i);
				TrackMeta bean = new TrackMeta();
				bean.setId(JSONUtil.getString(json, "id"));
				bean.setName(JSONUtil.getString(json, "title"));
				bean.setArtist(JSONUtil.getString(json, "nickname"));
				bean.setCoverUrl(JSONUtil.getString(json, "cover_url_middle"));
				bean.setPlayUrl(JSONUtil.getString(json, "play_url_32"));
				bean.setSource(Source.SOURCE_XMLY);
				bean.setDuration(JSONUtil.getInt(json, "play_size_32"));
				list.add(bean);
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}

		return list;
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
