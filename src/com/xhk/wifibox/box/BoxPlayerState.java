/* 
 * @Title:  BoxPlayerState.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-11-2 下午4:26:24 
 * @version:  V1.0 
 */
package com.xhk.wifibox.box;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author tang
 * 
 */
public class BoxPlayerState implements Parcelable {

	public String avTransportURI = "";

	public String currentTrackURI = "";

	public String transportState = "";

	public String currentTrackDuration = "";

	public int numberOfTracks = 0;

	public int currentTrack = 0;

	public String relativeTimePosition = "";

	public int currentVolume = 0;

	public String audioSource = "";

	/**
	 * 正在播放中
	 */
	public final static String BOX_PLAYING="PLAYING";
	
	public BoxPlayerState(){} 
	
	/**
	 * @param source
	 */
	public BoxPlayerState(Parcel source) {

		avTransportURI = source.readString();
		currentTrackURI =source.readString();
		transportState = source.readString();
		currentTrackDuration = source.readString();
		numberOfTracks = source.readInt();
		currentTrack = source.readInt();
		relativeTimePosition = source.readString();
		currentVolume = source.readInt();
		audioSource = source.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(avTransportURI);
		dest.writeString(currentTrackURI);
		dest.writeString(transportState);
		dest.writeString(currentTrackDuration);
		dest.writeInt(numberOfTracks);
		dest.writeInt(currentTrack);
		dest.writeString(relativeTimePosition);
		dest.writeInt(currentVolume);
		dest.writeString(audioSource);

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BoxPlayerState [avTransportURI=" + avTransportURI
				+ ", currentTrackURI=" + currentTrackURI + ", transportState="
				+ transportState + ", currentTrackDuration="
				+ currentTrackDuration + ", numberOfTracks=" + numberOfTracks
				+ ", currentTrack=" + currentTrack + ", relativeTimePosition="
				+ relativeTimePosition + ", currentVolume=" + currentVolume
				+ ", audioSource=" + audioSource + "]";
	}

	public final static Parcelable.Creator<BoxPlayerState> CREATOR = new Parcelable.Creator<BoxPlayerState>() {

		@Override
		public BoxPlayerState createFromParcel(Parcel source) {
			return new BoxPlayerState(source);
		}

		@Override
		public BoxPlayerState[] newArray(int size) {
			// TODO Auto-generated method stub
			return new BoxPlayerState[size];
		}
	};
}
