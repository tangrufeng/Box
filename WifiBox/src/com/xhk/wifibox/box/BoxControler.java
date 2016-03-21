/* 
 * @Title:  PlayControler.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-31 下午5:03:21 
 * @version:  V1.0 
 */
package com.xhk.wifibox.box;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.eryiche.frame.util.PreferenceUtils;
import com.xhk.wifibox.track.Source;
import com.xhk.wifibox.track.TrackMeta;
import com.xhk.wifibox.utils.Contants;
import com.xhk.wifibox.utils.JSONUtil;
import com.xhk.wifibox.utils.NetUtils;
import com.xhk.wifibox.utils.Util;

/**
 * @author tang
 * 
 */
public class BoxControler {

	/**
	 * 新的状态对象
	 */
	public static final String EXTRA_NEW_STAT = "NEW_STAT";
	/**
	 * 成功刷新音响播放状态
	 */
	public static final String ACTION_BOX_PLAY_STAT_SYNC_SUCCESS = "BOX_PLAY_STAT_SYNC_SUCCESS";

	/**
	 * 没ID地址广播
	 */
	public static final String ACTION_BOX_NO_ADDRESS = "ACTION_BOX_NO_ADDRESS";
	private final static String TAG = BoxControler.class.getSimpleName();
	private final static BoxControler instance = new BoxControler();
	private String boxIpAddress;
	private String jsonFilePath = "";
	private int boxPort;
	NetUtils netUtils = NetUtils.getInstance();
	private Context ctx;
	private String localIP;
	private final int BOX_RETURN_OK = 0;
	private final String BOX_RETURN_RESULT = "Result";
	private List<TrackMeta> playList = new ArrayList<TrackMeta>();
	private TrackMeta track = new TrackMeta();
	private String currentPlayListId = "";
	private boolean sync = false;
	// private boolean isPlay = false;
	private BoxCache boxCache = BoxCache.getCache();
	private List<OnBoxPlayStateListener> listeners = new ArrayList<BoxControler.OnBoxPlayStateListener>();

