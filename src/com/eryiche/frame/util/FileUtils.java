package com.eryiche.frame.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

public class FileUtils {

	/**
	 * 格式文件大小
	 * 
	 * @param size
	 * @return
	 */
	public static String formatFileSize(long size) {
		String[] units = new String[] { "B", "KB", "M", "G", "T", "P" };
		int mod = 1024;
		int i = 0;
		for (i = 0; size >= mod; i++) {
			size /= mod;
		}
		return Math.round(size) + units[i];
	}

	public static void copyStream(InputStream in, OutputStream out) {
		if (in == null || out == null) {
			return;
		}
		try {
			byte[] buffer = new byte[2048];
			int readLen = 0;
			while ((readLen = in.read(buffer)) != -1) {
				out.write(buffer, 0, readLen);
			}

			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 将Bitmap 图片保存到本地路径，并返回路径
	 * 
	 * @param c
	 * @param mType
	 *            资源类型，参照 MultimediaContentType 枚举，根据此类型，保存时可自动归类
	 * @param fileName
	 *            文件名称
	 * @param bitmap
	 *            图片
	 * @return
	 */
	public static String saveFile(Context c, String fileName, Bitmap bitmap) {
		return saveFile(c, "", fileName, bitmap);
	}

	public static String saveFile(Context c, String filePath, String fileName,
			Bitmap bitmap) {
		byte[] bytes = bitmapToBytes(bitmap);
		return saveFile(c, filePath, fileName, bytes);
	}

	public static byte[] bitmapToBytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	public static String saveFile(Context c, String filePath, String fileName,
			byte[] bytes) {
		String fileFullName = "";
		FileOutputStream fos = null;
		String dateFolder = new SimpleDateFormat("yyyyMMdd", Locale.CHINA)
				.format(new Date());
		try {
			String suffix = "";
			if (filePath == null || filePath.trim().length() == 0) {
				filePath = Environment.getExternalStorageDirectory() + "/YQB/"
						+ dateFolder + "/";
			}
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			File fullFile = new File(filePath, fileName + suffix);
			fileFullName = fullFile.getPath();
			fos = new FileOutputStream(new File(filePath, fileName + suffix));
			fos.write(bytes);
		} catch (Exception e) {
			fileFullName = "";
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					fileFullName = "";
				}
			}
		}
		return fileFullName;
	}
}
