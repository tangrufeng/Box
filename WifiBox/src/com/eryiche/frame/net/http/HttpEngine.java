package com.eryiche.frame.net.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.eryiche.frame.EryicheApplication;
import com.eryiche.frame.action.HttpResponseListener;
import com.eryiche.frame.util.LOG;

/**
 * HTTP 执行引擎 Http内部维护了一个Http请求队列
 * 
 * @author EX-XIAOFANQING001
 * @date 2012-6-1
 * @version [Android PABank C01, @2012-6-1]
 * @description
 */
public final class HttpEngine {

	private static final String TAG = HttpEngine.class.getSimpleName();

	private static int connectTimeout = 5 * 1000;
	private static int readTimeout = 5 * 1000;

	/**
	 * 单例对象
	 */
	private static HttpEngine instance;

	/**
	 * 资源对象
	 */
	private Resources mResources;

	/**
	 * Http请求消息队列的Handler
	 */
	private Handler mHttpRequestTaskHandler;

	private ResponseHandler responseHandler;

	private DefaultHttpClient mHttpClient;

	public static final int REQ_TYPE_SINGLE = 1;
	public static final int REQ_TYPE_CONCURRENT = 2;

	private static int requestType = REQ_TYPE_SINGLE;

	private ExecutorService mExecutorService = Executors.newFixedThreadPool(3);

	/**
	 * 单例模式的对象，不允许在外部实例化该对象
	 */
	private HttpEngine(Context ctx) {
		mResources = ctx.getResources();

		responseHandler = new ResponseHandler();

		// 对象在构造的时候同时创建HTTP请求队列线程
		Thread httpReqThread = new Thread(new HttpRequestTask());
		httpReqThread.setName("HttpReqThread");
		httpReqThread.start();

		getHttpClient();
	}

	/**
	 * HTTP请求队列任务 该任务不停的检查请求队列中是否有请求对象
	 * 
	 * @author EX-XIAOFANQING001
	 * @version [Android PABank C01, 2012-6-1]
	 */
	class HttpRequestTask implements Runnable {
		@SuppressLint("HandlerLeak")
		@Override
		public void run() {

			// 把线程变成消息循环线程
			Looper.prepare();

			// 在消息循环线程中定义处理消息的Handler对象
			mHttpRequestTaskHandler = new Handler() {
				public void handleMessage(android.os.Message msg) {
					final Request request = (Request) msg.obj;
					Log.i(TAG, "------------"+request);
					// 处理连接请求
					if (requestType == REQ_TYPE_SINGLE) {
						handleRequest(request);
					} else {
						mExecutorService.execute(new Runnable() {
							@Override
							public void run() {
								
								handleRequest(request);
							}
						});
					}

				};
			};

			// 把线程进入消息循环
			Looper.loop();
		}
	}

	/**
	 * 获取对象唯一的实例
	 * 
	 * @param ctx
	 * @return
	 */
	public static HttpEngine getInstance() {
		if (instance == null) {
			instance = new HttpEngine(EryicheApplication.getInstance());
		}

		return instance;
	}

	/**
	 * 运行客户端完全定制自己的处理器
	 * 
	 * @param responseHandler
	 */
	public void setResponseHandler(ResponseHandler responseHandler) {
		this.responseHandler = responseHandler;
	}

	/**
	 * 发送HTTP请求
	 * 
	 * @param request
	 */
	public void sendRequest(Request request) {
		if (mHttpRequestTaskHandler != null) {

			// 清除队列中当前排队的消息
			if (request.isRemove()) {
				mHttpRequestTaskHandler.removeMessages(0);
			}

			Message.obtain(mHttpRequestTaskHandler, 0, request).sendToTarget();
		} else {
			throw new RuntimeException("Http Request Task not lunched!");
		}
	}

	/**
	 * 释放消息循环线程
	 */
	public static void release() {
		if (instance != null) {
			if (instance.mHttpRequestTaskHandler != null) {
				instance.mHttpRequestTaskHandler.getLooper().quit();
			}
		}
	}

