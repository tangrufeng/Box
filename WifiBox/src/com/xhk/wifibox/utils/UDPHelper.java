/* 
 * @Title:  UDPHelper.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-10-23 下午3:30:34 
 * @version:  V1.0 
 */
package com.xhk.wifibox.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.xhk.wifibox.box.Box;
import com.xhk.wifibox.box.BoxCache;

/**
 * @author tang
 * 
 */
public class UDPHelper {
	private final static String TAG = UDPHelper.class.getSimpleName();

	public final static String ACTION_FIND_BOX_BY_NAME = "ACTION_FIND_BOX_BY_NAME";
	public final static String EXTRA_FIND_BOX = "EXTRA_FIND_BOX";
	private static UDPHelper helper = null; // 私有单例
	public boolean isThreadDisable = true;// 指示监听线程是否终止
	private OnFindBoxListener listener = null;
	private WifiManager.MulticastLock lock;

	private Context ctx;
	private Integer port = 0x1f1f; // UDP服务器监听的端口
	// private DatagramSocket dSocket = null;
	private String sendMsg = "{\"Req\":\"ScanDevice\"}";
	private byte[] receiveMsg = new byte[400];// 接收的字节大小，客户端发送的数据不能超过这个大小
	private boolean keepOn = true;

	public final static UDPHelper getHelper(Context ctx) {
		if (helper == null) {
			helper = new UDPHelper(ctx);
		}
		return helper;
	}

	private UDPHelper() {

	}

	private UDPHelper(Context ctx) {
		this.ctx = ctx;
		WifiManager wm = (WifiManager) ctx
				.getSystemService(Context.WIFI_SERVICE);
		lock = wm.createMulticastLock("XHKUDPLOCK");

	}

	public void setOnFindBoxListener(OnFindBoxListener listener) {
		this.listener = listener;
	}

	/**
	 * 寻找指定名称的音响， 如果找到音响，则会发送action为ACTION_FIND_BOX_BY_NAME的广播，
	 * 并将找到的音响(Box)通过EXTRA_FIND_BOX传递过来
	 * 
	 * @param boxName
	 */
	public void scanBoxByName(final String boxName) {
		if (TextUtils.isEmpty(boxName)) {
			return;
		}

		try {
			final DatagramSocket dSocket= new DatagramSocket(null);
			dSocket.setReuseAddress(true);
			dSocket.bind(new InetSocketAddress(port));
			InetAddress ia = InetAddress.getByName("255.255.255.255");
			final DatagramPacket sendPacket = new DatagramPacket(
					sendMsg.getBytes(), sendMsg.length(), ia, port);

			new Thread(new Runnable() { // 启动个线程一直发送监听广播，直到找到指定的Box
						@Override
						public void run() {
							Log.d(TAG,
									"=====>Begin scan for box which name is '"
											+ boxName + "'");
							keepOn=true;
							do {
								try {
									dSocket.send(sendPacket);
									Thread.sleep(2000);
								} catch (Exception e) {
									// Log.e(TAG, e.getLocalizedMessage(), e);
								}
							} while (keepOn);
						}
					}).start();

			final DatagramPacket receivePacket = new DatagramPacket(receiveMsg,
					receiveMsg.length);
			lock.acquire();

			new Thread(new Runnable() { // 启动个线程一直发送监听广播，直到找到指定的Box
						@Override
						public void run() {
							try {
								lock.acquire();
								do {
									Log.d(TAG, "begin receiveing====>");
									dSocket.receive(receivePacket);
									String strMsg = new String(receivePacket
											.getData()).trim();
									Log.d(TAG, receivePacket.getAddress()
											.getHostAddress().toString()
											+ ":" + strMsg);

									Box box = Box.buildBox(strMsg);
									if (box != null) {
										if (boxName.equals(box.deviceName)) {
											BoxCache.getCache().addBox(box);
											keepOn = false;
											Log.d(TAG,
													"=====>the box is found: "
															+ box);
											Intent intent = new Intent(
													ACTION_FIND_BOX_BY_NAME);
											intent.putExtra(EXTRA_FIND_BOX, box);
											ctx.sendBroadcast(intent);
											if (listener != null) {
												listener.afterFound(box);
											}
										}
									}
								} while (keepOn);
								lock.release();
							} catch (Exception e) {
								 Log.e(TAG, e.getLocalizedMessage(), e);
							} finally {
								if (dSocket != null) {
									dSocket.close();
								}
							}
						}
					}).start();
			lock.release();
		} catch (Exception e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		} finally {
		}

	}

	public void scanAllBox() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				DatagramSocket dSocket = null;
				try {
					dSocket = new DatagramSocket(null);
					dSocket.setReuseAddress(true);
					dSocket.bind(new InetSocketAddress(port));
					// 建立Socket连接
					InetAddress ia = InetAddress.getByName("255.255.255.255");
					DatagramPacket sendPacket = new DatagramPacket(sendMsg
							.getBytes(), sendMsg.length(), ia, port);

					dSocket.send(sendPacket);
					dSocket.send(sendPacket);
					dSocket.send(sendPacket);
					DatagramPacket receivePacket = new DatagramPacket(
							receiveMsg, receiveMsg.length);
					lock.acquire();
					while (isThreadDisable) {
						// 准备接收数据
						Log.d(TAG, "准备接收");
						dSocket.receive(receivePacket);
						String strMsg = new String(receivePacket.getData())
								.trim();
						Log.d(TAG, receivePacket.getAddress().getHostAddress()
								.toString()
								+ ":" + strMsg);
						Box box = Box.buildBox(strMsg);
						if (box != null) {
							BoxCache.getCache().addBox(box);
						}
					}
					lock.release();
				} catch (Exception e) {
					 Log.e(TAG, e.getLocalizedMessage(), e);
				} finally {
					if (dSocket != null) {
						dSocket.close();
					}
				}

			}
		}).start();
	}

	public interface OnFindBoxListener {
		public void afterFound(Box box);
	}
}
