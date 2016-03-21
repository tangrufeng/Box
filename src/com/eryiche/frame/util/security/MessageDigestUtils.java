package com.eryiche.frame.util.security;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import com.eryiche.frame.util.LOG;


/**
 * 消息摘要工具类
 * 
 * 提供对串消息摘要的方法和对文件消息摘要的方法
 * 
 * @author EX-XIAOFANQING001
 *
 */
public class MessageDigestUtils {
	
	private static final String TAG = MessageDigestUtils.class.getSimpleName();
	
	public static final String MD5 = "MD5";
	
	public static final String SHA1 = "SHA-1";

	/**
	 * 对数据进行消息摘要
	 * @param data 要进行消息摘要的数据
	 * @param algrothm 消息摘要算法
	 * @return 返回数据的消息摘要
	 * @throws Exception
	 */
	public static byte[] messageDigest(byte[] data, String algorithm) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
		messageDigest.update(data);
		return messageDigest.digest();
	}
	
	/**
	 * 对数据进行SHA1散列
	 * @param data 待hash文本
	 * @return 散列后16进制编码文本
	 */
	public static String MD5(String data) {
		if (data == null || data.length() == 0) {
			return null;
		}
		
		String hash = null;
		try {
			byte[] digestByte = messageDigest(data.getBytes("UTF-8"), MD5);
			hash = Hex.bytesToHexString(digestByte);
		} catch (Exception e) {
			// ignore
			LOG.e(TAG, e.toString());
		} 
		return hash;
	}
	
	/**
	 * 对数据进行SHA1散列
	 * @param data 待hash文本
	 * @return 散列后16进制编码文本
	 */
	public static String SHA1(String data) {
		if (data == null || data.length() == 0) {
			return null;
		}
		
		String hash = null;
		try {
			byte[] digestByte = messageDigest(data.getBytes("UTF-8"), SHA1);
			hash = Hex.bytesToHexString(digestByte);
		} catch (Exception e) {
			// ignore
			LOG.e(TAG, e.toString());
		} 
		return hash;
	}
	
	/**
	 * 使用指定的消息摘要算法对文件进行消息摘要
	 * 
	 * @param file 消息摘要的文件
	 * @param olgrothm 消息摘要算法
	 * @param listener 进度监听器
	 * @return 返回消息摘要后的的byte数组
	 * @throws Exception 
	 */
	public static byte[] fileMessageDigest(File file, String algorithm, OnFileMessageDigestProgressListener listener) throws Exception {
		FileInputStream fin = null; 
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			fin = new FileInputStream(file);
			DigestInputStream dis = new DigestInputStream(fin, md);
		
			long totalSize = file.length();
			
			long readTotalLen = 0;
			
			int lastProgress = 0;
			int progress = 0;
			
			byte[] buffer = new byte[1024];
			int readLen = 0;
			while ((readLen = dis.read(buffer)) != -1) {
				readTotalLen += readLen;
				progress = (int) ((100 * readTotalLen ) / totalSize);
				
				if (progress != lastProgress) {
					lastProgress = progress;
					if (listener != null) {
						listener.onFileMessageDigestProgress(file, progress);
					}
				}
			}
			return dis.getMessageDigest().digest();
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}
	
	/**
	 * 使用指定的消息摘要算法对文件进行消息摘要
	 * @param file 消息摘要的文件
	 * @param olgrothm 消息摘要算法
	 * @return
	 * @throws Exception 
	 */
	public static byte[] fileMessageDigest(File file, String algorithm) throws Exception {
		return fileMessageDigest(file, algorithm, null);
	}
	
	/**
	 * 对文件进行消息摘要的监听器
	 * @author EX-XIAOFANQING001
	 *
	 */
	public static interface OnFileMessageDigestProgressListener {
		public void onFileMessageDigestProgress(File file, int progress);
	}
	
	
	// test
	
	public static void main(String[] args) throws Exception {
		System.out.println(Hex.bytesToHexString(messageDigest("这是原文".getBytes(), "MD5")));
	}
}
