package com.xhk.wifibox.utils;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Locale;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.util.EntityUtils;

import com.xhk.wifibox.box.BoxControler;
import com.xhk.wifibox.box.DFVManager;

import android.util.Log;

/**
 * Basic, yet fully functional and spec compliant, HTTP/1.1 file server.
 * <p>
 * Please note the purpose of this application is demonstrate the usage of
 * HttpCore APIs. It is NOT intended to demonstrate the most efficient way of
 * building an HTTP file server.
 * 
 * 
 */
public class HttpServer {
	private static final String TAG = HttpServer.class.getSimpleName();

	public static void start(int port) {
		Thread t;
		try {
			t = new RequestListenerThread(port);
			t.setDaemon(false);
			t.start();
		} catch (IOException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		} // start the webservice server
	}

	static class WebServiceHandler implements HttpRequestHandler {

		public WebServiceHandler() {
			super();
		}

		public void handle(final HttpRequest request,
				final HttpResponse response, final HttpContext context)
				throws HttpException, IOException {
			Log.d(TAG, "====uri======" + request.getRequestLine().getUri());

			String method = request.getRequestLine().getMethod()
					.toUpperCase(Locale.ENGLISH);
			// get uri
			String target = request.getRequestLine().getUri();
			if (target == null) {
				return;
			} else if (method.equals("GET")) {
				response.setStatusCode(HttpStatus.SC_OK);
				if ("/playlist.json".equals(target)) {
					File file = new File(BoxControler.getInstance()
							.getPlaylistJsonFilePath());
					Log.d(TAG,
							"===" + file.getAbsolutePath() + "==="
									+ file.exists() + "===" + file.length());
					FileEntity entity = new FileEntity(file, "application/json");
					response.setEntity(entity);
					response.setHeader("content-length",
							String.valueOf(file.length()));
				} else if (target.startsWith("/song/")) {
					String localPath = Util.getLocalPath(target);
					File file = new File(URLDecoder.decode(localPath));
					Log.d(TAG,
							"===" + file.getAbsolutePath() + "==="
									+ file.exists() + "===" + file.length());
					FileEntity entity = new FileEntity(file,
							"application/octet-stream");
					response.setEntity(entity);
					response.setHeader("content-length",
							String.valueOf(file.length()));
				} else if ("/version".equals(target)) {
					String filePath = DFVManager.getManager()
							.getLocalVersionFile();
					if (filePath != null) {
						File file = new File(filePath);
						if (file != null) {
							Log.d(TAG, "===" + file.getAbsolutePath() + "==="
									+ file.exists() + "===" + file.length());
							FileEntity entity = new FileEntity(file,
									"application/octet-stream");
							response.setEntity(entity);
							response.setHeader("content-length",
									String.valueOf(file.length()));
						}
					}
				}
			} else {
				throw new MethodNotSupportedException(method
						+ " method not supported");
			}
		}

	}

	static class RequestListenerThread extends Thread {

		private final ServerSocket serversocket;
		private final HttpParams params;
		private final HttpService httpService;

		public RequestListenerThread(int port) throws IOException {
			//
			this.serversocket = new ServerSocket(port);

			// Set up the HTTP protocol processor
			HttpProcessor httpproc = new BasicHttpProcessor();

			this.params = new BasicHttpParams();
			this.params
					.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
					.setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE,
							8 * 1024)
					.setBooleanParameter(
							CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
					.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
					.setParameter(CoreProtocolPNames.ORIGIN_SERVER,
							"HttpComponents/1.1");

			// Set up request handlers
			HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
			reqistry.register("*", new WebServiceHandler()); // WebServiceHandler用来处理webservice请求。

			this.httpService = new HttpService(httpproc,
					new DefaultConnectionReuseStrategy(),
					new DefaultHttpResponseFactory());
			httpService.setParams(this.params);
			httpService.setHandlerResolver(reqistry); // 为http服务设置注册好的请求处理器。

		}

		@Override
		public void run() {
			Log.d(TAG, "Listening on port " + this.serversocket.getLocalPort());
			Log.d(TAG, "Thread.interrupted = " + Thread.interrupted());
			while (!Thread.interrupted()) {
				try {
					// Set up HTTP connection
					Socket socket = this.serversocket.accept();
					DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
					Log.d(TAG,
							"Incoming connection from "
									+ socket.getInetAddress());
					conn.bind(socket, this.params);

					// Start worker thread
					Thread t = new WorkerThread(this.httpService, conn);
					t.setDaemon(true);
					t.start();
				} catch (InterruptedIOException ex) {
					break;
				} catch (IOException e) {
					Log.e(TAG,
							"I/O error initialising connection thread: "
									+ e.getMessage(), e);
					break;
				}
			}
		}
	}

	static class WorkerThread extends Thread {

		private final HttpService httpservice;
		private final HttpServerConnection conn;

		public WorkerThread(final HttpService httpservice,
				final HttpServerConnection conn) {
			super();
			this.httpservice = httpservice;
			this.conn = conn;
		}

		@Override
		public void run() {
			System.out.println("New connection thread");
			HttpContext context = new BasicHttpContext(null);
			try {
				while (!Thread.interrupted() && this.conn.isOpen()) {
					this.httpservice.handleRequest(this.conn, context);
				}
			} catch (ConnectionClosedException ex) {
				System.err.println("Client closed connection");
			} catch (IOException ex) {
				System.err.println("I/O error: " + ex.getMessage());
			} catch (HttpException ex) {
				System.err.println("Unrecoverable HTTP protocol violation: "
						+ ex.getMessage());
			} finally {
				try {
					this.conn.shutdown();
				} catch (IOException ignore) {
				}
			}
		}
	}
}