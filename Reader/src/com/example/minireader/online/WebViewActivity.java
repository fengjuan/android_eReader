package com.example.minireader.online;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.example.minireader.MyActivityGroupProjectDemo;
import com.example.minireader.R;
import com.example.minireader.entity.BookInfo;
import com.example.minireader.sqlite.DbHelper;

/**
 * 
 * <pre>
 * 涓昏鐢ㄦ潵鍖呰http://m.shupeng.com;
 * 1. 娣诲姞webView 鍙互鎺ュ彈js鍥炶皟
 * 	mWebView.addJavascriptInterface(new JavaScriptInterface(), JS_INTERFACE );
 * 2. 鍥炶皟鏂规硶瑙ｆ瀽
 * 	涓昏鍥炶皟鎺ュ彛 {@link JavaScriptInterface#setBookInfo(String json)} 寰楀埌json瀛楃锟� * 	瑙ｆ瀽json瀛楃涓插緱鍒癇ook鐨勫熀鏈俊鎭拰褰撳墠Book鐨凜hapterId
 * 	骞朵笖鎶婅В鏋愬悗鐨勭粨鏋滄斁鍏ヤ功锟� * 	鍐嶆牴鎹繖浜涗俊鎭嫾鎺ョ綉锟� * 	濡傦細http://m.shupeng.com/#/book/50002260 褰撲腑50002260涓篵ookId
 * 	濡傦細http://m.shupeng.com/#/read/8520000201 褰撲腑 8520000201涓篶hapterId
 * 
 * 鍐嶄娇鐢╓ebView.loadUrl灏卞彲浠ヤ簡
 * </pre>
 */
public class WebViewActivity extends Activity {

	/**
	 * 
	 * 閲嶈鐨勫洖璋冪被 璋冪敤js 鑾峰緱褰撳墠闃呰椤电殑json淇℃伅 鍏蜂綋json 鏍煎紡瀵瑰簲
	 * http://code.google.com/p/shupeng-api/downloads/list
	 */
	BookInfo mBook;
	DbHelper dbHelper = new DbHelper(WebViewActivity.this);
	class JavaScriptInterface {
		public String setBookInfo(String json) {
			//System.out.println("json:" + json);
			JSONObject jo;
			JSONObject bookJo;
			try {
				jo = new JSONObject(json);
				bookJo = jo.getJSONObject("book");
				mBook = new BookInfo();
				int id = bookJo.getInt("id");
				mBook.id = id;
			
				mBook.bookname = bookJo.getString("name");
				System.out.println("bookname:" + mBook.bookname);
				//mBook.setThumb(bookJo.getString("thumb"));
				
				mBook.url = M_Shupeng_URL + "#/book/" + id;
				System.out.println("Url:" + mBook.url);
				//String chapterId = jo.getString("id");
			//	mBook.bookmark = Integer.parseInt(chapterId);
				//mBook.setChapterUrl(M_Shupeng_URL + "#/read/" + chapterId);
				mBook.downloadUrl = M_Shupeng_URL + "#/download/" + id;
				
				dbHelper.insert(mBook);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return json;
		}
	}

	private WebView mWebView;
	private View view;
	private Button mBtnDownload;
	private boolean isBtnDownloadShow = false;
	private String mCurrentUrl;
	
	/**
	 * 娉ㄦ剰: JS_INTERFACE = "jsInterface" 涓哄浐瀹氾拷?锛屼笉鍑嗕慨锟�	 */
	private static final String JS_INTERFACE = "jsInterface";
	private static String M_Shupeng_URL = "http://m.shupeng.com/";
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				view.setVisibility(View.GONE);
				mWebView.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		};
	};

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.brower);
		initUI();
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setDatabaseEnabled(true);
		mWebView.getSettings().setUseWideViewPort(true);	//自适应手机屏幕
		//mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setSupportZoom(true);		//设置支持缩放
		mWebView.getSettings().setBuiltInZoomControls(true);

		mWebView.requestFocus(View.FOCUS_DOWN);
		/**
		 * 娉ㄦ剰JS_INTERFACE 涓哄浐瀹氬�锛屼笉鍑嗕慨鏀�		 *  */
		
		mWebView.addJavascriptInterface(new JavaScriptInterface(), JS_INTERFACE);
		mWebView.setWebViewClient(new MyWebViewClient(mHandler,
				WebViewActivity.this));
		mWebView.setWebChromeClient(new WebChromeClient() {

			public void onConsoleMessage(String message, int lineNumber,
					String sourceID) {
				Log.d("MyApplication", message + " -- From line " + lineNumber
						+ " of " + sourceID);
			}
		});
		String url = getIntent().getStringExtra("url");
		if (TextUtils.isEmpty(url)) {
			mWebView.loadUrl(M_Shupeng_URL);
		} else {
			mWebView.loadUrl(url);
		}

		mWebView.setDownloadListener(new MyWebViewDownLoadListener());
		mBtnDownload.setOnClickListener(new txtDownloadListener());
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		mCurrentUrl = mWebView.getUrl();
		
		if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()
				&& !mCurrentUrl.equals(getString(R.string.m_shupeng_url))) {
			mWebView.goBack();
			return true;
		}
		if(!mWebView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		
		if(keyCode == KeyEvent.KEYCODE_MENU && mCurrentUrl.contains("#/read/") && !isBtnDownloadShow){
			mBtnDownload.setVisibility(View.VISIBLE);
			isBtnDownloadShow = true;
			return true;
		} else {
			mBtnDownload.setVisibility(View.GONE);
			isBtnDownloadShow = false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initUI() {
		mWebView = (WebView) findViewById(R.id.wv_myview);
		view = findViewById(R.id.ll_progress_bar);
		mBtnDownload = (Button) findViewById(R.id.btn_download);
	}

	public void close(View view) {
		finishActivityFromChild(WebViewActivity.this, 0);
	}
	
	public void returnLocalShelf(View view) {
		Intent intent = new Intent(this, MyActivityGroupProjectDemo.class);
		startActivity(intent);
		WebViewActivity.this.finish();
	}

	// 涓嬭浇鐩戝惉鍐呴儴
	private class MyWebViewDownLoadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent,String contentDisposition, String mimetype, long contentLength) {
			if (!Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				Toast t = Toast.makeText(WebViewActivity.this, "锟�锟斤拷SD鍗★拷?",
						Toast.LENGTH_LONG);
				t.setGravity(Gravity.CENTER, 0, 0);
				t.show();
				return;
			}
			DownloadTask task = new DownloadTask(WebViewActivity.this);
			task.execute(url);
		}

	}
	
	//涓嬭浇
	class txtDownloadListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			mWebView.loadUrl(mBook.downloadUrl);
			isBtnDownloadShow = false;
		}
		
	}

}
