package com.eryiche.frame.util.security;

/**
 * 16进制相关的方法
 * @author EX-XIAOFANQING001
 *
 */
public abstract class Hex {
	
	/**
	 * 把byte数组转换成16进制字符串
	 * @param data 要转换的字节数组
	 * @return 返回转换后的字符串
	 */
	public static String bytesToHexString(byte[] data) {
		
		// 如果字节数组为空则直接返回空
		if (data == null || data.length == 0) {
			return null;
		}
		
		// byte数组转换成字符串后的长度是原来字节数组的一倍
		char[] chars = new char[data.length << 1];
		
		// 循环字节把字节的高位和低位拆开放入一个新的数组当中
		byte b = 0;
		int j =0;
		for (int i=0; i<data.length; i++) {
			b = data[i];
			chars[j++] =  (char) ((b & 0xf0) >> 4); // 高位
			chars[j++] = (char) (b & 0x0f); // 低位
		}

		// 把字节转换成16进制的字符并拼凑成16进制的字符串
		StringBuffer buffer = new StringBuffer();
		for (int k=0; k<chars.length; k++) {
			buffer.append(Integer.toHexString(chars[k]));
		}
		return buffer.toString();
	}
	
	/**
	 * 把16进制的字符串还原成二进制的字节数组
	 * @param hexString 16进制的字符串
	 * @return 返回还原后的byte数组
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.trim().length() == 0) {
			return null;
		}
		
		// 还原后的字节数组长度是字符串的一半
		byte[] bytes = new byte[hexString.length() >> 1];
		
		//  每次取两个字符转换成数字后，分别作为字节的高位和低位重新组合成一个字节
		int j=0;
		for (int i=0; i<hexString.length(); i++) {
			// 取第一个字符作为字节的高位
			char c1 = hexString.charAt(i);
			byte gw = (byte) Character.digit(c1, 16); // 高位
			
			// 取第二个字节作为字节的低位
			i++;
			char c2 = hexString.charAt(i);
			byte dw = (byte) Character.digit(c2, 16);
			
			// 高位左移4位后和低位做与运算组合成字节
			bytes[j] = (byte) ((gw << 4) | dw);
			
			// 组合成一个字节后指针加1
			j++;
		}
		
		// 返回转换后的字节数组
		return bytes;
	}
}
