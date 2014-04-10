package com.example.minireader.online;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {

	private static final String TAG = "MyWebViewClient";
	protected ProgressDialog mDialog;
	private Handler mHandler;
	private Context mContext;

	public MyWebViewClient(Handler handler, Context context) {
		mHandler = handler;
		mContext = context;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		Log.i(TAG, "url : " + url);
		if (url.endsWith(".txt") || url.endsWith(".TXT")) {
			Log.i("url end", "url is end with txt");
			DownloadTask task = new DownloadTask(mContext);
			task.execute(url);
			return true;
		} else {
			return super.shouldOverrideUrlLoading(view, url);
		}
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		Log.i(TAG, "finish url : " + url);
		mHandler.sendEmptyMessage(1);
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		
		super.onPageStarted(view, url, favicon);
		Log.i(TAG, "start url : " + url);
	}

	@Override
	public void onScaleChanged(WebView view, float oldScale, float newScale) {
		super.onScaleChanged(view, oldScale, newScale);
		Log.i(TAG, "onScaleChanged " + oldScale + ": " + newScale);
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		super.onReceivedError(view, errorCode, description, failingUrl);
		Log.i(TAG, "onReceivedError ");
	}
}
