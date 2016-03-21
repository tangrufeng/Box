package com.eryiche.frame.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

import com.eryiche.frame.dataparser.DataParser;
import com.eryiche.frame.dataparser.DataParserFactory;
import com.eryiche.frame.dataparser.TreeHashMap;

/**
 * http请求工具类
 * @author EX-XIAOFANQING001
 *
 */
public class HttpUtil {
	
	public static final String LOG_TAG = HttpUtil.class.getSimpleName();
	
	private static int mConnectTimeout = 20000;
	private static int mReadTimeout = 20000;
	
	/**
	 * 数据解析器
	 */
	private static DataParser mDataParser = DataParserFactory.createDataParser(DataParserFactory.DATA_PARSER_XML);
	
	/**
	 * 设置超时时间
	 * @param connectTimeout
	 */
	public static void setTimeout(int connectTimeout) {
		mConnectTimeout = connectTimeout;
		mReadTimeout = connectTimeout;
	}
	
	static boolean isEmtyParam(Map<String, String> params) {
		if (params == null || params.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
    /**
     * 构造并返回HTTPClient实体
     */
    private static HttpClient getHttpClient() {
    	
        // 创建 HttpParams 以用来设置 HTTP 参数（这一部分不是必需的）
        HttpParams httpParams = new BasicHttpParams();

        // 设置连接超时和 Socket 超时，以及 Socket 缓存大小
        HttpConnectionParams.setConnectionTimeout(httpParams, mConnectTimeout);
        HttpConnectionParams.setSoTimeout(httpParams, mReadTimeout);
        HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
        
        // 设置重定向，缺省为 true
        HttpClientParams.setRedirecting(httpParams, true);
        String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
        HttpProtocolParams.setUserAgent(httpParams, userAgent);
        
        return new MyHttpClient(httpParams);
    }
	
	public static Response doPostRequest(String urlStr, Map<String, String> params) {
		
		Response response = new Response();
        
		HttpUriRequest httpRequest = null;
		
		if (isEmtyParam(params)) {
			httpRequest = new HttpGet(urlStr);
		} else {
			  // 全都使用Post请求来发送
			httpRequest = new HttpPost(urlStr);
			httpRequest.setHeader("connection", "keep-alive");
	        
	        // 上送参数
	        if (params != null && params.size() > 0) {
	        	List<NameValuePair> paramsPair = convertMapValuesToPairs(params);
		        if (!paramsPair.isEmpty()) {
		        	try {
		        		((HttpPost)httpRequest).setEntity(new UrlEncodedFormEntity(convertMapValuesToPairs(params), HTTP.UTF_8));
					} catch (UnsupportedEncodingException e) {
						// ignore
					}
		        }
	        }
		}

        try {
        	
        	HttpClient mHttpClient = getHttpClient();
        	
        	// 发起网络请求
			HttpResponse httpResponse = null;
			httpResponse = mHttpClient.execute(httpRequest);
			
			  // 状态码为200表示请求成功 
	        int statusCode = httpResponse.getStatusLine().getStatusCode();
	        
	        switch (statusCode) {
	        case 200:
	        	
	        	// 读取返回的数据
	        	InputStream is =  null;
	        	ByteArrayOutputStream baos = null;
	        	
	        	try {
		        	is = httpResponse.getEntity().getContent();
		        	baos = new ByteArrayOutputStream();
		        	
		        	byte[] buffer = new byte[1024];
		        	int readLen = 0;
		        	
		        	while ((readLen = is.read(buffer)) != -1) {
		        		baos.write(buffer, 0, readLen);
		        	}
		        	
		        	baos.flush();
		            // 处理返回结果
		            byte[] responseBytes = baos.toByteArray();
					
					Log.d(LOG_TAG, "success:" );
					
					// 统一把返回数据按utf-8的编码方式组装成文本
					response.setState(Response.STATE_SUCCESS);
					response.setResultData(responseBytes);
					
					return response;
		            
	        	} finally {
	        		try {
		        		if (is != null) {
		        			is.close();
		        		}
		        		
		        		if (baos != null) {
		        			baos.close();
		        		}
	        		} catch (IOException e) {
	        			// ignore
	        		}
	        	}
	        default:
	        	Log.d(LOG_TAG, "server error");
				// 服务器返回非200
				response.setState(Response.STATE_SERVER_ERROR);
				return response;
	        }
	        
		} catch (ClientProtocolException e) {
			Log.i(LOG_TAG, "", e);
			response.setState(Response.STATE_UNKNOW_URL);
			return response;
		} catch (IOException e) {
			Log.i(LOG_TAG, "", e);
			response.setState(Response.STATE_NETWORK_ERROR);
			return response;
		}
		
	}
	
	/**
	 * 发送一个http请求,返回请求的字符串
	 * @param urlStr 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Response doPostRequestWithString(String urlStr, Map<String, String> params) {
		return doPostRequestWithString(urlStr, params, "utf-8");
	}
	
	/**
	 * 工具方法，用来将MAP转换成NameValuePair列表
	 * @param mapParams
	 * @return
	 */
	private static  List<NameValuePair> convertMapValuesToPairs(Map<String, String> mapParams) {
		List<NameValuePair> pairParams = new ArrayList<NameValuePair>();
		if (mapParams == null || mapParams.isEmpty()) {
			return pairParams;
		}
		
		for (String key : mapParams.keySet()) {
			String value = mapParams.get(key);
			BasicNameValuePair nvp = new BasicNameValuePair(key, value); 
			pairParams.add(nvp);
		}
		
		return pairParams;
	}
	
	/**
	 * 发送一个http请求,返回请求的字符串
	 * @param urlStr 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Response doPostRequestWithString(String urlStr, Map<String, String> params, String charset) {
		Response response = doPostRequest(urlStr, params);
		if (response.getState() == Response.STATE_SUCCESS) {
			try {
				response.setResultData(new String((byte[])response.getResultData(), charset));
			} catch (UnsupportedEncodingException e) {
				response.setState(Response.STATE_UNSUPPORT_EDENCODING);
			}
		}
		return response;
	}
	
	/**
	 * 发送一个http请求，返回解析后的xml数据
	 * @param urlStr 请求的URL地址
	 * @param params 请求参数
	 * @return 返回服务器返回的并且解析后的XML数据
	 * @throws Exception
	 */
	public static Response doPostRequestWithParser(String urlStr, Map<String, String> params) {
		Response result = doPostRequestWithString(urlStr, params);
		
		if (result.getState() == Response.STATE_SUCCESS) {
			String resultStr = (String) result.getResultData();
			TreeHashMap<String, Object> data = null;
			try {
				data = mDataParser.parseData(resultStr.getBytes());
			} catch (Exception e) {
				result.setState(Response.STATE_PARSER_ERROR);
			}
			result.setResultData(data);
		}
		
		return result;
	}
	
	
	/**
	 * Http返回的结果对象
	 * @author EX-XIAOFANQING001
	 *
	 */
	public static class Response {
		public static final int STATE_SUCCESS = 1;
		public static final int STATE_UNKNOW_URL = 2;
		public static final int STATE_NETWORK_ERROR = 3;
		public static final int STATE_SERVER_ERROR = 4;
		public static final int STATE_PARSER_ERROR = 5;
		public static final int STATE_UNSUPPORT_EDENCODING = 6;
		
		private int state;
		
		private Object resultData;
		
		public Response() {
		}
		
		public Response(int state) {
			this.state = state;
		}
		
		public Response(int state, Object data) {
			this.state  = state;
			this.resultData = data;
		}

		public void setState(int state) {
			this.state = state;
		}

		public int getState() {
			return state;
		}

		public void setResultData(Object resultData) {
			this.resultData = resultData;
		}

		public Object getResultData() {
			return resultData;
		}

		@Override
		public String toString() {
			return "Response [state=" + state + ", resultData=" + resultData
					+ "]";
		}
		
	}
	
	
	/**
     * 自定义支持https服务器的HttpClient
     * @author ex-xiaofanqing001
     *
     */
	private static class MyHttpClient extends DefaultHttpClient {
		public MyHttpClient(HttpParams httpParams) {
			super(httpParams);
		}

		@Override
		protected ClientConnectionManager createClientConnectionManager() {
			// TODO 是否支持信任所有证书的的开关
			boolean isTestEnv = true;
			
			if (isTestEnv) {
				SchemeRegistry schemeRegistry = new SchemeRegistry();
				
				schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
				schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
				
				ClientConnectionManager connManager = new ThreadSafeClientConnManager(getParams(), schemeRegistry);
				return connManager;
			} else {
				return super.createClientConnectionManager();
			}
		}
	}
	
	static class EasySSLSocketFactory implements SocketFactory, LayeredSocketFactory {  
	    
		private SSLContext sslcontext = null;  
	    
	    private static SSLContext createEasySSLContext() throws IOException {  
	        try {  
	            SSLContext context = SSLContext.getInstance("TLS");  
	            context.init(null, new TrustManager[] { new EasyX509TrustManager(null)}, null);  
	            return context;  
	        } catch (Exception e) {  
	            throw new IOException(e.getMessage());  
	        }  
	    }  
	  
	    private SSLContext getSSLContext() throws IOException {  
	        if (this.sslcontext == null) {  
	            this.sslcontext = createEasySSLContext();  
	        }  
	        return this.sslcontext;  
	    }  
	  
	     
	    public Socket connectSocket(Socket sock, String host, int port,  
	            InetAddress localAddress, int localPort, HttpParams params)  
	            throws IOException, UnknownHostException, ConnectTimeoutException {  
	    	
	        int connTimeout = HttpConnectionParams.getConnectionTimeout(params);  
	        int soTimeout = HttpConnectionParams.getSoTimeout(params);  
	  
	        InetSocketAddress remoteAddress = new InetSocketAddress(host, port);  
	        SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket());  
	  
	        if ((localAddress != null) || (localPort > 0)) {  
	            // we need to bind explicitly  
	            if (localPort < 0) {  
	                localPort = 0; // indicates "any"  
	            }  
	            InetSocketAddress isa = new InetSocketAddress(localAddress, localPort);  
	            sslsock.bind(isa);  
	        }  
	  
	        sslsock.connect(remoteAddress, connTimeout);  
	        sslsock.setSoTimeout(soTimeout);  
	        return sslsock;  
	  
	    }  
	  
	     
	    public Socket createSocket() throws IOException {  
	        return getSSLContext().getSocketFactory().createSocket();  
	    }  
	  
	     
	    public boolean isSecure(Socket socket) throws IllegalArgumentException {  
	        return true;  
	    }  
	  
	     
	    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {  
	        return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);  
	    }  
	  
	    public boolean equals(Object obj) {  
	        return ((obj != null) && obj.getClass().equals(EasySSLSocketFactory.class));  
	    }  
	  
	    public int hashCode() {  
	        return EasySSLSocketFactory.class.hashCode();  
	    }
	    
	
	    static class EasyX509TrustManager implements X509TrustManager {  
	        private X509TrustManager standardTrustManager = null;  
	         
	        public EasyX509TrustManager(KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException {  
	            super();  
	            
	            TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());  
	            factory.init(keystore);  
	            TrustManager[] trustmanagers = factory.getTrustManagers();  
	            
	            if (trustmanagers.length == 0) {  
	                throw new NoSuchAlgorithmException("no trust manager found");  
	            }  
	            this.standardTrustManager = (X509TrustManager) trustmanagers[0];  
	        }  
	         
	        public void checkClientTrusted(X509Certificate[] certificates, String authType) throws CertificateException {  
	            standardTrustManager.checkClientTrusted(certificates, authType);  
	        }  
	      
	         
	        public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException {  
	            if ((certificates != null) && (certificates.length == 1)) {  
	                certificates[0].checkValidity();  
	            } else {  
	                standardTrustManager.checkServerTrusted(certificates, authType);  
	            }  
	        }  
	         
	        public X509Certificate[] getAcceptedIssuers() {  
	            return this.standardTrustManager.getAcceptedIssuers();  
	        }  
	    }
	}
}
