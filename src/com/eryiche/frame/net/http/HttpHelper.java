/*
 * 文件名: HttpHelper.java
 * 版    权：  Copyright PingAn Technology All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: EX-XIAOFANQING001
 * 创建时间: 2012-6-1
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.eryiche.frame.net.http;

import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.content.res.Resources;

/**
 * URL工具类
 * @author EX-XIAOFANQING001
 * @date 2012-6-1
 * @version [Android PABank C01, @2012-6-1]
 * @description
 */
public class HttpHelper {
	
	private static String host;

    /**
     * 根据URL的资源ID获取URL串
     * @param ctx
     * @param urlId
     * @return
     */
    public static String getUrlStrByResId(Resources resources, int urlId) {
        return resources.getString(urlId);
    }
    
    /**
     * 获取当前的服务器地址
     * @param ctx
     * @return
     */
    public static String getHost() {
        return host;
    }
    
    /**
     * 如果URL不是以HTTP或HTTPS开头，则在相对URL的地址上加上主机地址
     * @param resources
     * @param relativeUrl
     * @return
     */
    public static String appendHost(String relativeUrl) {
    	 // 2.判断相对地址是否已HTTP或者HTTPS开头
        String urlStr = null;
        if (relativeUrl.toLowerCase().startsWith("http") || relativeUrl.toLowerCase().startsWith("https")) {
            urlStr = relativeUrl;
        } else {
            String host = HttpHelper.getHost();
            urlStr = host + relativeUrl;
        }
    	return urlStr;
    }
    
    /**
     * 译码器
     * @param url 要译的url文件
     * @return 返回utf8编码十六进制字符串
     */
    public static String urlEncode(String url) {
        final String hexChars = "0123456789ABCDEF"; // 16进制符号
        
        StringBuffer sb = new StringBuffer();
        int length = url.length();
        
        for (int i = 0; i < length; i++) {
            char c = url.charAt(i);
            if (c == ' ') {
                sb.append("%20");
                
            // 小于128的ascii符号保留
            } else if (c < 128) {
                sb.append(c);
            
            // 非ascii符号转化成如下形式
            } else {
                
                // 先将该字符转化为字节数组
                String temp = String.valueOf(c);
                byte[] b = new byte[0];
                
                try {
                    b = temp.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                
                // 循环该字节数组,将每个字节都转为%+2位16进制的形式,最后拼接而成
                for (int j = 0; j < b.length; j++) {
                    // 首先拼接一个%用于间隔字节
                    sb.append('%');
                    // 1个字节是8位2进制,即2位16进制,先取高四位然后映射到16进制中的数值
                    sb.append(hexChars.charAt((b[j] & 0xf0) >> 4));
                    // 再取低四位映射到16进制中的数值,至此一个字节的转化工作完成
                    sb.append(hexChars.charAt(b[j] & 0x0f));
                }
            }
        }
        return sb.toString();
    }
    
    /**
     * Https信任所有证书
     */
    public static void trustAllHosts() {
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[]{};
			}

			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			
			}
		}};

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setHost(String host) {
		HttpHelper.host = host;
	}
    
}

