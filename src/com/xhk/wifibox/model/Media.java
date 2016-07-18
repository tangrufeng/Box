/*****************************************************************************
 * Media.java
 * ****************************************************************************
 * Copyright © 2011-2012 VLC authors and VideoLAN
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package com.xhk.wifibox.model;

import android.content.Context;
import android.database.sqlite.SQLiteFullException;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.xhk.wifibox.XHKApplication;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.LibVlcUtil;
import org.videolan.libvlc.TrackInfo;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Locale;

public class Media implements Comparable<Media> {

    public final static String TAG = "XHK/MediaItem";

    public final static HashSet<String> VIDEO_EXTENSIONS;
    public final static HashSet<String> AUDIO_EXTENSIONS;
    public final static String EXTENSIONS_REGEX;
    public final static HashSet<String> FOLDER_BLACKLIST;
    public final static int TYPE_ALL = -1;
    public final static int TYPE_VIDEO = 0;
    public final static int TYPE_AUDIO = 1;
    public final static int TYPE_GROUP = 2;

    static {
        String[] video_extensions = {
                ".3g2", ".3gp", ".3gp2", ".3gpp", ".amv", ".asf", ".avi", ".divx", "drc", ".dv",
                ".f4v", ".flv", ".gvi", ".gxf", ".iso", ".m1v", ".m2v", ".m2t", ".m2ts", ".m4v",
                ".mkv", ".mov", ".mp2", ".mp2v", ".mp4", ".mp4v", ".mpe", ".mpeg",
                ".mpeg1", ".mpeg2", ".mpeg4", ".mpg", ".mpv2", ".mts", ".mtv", ".mxf", ".mxg",
                ".nsv", ".nuv", ".ogm", ".ogv", ".ogx", ".ps", ".rec", ".rm", ".rmvb", ".tod",
                ".ts", ".tts", ".vob", ".vro", ".webm", ".wm", ".wmv", ".wtv", ".xesc"};

        String[] audio_extensions = {
                ".3ga", ".a52", ".aac", ".ac3", ".adt", ".adts", ".aif", ".aifc", ".aiff", ".amr",
                ".aob", ".ape", ".awb", ".caf", ".dts", ".flac", ".it", ".m4a", ".m4p",
                ".mid", ".mka", ".mlp", ".mod", ".mpa", ".mp1", ".mp2", ".mp3", ".mpc", ".mpga",
                ".oga", ".ogg", ".oma", ".opus", ".ra", ".ram", ".rmi", ".s3m", ".spx", ".tta",
                ".voc", ".vqf", ".w64", ".wav", ".wma", ".wv", ".xa", ".xm"};

        String[] folder_blacklist = {
                "/alarms",
                "/notifications",
                "/ringtones",
                "/media/alarms",
                "/media/notifications",
                "/media/ringtones",
                "/media/audio/alarms",
                "/media/audio/notifications",
                "/media/audio/ringtones",
                "/Android/data/"};

        VIDEO_EXTENSIONS = new HashSet<String>();
        for (String item : video_extensions)
            VIDEO_EXTENSIONS.add(item);
        AUDIO_EXTENSIONS = new HashSet<String>();
        for (String item : audio_extensions)
            AUDIO_EXTENSIONS.add(item);

        StringBuilder sb = new StringBuilder(115);
        sb.append(".+(\\.)((?i)(");
        sb.append(video_extensions[0].substring(1));
        for (int i = 1; i < video_extensions.length; i++) {
            sb.append('|');
            sb.append(video_extensions[i].substring(1));
        }
        for (int i = 0; i < audio_extensions.length; i++) {
            sb.append('|');
            sb.append(audio_extensions[i].substring(1));
        }
        sb.append("))");
        EXTENSIONS_REGEX = sb.toString();
        FOLDER_BLACKLIST = new HashSet<String>();
        for (String item : folder_blacklist)
            FOLDER_BLACKLIST.add(android.os.Environment.getExternalStorageDirectory().getPath() + item);
    }

    private final String mLocation;
    /**
     * Metadata from libvlc_media
     */
    protected String mTitle;
    private String mArtist;
    private String mGenre;
    private String mCopyright;
    private String mAlbum;
    private String mTrackNumber;
    private String mDescription;
    private String mRating;
    private String mDate;
    private String mSettings;
    private String mNowPlaying;
    private String mPublisher;
    private String mEncodedBy;
    private String mTrackID;
    private String mArtworkURL;
    private String mFilename;
    private long mTime = 0;
    private int mAudioTrack = -1;
    private int mSpuTrack = -2;
    private long mLength = 0;
    private int mType;
    private int mWidth = 0;
    private int mHeight = 0;
    private Bitmap mPicture;
    private boolean mIsPictureParsed;

    /**
     * Create a new Media
     *
     * @param libVLC A pointer to the libVLC instance. Should not be NULL
     * @param URI    The URI of the media.
     */
    public Media(LibVLC libVLC, String URI) {
        if (libVLC == null)
            throw new NullPointerException("libVLC was null");

        mLocation = URI;

        mType = TYPE_ALL;
        TrackInfo[] tracks = libVLC.readTracksInfo(mLocation);
        extractTrackInfo(tracks);
        try {
            Log.e(TAG, mArtist + ">>>mLocation>>>" + URLDecoder.decode(mLocation, "UTF-8"));
        } catch (Exception e) {
        }
        getTitleFromUrl();
        onlyCNENNUM();
    }

    public Media(String location, long time, long length, int type,
                 Bitmap picture, String title, String artist, String genre, String album,
                 int width, int height, String artworkURL, int audio, int spu) {
        mLocation = location;
        mFilename = null;
        mTime = time;
        mAudioTrack = audio;
        mSpuTrack = spu;
        mLength = length;
        mType = type;
        mPicture = picture;
        mWidth = width;
        mHeight = height;

        mTitle = title;
        mArtist = getValueWrapper(artist, UnknownStringType.Artist);
        mGenre = getValueWrapper(genre, UnknownStringType.Genre);
        mAlbum = getValueWrapper(album, UnknownStringType.Album);
        mArtworkURL = artworkURL;
    }

    /**
     * Uses introspection to read VLC l10n databases, so that we can sever the
     * hard-coded dependency gracefully for 3rd party libvlc apps while still
     * maintaining good l10n in VLC for Android.
     *
     * @param string The default string
     * @param type   Alias for R.string.xxx
     * @return The default string if not empty or string from introspection
     */
    private static String getValueWrapper(String string, UnknownStringType type) {
        if (string != null && string.length() > 0) return string;

        try {
            Class<?> stringClass = Class.forName("org.videolan.vlc.R$string");
            Class<?> utilClass = Class.forName("org.videolan.vlc.Util");

            Integer value;
            switch (type) {
                case Album:
                    value = (Integer) stringClass.getField("unknown_album").get(null);
                    break;
                case Genre:
                    value = (Integer) stringClass.getField("unknown_genre").get(null);
                    break;
                case Artist:
                default:
                    value = (Integer) stringClass.getField("unknown_artist").get(null);
                    break;
            }

            Method getValueMethod = utilClass.getDeclaredMethod("getValue", String.class, Integer.TYPE);
            // Util.getValue(string, R.string.xxx);
            return (String) getValueMethod.invoke(null, string, value);
        } catch (ClassNotFoundException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (NoSuchFieldException e) {
        } catch (NoSuchMethodException e) {
        } catch (InvocationTargetException e) {
        }

        // VLC for Android translations not available (custom app perhaps)
        // Use hardcoded English phrases.
        switch (type) {
            case Album:
                return "Unknown Album";
            case Genre:
                return "Unknown Genre";
            case Artist:
            default:
                return "Unknown Artist";
        }
    }

    private void onlyCNENNUM() {
        String pattern = "[a-zA-Z0-9() \\u4e00-\\u9fa5]+";
        if (TextUtils.isEmpty(mArtist) || !mArtist.matches(pattern)) {
            mArtist = "unknown_artist";
        }
    }

    private void getTitleFromUrl() {
        if (!TextUtils.isEmpty(mLocation)) {
            try {
                String temp = URLDecoder.decode(mLocation, "utf-8");
                int bIndex = temp.lastIndexOf("/");
                int eIndex = temp.lastIndexOf(".");
                mTitle = temp.substring(bIndex + 1, eIndex);
            } catch (UnsupportedEncodingException e) {
            }
        }
    }

    private void extractTrackInfo(TrackInfo[] tracks) {
        if (tracks == null)
            return;

        for (TrackInfo track : tracks) {
            if (track.Type == TrackInfo.TYPE_VIDEO) {
                mType = TYPE_VIDEO;
                mWidth = track.Width;
                mHeight = track.Height;
            } else if (mType == TYPE_ALL && track.Type == TrackInfo.TYPE_AUDIO) {
                mType = TYPE_AUDIO;
            } else if (track.Type == TrackInfo.TYPE_META) {
                mLength = track.Length;
                mTitle = track.Title;
                mArtist = getValueWrapper(track.Artist, UnknownStringType.Artist);
                mAlbum = getValueWrapper(track.Album, UnknownStringType.Album);
                mGenre = getValueWrapper(track.Genre, UnknownStringType.Genre);
                mArtworkURL = track.ArtworkURL;
            }
        }

        /* No useful ES found */
        if (mType == TYPE_ALL) {
            int dotIndex = mLocation.lastIndexOf(".");
            if (dotIndex != -1) {
                String fileExt = mLocation.substring(dotIndex);
                if (Media.VIDEO_EXTENSIONS.contains(fileExt)) {
                    mType = TYPE_VIDEO;
                } else if (Media.AUDIO_EXTENSIONS.contains(fileExt)) {
                    mType = TYPE_AUDIO;
                }
            }
        }
    }

    /**
     * Compare the filenames to sort items
     */
    @Override
    public int compareTo(Media another) {
        return mTitle.toUpperCase(Locale.getDefault()).compareTo(
                another.getTitle().toUpperCase(Locale.getDefault()));
    }

    public String getLocation() {
        return mLocation;
    }

    public void updateMeta() {

    }

    public String getFileName() {
        if (mFilename == null) {
            mFilename = LibVlcUtil.URItoFileName(mLocation);
        }
        return mFilename;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getAudioTrack() {
        return mAudioTrack;
    }

    public void setAudioTrack(int track) {
        mAudioTrack = track;
    }

    public int getSpuTrack() {
        return mSpuTrack;
    }

    public void setSpuTrack(int track) {
        mSpuTrack = track;
    }

    public long getLength() {
        return mLength;
    }

    public int getType() {
        return mType;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public Bitmap getPicture() {
        // mPicture is not null only if passed through
        // the ctor which is deprecated by now.
        if (mPicture == null) {
            BitmapCache cache = BitmapCache.getInstance();
            Bitmap picture = cache.getBitmapFromMemCache(mLocation);
            if (picture == null) {
                /* Not in memcache:
                 * serving the file from the database and
                 * adding it to the memcache for later use.
                 */
                Context c = XHKApplication.getAppContext();
                picture = MediaDatabase.getInstance(c).getPicture(c, mLocation);
                cache.addBitmapToMemCache(mLocation, picture);
            }
            return picture;
        } else {
            return mPicture;
        }
    }

    public void setPicture(Context context, Bitmap p) {
        Log.d(TAG, "Set new picture for " + getTitle());
        try {
            MediaDatabase.getInstance(context).updateMedia(
                    mLocation,
                    MediaDatabase.mediaColumn.MEDIA_PICTURE,
                    p);
        } catch (SQLiteFullException e) {
            // TODO: do something clever
            e.printStackTrace();
        }
        mIsPictureParsed = true;
    }

    public boolean isPictureParsed() {
        return mIsPictureParsed;
    }

    public void setPictureParsed(boolean isParsed) {
        mIsPictureParsed = isParsed;
    }

    public String getTitle() {
        if (mTitle != null && mType != TYPE_VIDEO)
            return mTitle;
        else {
            int end = getFileName().lastIndexOf(".");
            if (end <= 0)
                return getFileName();
            return getFileName().substring(0, end);
        }
    }

    public String getSubtitle() {
        return mType != TYPE_VIDEO ? mArtist + " - " + mAlbum : "";
    }

    public String getArtist() {
        return mArtist;
    }

    public String getGenre() {
        if (mGenre == getValueWrapper(null, UnknownStringType.Genre))
            return mGenre;
        else if (mGenre.length() > 1)/* Make genres case insensitive via normalisation */
            return Character.toUpperCase(mGenre.charAt(0)) + mGenre.substring(1).toLowerCase(Locale.getDefault());
        else
            return mGenre;
    }

    public String getCopyright() {
        return mCopyright;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public String getTrackNumber() {
        return mTrackNumber;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getRating() {
        return mRating;
    }

    public String getDate() {
        return mDate;
    }

    public String getSettings() {
        return mSettings;
    }

    public String getNowPlaying() {
        return mNowPlaying;
    }

    public String getPublisher() {
        return mPublisher;
    }

    public String getEncodedBy() {
        return mEncodedBy;
    }

    public String getTrackID() {
        return mTrackID;
    }

    public String getArtworkURL() {
        return mArtworkURL;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Media [mTitle=" + mTitle + ", mArtist=" + mArtist + ", mGenre="
                + mGenre + ", mCopyright=" + mCopyright + ", mAlbum=" + mAlbum
                + ", mTrackNumber=" + mTrackNumber + ", mDescription="
                + mDescription + ", mRating=" + mRating + ", mDate=" + mDate
                + ", mSettings=" + mSettings + ", mNowPlaying=" + mNowPlaying
                + ", mPublisher=" + mPublisher + ", mEncodedBy=" + mEncodedBy
                + ", mTrackID=" + mTrackID + ", mArtworkURL=" + mArtworkURL
                + ", mLocation=" + mLocation + ", mFilename=" + mFilename
                + ", mTime=" + mTime + ", mAudioTrack=" + mAudioTrack
                + ", mSpuTrack=" + mSpuTrack + ", mLength=" + mLength
                + ", mType=" + mType + ", mWidth=" + mWidth + ", mHeight="
                + mHeight + ", mPicture=" + mPicture + ", mIsPictureParsed="
                + mIsPictureParsed + "]";
    }

    private enum UnknownStringType {Artist, Genre, Album}
}