	/**
	 * 处理发送HTTP请求
	 * 
	 * @param request
	 *            Http请求对象
	 */
	private void handleRequest(Request request) {

		LOG.i(TAG, "begin handle request:" + request);

		// 看看缓存里是否有数据，否则，直接从缓存里取
		Object cachedData = DataCache.getCache(request.getUrlId(),
				request.getConnectionId());
		if (cachedData != null) {
			LOG.i(TAG, "load data in cached..");
			HttpResponseListener listener = request.getHttpResponseListener();
			if (listener != null) {
				listener.response(HttpResponseListener.NET_SUCCESS, cachedData,
						request.getUrlId(), request.getConnectionId());
			}
			return;
		}

		// 首先根据资源ID获取实际的URL字符串
		String relativeUrl = HttpHelper.getUrlStrByResId(mResources,
				request.getUrlId());

		// URL 加上主机地址
		String urlStr = HttpHelper.appendHost(relativeUrl);
		LOG.i(TAG, "begin request:" + urlStr);

		// 对URL 进行编码
		urlStr = HttpHelper.urlEncode(urlStr);

		// Http请求对象
		HttpUriRequest httpRequest = null;

		switch (request.getRequestType()) {
		case Request.RequestType.POST:
			httpRequest = new HttpPost(urlStr);
			// 上送参数
			if (request.getParam() != null) {
				RequestParams requestParams = convertMapValuesToRquestParams(
						request.getParam(), request.getUploadFiles());
				((HttpPost) httpRequest).setEntity(requestParams.getEntity());
			}
			break;
		default:
			Map<String, String> params = request.getParam();
			if (params != null && !params.isEmpty()) {
				StringBuffer sb = new StringBuffer();
				if (urlStr.indexOf('?') == -1) {
					sb.append("?");
				} else {
					sb.append("&");
				}
				Iterator<String> keys = params.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					String value = params.get(key);
					if (value != null || !"".equals(value)) {
						sb.append(key).append("=").append(params.get(key))
								.append("&");
					}
				}
				sb.deleteCharAt(sb.length() - 1);
				urlStr += sb.toString();
			}
			httpRequest = new HttpGet(urlStr);
			break;
		}

		httpRequest.addHeader("connection", "keep-alive");
		List<Header> requestHeaders = request.getHeaders();
		if (requestHeaders != null) {
			for (int i = 0; i < requestHeaders.size(); i++) {
				httpRequest.addHeader(requestHeaders.get(i));
			}
		}