	private BoxControler() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					// for (OnBoxPlayStateListener lis : listeners) {
					// Log.d(TAG, lis.getClass().getSimpleName());
					// }
					if (sync) {
						if (!listeners.isEmpty()) {
							BoxPlayerState state = getBoxPlayerState();
							// Log.d(TAG, "=====>" + state);
							for (OnBoxPlayStateListener listener : listeners) {
								listener.OnStateChanged(state);
							}
						}
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {

					}
				}
			}
		}).start();
	}

	public void addOnBoxPlayStateChangedListener(OnBoxPlayStateListener listener) {
		if (listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void setBoxApiAddress(String ipAddress, int port) {
		this.boxIpAddress = ipAddress;
		this.boxPort = port;
	}

	public static BoxControler getInstance() {
		return instance;
	}

	// public boolean isPlay() {
	// return this.isPlay;
	// }
	//
	// public void setPlayStatus(boolean isPlay) {
	// getCurrentPlayListFromBox();
	// this.isPlay = isPlay;
	// if (isPlay) {
	// play();
	// } else {
	// pause();
	// }
	// }

	private void writeJSONToFile(String str, String fileName) {
		String filePath = Environment.getExternalStorageDirectory()
				+ "/XHKBOX/";
		File path = new File(filePath);
		if (!path.exists()) {
			path.mkdir();
		}
		File file = new File(filePath, fileName);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(str.getBytes());
			this.jsonFilePath = file.getAbsolutePath();
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		} finally {
			if (fos != null) {
				try {
					fos.flush();
				} catch (IOException e) {
					Log.e(TAG, e.getLocalizedMessage(), e);
				}
				try {
					fos.close();
				} catch (IOException e) {
					Log.e(TAG, e.getLocalizedMessage(), e);
				}
			}
		}
	}

	public String getPlaylistJsonFilePath() {
		return this.jsonFilePath;
	}

	private String rep(String str){
		if(str!=null){
			return str.replace("\"", "'");
		}else{
			return str;
		}
	}
	private String getPlayListJSON(List<TrackMeta> playList, TrackMeta tm) {
		if (playList == null || playList.isEmpty()) {
			return "";
		}
		if (tm == null) {
			tm = playList.get(0);
		}
		currentPlayListId = String.valueOf(System.currentTimeMillis());
		StringBuilder sb = new StringBuilder();
		sb.append("{\"FileType\":\"0\",").append("\"classItems\":[{")
				.append("\"begin\":\"").append(playList.indexOf(tm))
				.append("\",\"id\":\"").append(currentPlayListId)
				.append("\",\"module\":\"cycle\",\"musics\":[");
		for (Iterator<TrackMeta> iter = playList.iterator(); iter.hasNext();) {
			TrackMeta t = iter.next();
			int index = playList.indexOf(t);
			sb.append("{\"id\":\"").append(index + 1).append("\",\"name\":\"")
					.append(rep(t.getName())).append("\",\"url\":\"")
					.append(t.getPlayUrl()).append("\",\"position\":")
					.append(index + 1).append(",\"coverUrl\":\"")
					.append(t.getCoverUrl()).append("\",\"artist\":\"")
					.append(rep(t.getArtist())).append("\",\"source\":")
					.append(t.getSource());
			if (t.getSource() == Source.SOURCE_XMYY) {
				sb.append(",\"type\":1}");
			} else {
				sb.append("}");
			}

			if (iter.hasNext()) {
				sb.append(",");
			}
		}
		sb.append("]}]}");
		return sb.toString();
	}

	public void setContext(Context ctx) {
		this.ctx = ctx;
	}

	private String getLocalIP() {
		if (TextUtils.isEmpty(localIP)) {
			WifiManager wifiManager = (WifiManager) ctx
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int ipAddress = wifiInfo.getIpAddress();
			localIP = ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
					+ (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
			;
		}
		return localIP;
	}

	public String getLocalSongPath() {
		return "http://" + getLocalIP() + ":" + Contants.LOCAL_HTTP_PORT
				+ "/song/";
	}

	private void flushCurrent(BoxPlayerState state) {
		if (state == null)
			return;
		String uri = state.avTransportURI;
		if (TextUtils.isEmpty(uri))
			return;
		String boxPLID = uri.substring(uri.lastIndexOf(";") + 1);
		if (!boxPLID.equals(currentPlayListId)) {
			getCurrentPlayListFromBox();
			currentPlayListId = boxPLID;
		}

		synchronized (track) { // 这个时候要锁住它
			if (!playList.isEmpty()) {
				int index = (state.currentTrack - 1);
				if (index >= 0) {
					track = playList.get(index);
					if (track != null) {
						track.setDuration(Util
								.fromHHmmSS(state.currentTrackDuration));
						track.setCurDuration(Util
								.fromHHmmSS(state.relativeTimePosition));
					}
				}
			}
		}
	}

	public void setPlayList(final List<TrackMeta> playList, final TrackMeta tm) {
		this.playList.clear();
		this.playList.addAll(playList);
		if (tm != null) {
			this.track = tm;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				writeJSONToFile(getPlayListJSON(playList, tm), "playlist.json");
				setPlayURI("http://" + getLocalIP() + ":"
						+ Contants.LOCAL_HTTP_PORT + "/playlist.json");
			}
		}).start();
	}

	public TrackMeta getCurrentTrack() {
		return track;
	}

	public List<TrackMeta> getCurrentPlayListFromBox() {
		return getPlayListFromBox(0);
	}

	/**
	 * @param type
	 * @return
	 */
	private List<TrackMeta> getPlayListFromBox(int type) {
		String reqJSON = "{\"Req\":\"GetPlaylist\",\"Body\":{\"Type\":" + type
				+ "}}";
		// Log.d(TAG, reqJSON);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("CMD", "HTTPAPI"));
		params.add(new BasicNameValuePair("JSONREQ", reqJSON));
		JSONObject respJSON = netUtils.getJSONDataByPost(
				getApiAddr(boxCache.getCurrent()), params);

		// Log.d(TAG, reqJSON);
		List<TrackMeta> result = new ArrayList<TrackMeta>();
		if (BOX_RETURN_OK != JSONUtil.getInt(respJSON, BOX_RETURN_RESULT)) {
			return result;
		}
		JSONObject body = JSONUtil.getJSONObject(respJSON, "Body");
		JSONArray items = JSONUtil.getJSONArray(body, "classItems");
		JSONObject list = JSONUtil.getJSONObject(items, 0);
		JSONArray musics = JSONUtil.getJSONArray(list, "musics");
		for (int i = 0; i < musics.length(); i++) {
			TrackMeta tm = new TrackMeta();
			JSONObject jo = JSONUtil.getJSONObject(musics, i);
			tm.setId(JSONUtil.getString(jo, "id"));
			tm.setName(JSONUtil.getString(jo, "name"));
			tm.setArtist(JSONUtil.getString(jo, "artist"));
			tm.setPlayUrl(JSONUtil.getString(jo, "url"));
			tm.setPosition(JSONUtil.getInt(jo, "position"));
			tm.setCoverUrl(JSONUtil.getString(jo, "coverUrl"));
			tm.setSource(JSONUtil.getInt(jo, "source"));
			result.add(tm);
		}

		this.playList.clear();
		this.playList.addAll(result);
		return result;
	}

	public String getCurrentAudioSource() {
		String reqJSON = "{\"Req\":\"GetCurrentAudioSource\"}";
		Log.d(TAG, reqJSON);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("CMD", "HTTPAPI"));
		params.add(new BasicNameValuePair("JSONREQ", reqJSON));
		JSONObject respJSON = netUtils.getJSONDataByPost(
				getApiAddr(boxCache.getCurrent()), params);
		if (BOX_RETURN_OK != JSONUtil.getInt(respJSON, BOX_RETURN_RESULT)) {
			return null;
		}
		JSONObject body = JSONUtil.getJSONObject(respJSON, "Body");
		return JSONUtil.getString(body, "AudioSource");
	}

	public void stopSyncBoxPlayState() {
		this.sync = false;
	}

	public void startSyncBoxPlayState() {
		this.sync = true;
	}

	public BoxPlayerState getBoxPlayerState() {
		String reqJSON = "{\"Req\":\"GetPlayerState\",\"Body\":{\"Variables\":"
				+ "\"AVTransportURI CurrentTrackURI TransportState "
				+ "CurrentTrackDuration NumberOfTracks CurrentTrack "
				+ "RelativeTimePosition CurrentVolume AudioSource\"}}";

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("CMD", "HTTPAPI"));
		params.add(new BasicNameValuePair("JSONREQ", reqJSON));
		JSONObject respJSON = netUtils.getJSONDataByPost(
				getApiAddr(boxCache.getCurrent()), params);
		if (BOX_RETURN_OK != JSONUtil.getInt(respJSON, BOX_RETURN_RESULT)) {
			return null;
		}

		BoxPlayerState s = new BoxPlayerState();
		JSONObject body = JSONUtil.getJSONObject(respJSON, "Body");
		s.avTransportURI = JSONUtil.getString(body, "AVTransportURI");
		s.currentTrackURI = JSONUtil.getString(body, "CurrentTrackURI");
		s.transportState = JSONUtil.getString(body, "TransportState");
		s.currentTrackDuration = JSONUtil.getString(body,
				"CurrentTrackDuration");
		s.numberOfTracks = JSONUtil.getInt(body, "NumberOfTracks");
		s.currentTrack = JSONUtil.getInt(body, "CurrentTrack");
		s.relativeTimePosition = JSONUtil.getString(body,
				"RelativeTimePosition");
		s.currentVolume = JSONUtil.getInt(body, "CurrentVolume");
		s.audioSource = JSONUtil.getString(body, "AudioSource");
		flushCurrent(s); // 顺便刷新一下应用里的播放状态
		Intent i = new Intent(ACTION_BOX_PLAY_STAT_SYNC_SUCCESS);
		i.putExtra(EXTRA_NEW_STAT, s);
		ctx.sendBroadcast(i);
		return s;
	}

	/**
	 * 设置播放URL
	 * 
	 * @param uri
	 * @return
	 */
	public boolean setPlayURI(String url) {
		if (TextUtils.isEmpty(url)) {
			return false;
		}
		String reqJSON = "{\"Req\":\"SetAVTransportURI\",\"Body\":{\"AVTransportURI\":\""
				+ url + "\"}}";
		return sendSimpleContorlInfo(reqJSON, boxCache.getCurrent());
	}

	/**
	 * 设置播放URL
	 * 
	 * @param uri
	 * @return
	 */
	public boolean setPlayURI(TrackMeta track) {
		if (track == null || TextUtils.isEmpty(track.getPlayUrl())) {
			return false;
		}
		String reqJSON = "{\"Req\":\"SetAVTransportURI\",\"Body\":{\"AVTransportURI\":\""
				+ track.getPlayUrl() + "\"}}";
		if (sendSimpleContorlInfo(reqJSON, boxCache.getCurrent())) {
			this.track = track;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 播放
	 * 
	 * @return
	 */
	public boolean play() {
		String reqJSON = "{\"Req\":\"PlayerDoPlay\"}";
		return sendSimpleContorlInfo(reqJSON, boxCache.getCurrent());
	}

	/**
	 * 播放
	 * 
	 * @return
	 */
	public boolean pause() {
		String reqJSON = "{\"Req\":\"PlayerDoPause\"}";
		return sendSimpleContorlInfo(reqJSON, boxCache.getCurrent());
	}

	/**
	 * 快进至
	 * 
	 * @param seekTo
	 *            格式为HH:MM:SS
	 * @return
	 */
	public boolean seek(String seekTo) {
		String reqJSON = "{\"Req\":\"PlayerDoSeek\",\"Body\":{\"Unit\":\"REL_TIME\",\"Target\":\""
				+ seekTo + "\"}}";
		return sendSimpleContorlInfo(reqJSON, boxCache.getCurrent());
	}

	public boolean next() {
		BoxPlayerState state = getBoxPlayerState();
		if (state == null) {
			return false;
		}
		int i = 0;
		if (state.currentTrack != state.numberOfTracks) {
			i = state.currentTrack + 1;
		} else {
			i = 1;
		}
		String reqJSON = "{\"Req\":\"PlayerDoSeek\",\"Body\":{\"Unit\":\"TRACK_NR\",\"Target\":\""
				+ i + "\"}}";
		return sendSimpleContorlInfo(reqJSON, boxCache.getCurrent());
	}

	public boolean previous() {
		BoxPlayerState state = getBoxPlayerState();
		if (state == null) {
			return false;
		}
		int i = 0;
		if (state.currentTrack != 1) {
			i = state.currentTrack - 1;
		} else {
			i = 1;
		}
		String reqJSON = "{\"Req\":\"PlayerDoSeek\",\"Body\":{\"Unit\":\"TRACK_NR\",\"Target\":\""
				+ i + "\"}}";
		return sendSimpleContorlInfo(reqJSON, boxCache.getCurrent());
	}

	public boolean connectAP(String ssid, String pwd, Box box) {
		String reqJSON = "{\"Req\":\"WiFiStaConnect\",\"Body\":{\"WiFiStaSSID\":\""
				+ ssid + "\",\"WiFiStaKey\":\"" + pwd + "\"}}";
		if (sendSimpleContorlInfo(reqJSON, box)) {
			return setNetworkConfig(box);
		} else {
			return false;
		}
	}

	/**
	 * 是否连接到网络
	 * 
	 * @return
	 */
	public String[] getNetworkState(Box box) {
		String reqJSON = "{\"Req\":\"GetNetworkState\"}";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("CMD", "HTTPAPI"));
		params.add(new BasicNameValuePair("JSONREQ", reqJSON));
		JSONObject respJSON = netUtils.getJSONDataByPost(getApiAddr(box),
				params);
		if (respJSON == null) {
			return null;
		}
		JSONObject body = JSONUtil.getJSONObject(respJSON, "Body");
		if (1 == JSONUtil.getInt(body, "DeviceWanConnect", 0)) {
			String[] s = new String[5];
			s[0] = JSONUtil.getString(body, "DeviceWanConnect");
			s[1] = JSONUtil.getString(body, "DeviceWanIp");
			s[2] = JSONUtil.getString(body, "DeviceWanGw");
			s[3] = JSONUtil.getString(body, "DeviceWanMask");
			s[4] = JSONUtil.getString(body, "DeviceWanDNS");
			return s;
		} else {
			return null;
		}
	}

	public boolean setBoxVolume(int volume, Box box) {
		String reqJSON = "{\"Req\":\"PlayerSetVolume\",\"Body\":{\"DesiredVolume\":\""
				+ volume + "\"}}";
		Log.d(TAG, reqJSON);
		return sendSimpleContorlInfo(reqJSON, box);
	}

	public boolean bindHotKey(int keyCode, List<TrackMeta> list) {
		if (keyCode < 1 || keyCode > 6 || list.isEmpty()) {
			return false;
		}

		writeJSONToFile(getPlayListJSON(list, null), "playlist_" + keyCode
				+ ".json");
		String jsonPath = "http://" + getLocalIP() + ":"
				+ Contants.LOCAL_HTTP_PORT + "/playlist.json";
		String reqJSON = "{\"Req\":\"SetupHotKey\",\"Body\":{\"KeyId\":"
				+ keyCode + ",\"BindAudioUrl\":\"" + jsonPath
				+ "\",\"Type\":0}}";
		return sendSimpleContorlInfo(reqJSON, boxCache.getCurrent());
	}

	public boolean renameBox(String name, Box box) {
		if (TextUtils.isEmpty(name)) {
			return false;
		}

		String reqJSON = "{\"Req\":\"SetDeviceBasicConfig\",\"Body\":{\"DeviceName\":\""
				+ name + "\"}}";
		return sendSimpleContorlInfo(reqJSON, box);
	}

	public boolean setAudioDSP(String dspMode,Box box){
		if (TextUtils.isEmpty(dspMode)) {
			return false;
		}

		String reqJSON = "{\"Req\":\"SetDSP\",\"Body\":{\"AudioDSP\":\""
				+ dspMode + "\"}}";
		return sendSimpleContorlInfo(reqJSON, box);
	}
	
	public String[] getDeiviceBasicInfo(Box box) {
		String reqJSON = "{\"Req\":\"GetDeviceBasicConfig\"}";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("CMD", "HTTPAPI"));
		params.add(new BasicNameValuePair("JSONREQ", reqJSON));
		JSONObject respJSON = netUtils.getJSONDataByPost(getApiAddr(box),
				params);
		if (respJSON == null) {
			return null;
		} else {
			JSONObject body = JSONUtil.getJSONObject(respJSON, "Body");
			String[] str = new String[2];
			str[0] = JSONUtil.getString(body, "DeviceName");
			str[1] = JSONUtil.getString(body, "DeviceFiremwareVersion");
			return str;
		}

	}

	public UDiskInfo getUdiskInfo(Box box) {
		UDiskInfo u = new UDiskInfo();
		String reqJSON = "{\"Req\":\"GetUdiskInfo\"}";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("CMD", "HTTPAPI"));
		params.add(new BasicNameValuePair("JSONREQ", reqJSON));
		JSONObject respJSON = netUtils.getJSONDataByPost(getApiAddr(box),
				params);
		if (respJSON == null) {
			return u;
		} else {
			JSONObject body = JSONUtil.getJSONObject(respJSON, "Body");
			u.state = JSONUtil.getInt(body, "State");
			u.size = JSONUtil.getInt(body, "Size");
			u.used = JSONUtil.getInt(body, "Used");
			return u;
		}
	}

	public boolean setNetworkConfig(Box box) {
		String reqJSON = "{\"Req\":\"SetNetworkConfig\",\"Body\":{\"NetworkMode\":\"1\",\"EthMode\":\"0\",\"WlanHotspot\":\"ON\",\"WanMode\":\"DHCP\"}}";
		return sendSimpleContorlInfo(reqJSON, box);
	}

	public List<BoxAP> getBoxWifiList(Box box) {
		List<BoxAP> list = new ArrayList<BoxAP>();
		String reqJSON = "{\"Req\":\"WiFiScan\"}";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("CMD", "HTTPAPI"));
		params.add(new BasicNameValuePair("JSONREQ", reqJSON));
		JSONObject respJSON = netUtils.getJSONDataByPost(getApiAddr(box),
				params);
		if (respJSON == null) {
			return list;
		}
		JSONObject body = JSONUtil.getJSONObject(respJSON, "Body");
		JSONArray apListJSON = JSONUtil.getJSONArray(body, "ApList");
		for (int i = 0; i <= apListJSON.length(); i++) {
			JSONObject jo = JSONUtil.getJSONObject(apListJSON, i);
			BoxAP b = BoxAP.buildBoxAP(jo);
			if (b != null) {
				list.add(b);
			}
		}
		Collections.sort(list);
		return list;
	}

	public boolean restart(Box box) {
		String reqJSON = "{\"Req\":\"RestartDevice\"}";
		return sendSimpleContorlInfo(reqJSON, box);
	}

	/**
	 * @param source
	 *            Enum of "AUX" "USB" "WIFI" "HOTKEYn"
	 * @return
	 */
	public boolean switchAudioSource(String source, Box box) {
		String reqJSON = "{\"Req\":\"SwitchAudioSource\",\"Body\":{\"AudioSource\":\""
				+ source + "\"}}";
		return sendSimpleContorlInfo(reqJSON, box);
	}

	public boolean resumeFactorySettings(Box box) {
		String reqJSON = "{\"Req\":\"RestoreDeviceFactorySettings\"}";
		return sendSimpleContorlInfo(reqJSON, box);
	}

	public boolean upgradeFirmware(Box box) {
		String reqJSON = "{\"Req\":\"UpgradeFirmware\",\"Body\":{\"UpgradeUrl\":\""
				+ PreferenceUtils.getString(Contants.PREF_VERSION_URL) + "\"}}";
		return sendSimpleContorlInfo(reqJSON, box, 2003);
	}

	public int getUpgadeStatus(Box box) {
		String reqJSON = "{\"Req\":\"UpgradeFirmware\"}";
		Log.d(TAG, reqJSON);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("CMD", "HTTPAPI"));
		params.add(new BasicNameValuePair("JSONREQ", reqJSON));
		JSONObject respJSON = netUtils.getJSONDataByPost(getApiAddr(box),
				params);
		Log.d(TAG, respJSON.toString());
		return JSONUtil.getInt(respJSON, BOX_RETURN_RESULT);
	}

	private boolean sendSimpleContorlInfo(String reqJSON, Box box) {
		return sendSimpleContorlInfo(reqJSON, box, BOX_RETURN_OK);
	}

	/**
	 * @param reqJSON
	 * @return
	 */
	private boolean sendSimpleContorlInfo(String reqJSON, Box box, int OKCode) {
		Log.d(TAG, reqJSON);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("CMD", "HTTPAPI"));
		params.add(new BasicNameValuePair("JSONREQ", reqJSON));
		JSONObject respJSON = netUtils.getJSONDataByPost(getApiAddr(box),
				params);
		Log.d(TAG, respJSON.toString());
		return OKCode == JSONUtil.getInt(respJSON, BOX_RETURN_RESULT);
	}

	private String getApiAddr(Box box) {
		if (box == null) {
			Log.d(TAG, boxCache.getAll().toString() + "=======");
			return null;
		}
		return "http://" + box.deviceIpAddr + ":" + box.httpApiPort
				+ "/httpapi.html";
	}

	public interface OnBoxPlayStateListener {
		public void OnStateChanged(BoxPlayerState state);
	}
}
