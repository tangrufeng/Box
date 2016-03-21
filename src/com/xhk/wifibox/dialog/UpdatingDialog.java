package com.xhk.wifibox.dialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eryiche.frame.util.LOG;
import com.eryiche.frame.util.ThreadPools;
import com.xhk.wifibox.R;
import com.xhk.wifibox.model.UpdateInfo;

/**
 * 更新对话框
 * 
 * @author EX-XIAOFANQING001
 * 
 */
public class UpdatingDialog extends BaseTitleDialog implements
		View.OnClickListener {

	static final String TAG = UpdatingDialog.class.getSimpleName();

	private Handler handler = new Handler();

	private ProgressBar pbUpdating;

	private TextView tvProgress;

	private Button btnCancel;

	private UpdateInfo updateInfo;

	private String updateUrl;
	private String filename;

	// 监听器
	private OnCancelUpdateClickListener onCancelUpdateClickListener;
	private OnUpdateCompleteListener onUpdateCompleteListener;
	private OnUpdateErrorListener onUpdateErrorListener;

	private UpdateDownloadTask updateDownloadTask;

	/**
	 * 构造器
	 * 
	 * @param ctx
	 */
	public UpdatingDialog(Context ctx) {
		this(ctx, null);
	}

	/**
	 * 构造器
	 * 
	 * @param context
	 *            上下文
	 * @param updateInfo
	 *            更新信息
	 */
	public UpdatingDialog(Context context, final UpdateInfo updateInfo) {
		super(context);
		setTitle("版本更新");
		setContentView(LayoutInflater.from(context).inflate(
				R.layout.dialog_updating, null));
		pbUpdating = (ProgressBar) findViewById(R.id.pb_update_progress);
		tvProgress = (TextView) findViewById(R.id.tv_progress);
		btnCancel = (Button) findViewById(R.id.btn_cancel);

		btnCancel.setOnClickListener(this);

		setCancelable(false);

		if (updateInfo != null) {
			try {
				setUpdateInfo(updateInfo);
			} catch (Exception e) {
				// ignore
				LOG.e(TAG, e.toString());
			}
		}
	}

	/**
	 * 工具方法用来根据URL和版本号拿到要下载的文件名
	 * 
	 * @param url
	 * @param versionName
	 * @return
	 */
	public static String getApkFileName(String url) {
		// 本地保存的文件名为，URL最后部分加上版本号
		String fileName = url.substring(url.lastIndexOf("/") + 1);

		int subindex = fileName.indexOf(".apk");
		if (subindex > -1) {
			fileName = fileName.substring(0, subindex);
		}
		return fileName + ".apk";
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_cancel) {
			if (updateDownloadTask != null) {
				updateDownloadTask.cancel();
			}

			if (onCancelUpdateClickListener != null) {
				onCancelUpdateClickListener.onCancelUpdateClick(updateUrl);
			}
			dismiss();
		}
	}

	public void setUpdateInfo(final UpdateInfo updateInfo) {
		if (updateInfo == null) {
			return;
		}

		this.updateInfo = updateInfo;
		updateUrl = updateInfo.appUrl;

		if (updateUrl != null) {
			// 本地保存的文件名为，URL最后部分加上版本号
			this.filename = getApkFileName(updateUrl);
		}
	}

	/**
	 * 开始更新
	 */
	public void startUpdate() {
		Log.i(TAG, "begin start update, version:" + updateInfo.appVersionCode
				+ ", address:" + updateUrl);
		updateDownloadTask = new UpdateDownloadTask();
		ThreadPools.execute(updateDownloadTask);
	}

	public void setOnCancelUpdateClickListener(
			OnCancelUpdateClickListener onCancelUpdateClickListener) {
		this.onCancelUpdateClickListener = onCancelUpdateClickListener;
	}

	public void setOnUpdateCompleteListener(
			OnUpdateCompleteListener onUpdateCompleteListener) {
		this.onUpdateCompleteListener = onUpdateCompleteListener;
	}

	public void setOnUpdateErrorListener(
			OnUpdateErrorListener onUpdateErrorListener) {
		this.onUpdateErrorListener = onUpdateErrorListener;
	}

	/**
	 * 下载线程
	 * 
	 * @author EX-XIAOFANQING001
	 * 
	 */
	private class UpdateDownloadTask implements Runnable {
		/**
		 * 标识当前用户取消了更新线程
		 */
		private transient boolean isCancel;

		/**
		 * 取消更新
		 */
		public void cancel() {
			isCancel = true;
		}

		@SuppressWarnings("resource")
		public void run() {
			InputStream in = null;
			OutputStream out = null;

			HttpEntity entity = null;

			try {
				// 创建要下载的本地文件
				String path = Environment.getExternalStorageDirectory()
						+ "/XHKBOX/";

				LOG.i(TAG, "f:" + path + "=========" + filename);
				final File f = new File(path, filename);
				LOG.i(TAG, "f:" + f.getAbsolutePath());
				File parentDir = f.getParentFile();
				if (!parentDir.exists()) {
					parentDir.mkdirs();
				}

				if (f.exists()) {
					f.delete();
				}
				f.createNewFile();
				Log.i(TAG, "file length:" + f.length());
				out = new FileOutputStream(f, true);

				/* 开始下载文件 */
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(updateUrl + "?num="
						+ System.currentTimeMillis()); // 加上变量，防止读取缓存;
				// HttpPost get = new HttpPost(updateUrl);

				// 断点续传的功能，设置当前已下载的字节到服务器
				get.addHeader("Range", "bytes=" + (f.length()) + "-");

				HttpResponse response = client.execute(get);
				int status = response.getStatusLine().getStatusCode();
				LOG.d(TAG, "state:" + status);

				entity = response.getEntity();
				in = entity.getContent();

				// 文件的总大小
				final long total = entity.getContentLength() + f.length();

				LOG.d(TAG, "total:" + total);

				// 设置初始进度
				handler.post(new Runnable() {
					@Override
					public void run() {
						int progress = (int) (f.length() * 100 / total);
						pbUpdating.setProgress(progress);
						tvProgress.setText(String.valueOf(progress));
					}
				});

				LOG.i(TAG, "total size:" + total);

				long current = f.length();
				long step = total / 100;

				byte buf[] = new byte[1024];
				do {
					/* 判断用户是否已取消下载 */
					if (isCancel) {
						return;
					}

					/* 如果下载过程中用户删除文件,停止下载 */
					if (!f.exists()) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								if (onUpdateErrorListener != null) {
									onUpdateCompleteListener.onUpdateComplete(
											updateUrl, null);
									dismiss();
								}
							}
						});
						return;
					}

					int numread = in.read(buf);
					if (numread <= 0) {
						break;
					}

					current += numread;
					out.write(buf, 0, numread);

					/* 如果下载进度超过1%就通知以下进度 */
					if (current % step < 1024) {
						final int progress = (int) (current * 100 / total);
						LOG.i(TAG, "update progress:" + progress);
						handler.post(new Runnable() {
							public void run() {
								pbUpdating.setProgress(progress);
								tvProgress.setText("("
										+ String.valueOf(progress) + "%)");
							}
						});
					}
				} while (true);

				if (current >= total) {
					Log.i(TAG, "update complete, current:" + current
							+ " total:" + total);
					handler.post(new Runnable() {
						public void run() {
							if (onUpdateCompleteListener != null) {
								onUpdateCompleteListener.onUpdateComplete(
										updateUrl, f);
							}
							dismiss();
						}
					});
				}
			} catch (Exception e) {
				LOG.e(TAG, "", e);
				handler.post(new Runnable() {
					public void run() {
						Toast.makeText(getContext(), "更新出错！",
								Toast.LENGTH_SHORT).show();
						dismiss();

						handler.post(new Runnable() {
							@Override
							public void run() {
								if (onUpdateErrorListener != null) {
									onUpdateErrorListener.OnUpdateError(
											updateUrl, null);
								}
							}
						});
					}
				});
			} finally {
				try {
					if (in != null) {
						in.close();
					}
					if (out != null) {
						out.close();
					}
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	/**
	 * 在更新的过程中，用户点击取消的监听器
	 * 
	 * @author EX-XIAOFANQING001
	 * 
	 */
	public interface OnCancelUpdateClickListener {
		public void onCancelUpdateClick(String url);
	}

	/**
	 * 更新完成监听器
	 * 
	 * @author EX-XIAOFANQING001
	 * 
	 */
	public interface OnUpdateCompleteListener {
		public void onUpdateComplete(String url, File localFile);
	}

	public interface OnUpdateErrorListener {
		public void OnUpdateError(String url, File localFile);
	}

}