		try {
			// 发起网络请求
			HttpResponse httpResponse = mHttpClient.execute(httpRequest);

			// 状态码为200表示请求成功
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			switch (statusCode) {
			case HttpStatus.SC_OK:

				HttpEntity entity = null;
				HttpEntity temp = httpResponse.getEntity();

				if (temp != null) {
					entity = new BufferedHttpEntity(temp);
					// 读取服务器返回的数据
					byte[] responseBytes = EntityUtils.toByteArray(entity);
					handleRespnseData(request, responseBytes);
					entity.consumeContent();
				}
				break;
			case 404:
				backRequestError(HttpResponseListener.NET_NOT_FUND_404, request);
				break;
			case 500:
				backRequestError(HttpResponseListener.NET_SERVER_ERROR_500,
						request);
				break;
			default:
				backRequestError(
						HttpResponseListener.NET_SERVER_RESPONSE_NOT_200,
						request);
				break;
			}

		} catch (ClientProtocolException e) {
			backRequestError(HttpResponseListener.NET_MALFORMED_URL, request);
			LOG.w(TAG, "ClientProtocolException", e);
		} catch (IOException e) {
			backRequestError(HttpResponseListener.NET_CONNECT_ERROR, request);
			LOG.w(TAG, "IOException", e);
		} finally {
			// 释放资源
			if (httpRequest != null) {
				httpRequest.abort();
			}
		}
	}

	/**
	 * 返回服务器网络连接请求错误给客户端
	 * 
	 * @param code
	 *            错误码
	 * @param request
	 *            当前的数据请求对象
	 */
	private void backRequestError(int code, Request request, byte[] datas) {
		HttpResponseListener listener = request.getHttpResponseListener();
		if (listener != null) {
			// 如果是解析失败，直接把数据返回给页面
			if (code == HttpResponseListener.NET_DATA_PARSER_ERROR) {
				listener.response(code, datas, request.getUrlId(),
						request.getConnectionId());
			} else {
				listener.response(code, null, request.getUrlId(),
						request.getConnectionId());
			}
		}
	}

	/**
	 * 返回服务器网络连接请求错误给客户端
	 * 
	 * @param code
	 *            错误码
	 * @param request
	 *            当前的数据请求对象
	 */
	private void backRequestError(int code, Request request) {
		backRequestError(code, request, null);
	}

	/**
	 * 处理HTTP返回结果
	 * 
	 * @param currentRequest
	 * @param datas
	 */
	private void handleRespnseData(final Request currentRequest,
			final byte[] datas) {
		// 如果当前的URL不需要解析，则直接把二进制返回
		if (UrlUtil.isNoParseUrl(currentRequest.getUrlId())) {
			HttpResponseListener listener = currentRequest
					.getHttpResponseListener();
			if (listener != null) {
				listener.response(HttpResponseListener.NET_SUCCESS, datas,
						currentRequest.getUrlId(),
						currentRequest.getConnectionId());
			}
			return;
		}

		responseHandler.handleRespnseData(currentRequest, datas);
	}

	/**
	 * 工具方法，用来将MAP转换成NameValuePair列表
	 * 
	 * @param mapParams
	 * @return
	 */
	private static RequestParams convertMapValuesToRquestParams(
			Map<String, String> params, Map<String, File> uploadFiles) {

		RequestParams requestParams = null;

		if (params != null) {
			requestParams = new RequestParams(params);
		}

		if (uploadFiles != null) {
			if (requestParams == null) {
				requestParams = new RequestParams();
			}

			for (String key : uploadFiles.keySet()) {
				try {
					requestParams.put(key, uploadFiles.get(key));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					requestParams.remove(key);
				}
			}
		}

		return requestParams;
	}

	/**
	 * 构造并返回HTTPClient实体
	 */
	private void getHttpClient() {
		if (mHttpClient == null) {

			// 创建 HttpParams 以用来设置 HTTP 参数（这一部分不是必需的）
			// HttpParams是一个封装了的map，提供基本类型的put和get操作

			HttpParams httpParams = new BasicHttpParams();

			// 设置连接超时和 Socket 超时，以及 Socket 缓存大小

			// 使用HttpConnectionParams的工具方法给HttpParams设置值
			HttpConnectionParams.setConnectionTimeout(httpParams,
					connectTimeout); // 连接超时
			HttpConnectionParams.setSoTimeout(httpParams, readTimeout); // 读取超时
			HttpConnectionParams.setSocketBufferSize(httpParams, 8192); // 换成大小

			// 设置重定向，缺省为 true
			HttpClientParams.setRedirecting(httpParams, true);

			// useragent请求头
			String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
			HttpProtocolParams.setUserAgent(httpParams, userAgent);

			// 使用HttpParams构建HttpClient对象
			mHttpClient = new MyHttpClient(httpParams);

			// 异常重试机制
			HttpRequestRetryHandler mRetryHandler = new HttpRequestRetryHandler() {
				@Override
				public boolean retryRequest(IOException exception,
						int executionCount, HttpContext context) {
					Log.e(TAG, "retryRequest:" + exception + ", "
							+ executionCount);

					// 设置恢复策略，在发生异常时候将自动重试N次
					if (executionCount >= 3) {
						// 如果超过最大重试次数，那么就不要继续了
						return false;
					}
					if (exception instanceof NoHttpResponseException) {
						// 如果服务器丢掉了连接，那么就重试
						return true;
					}
					if (exception instanceof SSLHandshakeException) {
						// 不要重试SSL握手异常
						return false;
					}
					HttpRequest request = (HttpRequest) context
							.getAttribute(ExecutionContext.HTTP_REQUEST);
					boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
					if (!idempotent) {
						// 如果请求被认为是幂等的，那么就重试
						return true;
					}
					return false;
				}
			};

			((AbstractHttpClient) mHttpClient)
					.setHttpRequestRetryHandler(mRetryHandler);

		}
	}

	public static int getRequestType() {
		return requestType;
	}

	public static void setRequestType(int requestType) {
		HttpEngine.requestType = requestType;
	}

	/**
	 * 自定义支持https服务器的HttpClient
	 * 
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

				schemeRegistry.register(new Scheme("http", PlainSocketFactory
						.getSocketFactory(), 80));
				schemeRegistry.register(new Scheme("https",
						new EasySSLSocketFactory(), 443));

				ClientConnectionManager connManager = new ThreadSafeClientConnManager(
						getParams(), schemeRegistry);
				return connManager;
			} else {
				return super.createClientConnectionManager();
			}
		}
	}
}
