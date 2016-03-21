package com.xhk.wifibox.track;


public class TrackMeta{


	private String id = "";
	/**
	 * 音频名称
	 */
	private String name = "";

	/**
	 * 音频来源
	 */
	private int source;

	/**
	 * 播放地址
	 */
	private String url = "";

	private int position = 0;

	/**
	 * 封面地址
	 */
	private String coverUrl = "";

	/**
	 * 音频时长
	 */
	private int duration;

	/**
	 * 当前播放进度
	 */
	private int curDuration;
	
	/**
	 * 艺术家
	 */
	private String artist;

	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @param artist
	 *            the artist to set
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * @return the trackName
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param trackName
	 *            the trackName to set
	 */
	public void setName(String trackName) {
		this.name = trackName;
	}

	/**
	 * @return the source
	 * @see Source#SOURCE_LOCAL
	 * @see Source#SOURCE_TTFM
	 * @see Source#SOURCE_XMLY
	 */
	public int getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 * @see Source#SOURCE_LOCAL
	 * @see Source#SOURCE_TTFM
	 * @see Source#SOURCE_XMLY
	 */
	public void setSource(int source) {
		this.source = source;
	}
	

	/**
	 * @return the curDuration
	 */
	public int getCurDuration() {
		return curDuration;
	}

	/**
	 * @param curDuration the curDuration to set
	 */
	public void setCurDuration(int curDuration) {
		this.curDuration = curDuration;
	}

	/**
	 * @return the playUrl
	 */
	public String getPlayUrl() {
		return url;
	}

	/**
	 * @param playUrl
	 *            the playUrl to set
	 */
	public void setPlayUrl(String playUrl) {
		this.url = playUrl;
	}

	/**
	 * @return the coverUrl
	 */
	public String getCoverUrl() {
		return coverUrl;
	}

	/**
	 * @param coverUrl
	 *            the coverUrl to set
	 */
	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	/**
	 * @return the length
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * @param duration
	 *            the length to set
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artist == null) ? 0 : artist.hashCode());
		result = prime * result
				+ ((coverUrl == null) ? 0 : coverUrl.hashCode());
		result = prime * result + (int) (duration ^ (duration >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + position;
		result = prime * result + source;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrackMeta other = (TrackMeta) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TrackMeta [id=" + id + ", name=" + name + ", source=" + source
				+ ", url=" + url + ", position=" + position + ", coverUrl="
				+ coverUrl + ", duration=" + duration + ", artist=" + artist
				+ "]";
	}

	public String toJSONString() {
		return "{\"id\":\"" + id + "\",\"name\":\"" + name + "\",\"url\":\""
				+ url + "\",\"position\":\"" + position + "\",\"duration\":\""
				+ duration + "\",\"artist\":\"" + artist + "\",\"source\":\""
				+ source + "\",\"coverUrl\":\"" + coverUrl + "\"}";
	}
	
//	public static void buildFromBoxJSON(String jsonStr,TrackMeta track){
//		if(track==null){
//			return ;
//		}
//		try {
//			JSONObject jo=new JSONObject(jsonStr);
//			JSONObject body=JSONUtil.getJSONObject(jo, "Body");
//			track.url=JSONUtil.getString(jo, "CurrentTrackURI");
//			track.url=JSONUtil.getString(jo, "CurrentTrackURI");
//			track.url=JSONUtil.getString(jo, "CurrentTrackURI");
//			track.url=JSONUtil.getString(jo, "CurrentTrackURI");
//			
//		} catch (JSONException e) {
//			return;
//		}
//	}

}
