package com.example.minireader.online;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class DownloadTask extends AsyncTask<String, Void, String> {
	private Context mContext;

	public DownloadTask(Context context) {
		mContext = context;
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		String url = params[0];
		Log.i("tag", "url="+url);
		try {
			Log.i("tag", "decode url" + URLDecoder.decode(url,"utf-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String fileName = url.substring(url.lastIndexOf("/") + 1);
		Log.i("tag", "pre" + fileName);
		
		try {
			fileName = URLDecoder.decode(fileName, "utf-8");
			Log.i("tag", "fileName=" + fileName);

			File directory = Environment.getExternalStorageDirectory();
			File file = new File(directory, fileName);
			if (file.exists()) {
				Log.i("tag", "The file has already exists.");
				return fileName;
			}
			
			HttpClient client = new DefaultHttpClient();
			// client.getParams().setIntParameter("http.socket.timeout",3000);//璁剧疆瓒呮椂
			HttpGet get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			System.out.println("ContentType:" + response.getEntity().getContentType() +"锛宔ncodeing:" + response.getEntity().getContentEncoding());
			
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entity = response.getEntity();
				InputStream input = entity.getContent();
				int len = 0;
				byte[] buf = new byte[1024];
				while((len = input.read(buf)) != -1) {
					System.out.println(new String(buf));
				}
				writeToSDCard(fileName, input);

				input.close();
				// entity.consumeContent();
				return fileName;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		closeProgressDialog();
		if (result == null) {
			return;
		}

		File directory = Environment.getExternalStorageDirectory();
		File file = new File(directory, result);
		Log.i("tag", "Path=" + file.getAbsolutePath());

		Intent intent = getFileIntent(file);

		mContext.startActivity(intent);

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		showProgressDialog();
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

	private ProgressDialog mDialog;

	private void showProgressDialog() {
		if (mDialog == null) {
			mDialog = new ProgressDialog(mContext);
			mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 璁剧疆椋庢牸涓哄渾褰㈣繘搴︽潯
			mDialog.setMessage("姝ｅ湪鍔犺浇 锛岃绛夊緟...");
			mDialog.setIndeterminate(false);// 璁剧疆杩涘害鏉℃槸鍚︿负涓嶆槑锟�
			mDialog.setCancelable(true);// 璁剧疆杩涘害鏉℃槸鍚﹀彲浠ユ寜锟�锟斤拷閿彇锟�
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					mDialog = null;
				}
			});
			mDialog.show();

		}
	}

	private void closeProgressDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	public Intent getFileIntent(File file) {
		// Uri uri = Uri.parse("http://m.ql18.com.cn/hpf10/1.pdf");
		Uri uri = Uri.fromFile(file);
		String type = getMIMEType(file);
		Log.i("tag", "type=" + type);
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(uri, type);
		return intent;
	}

	public void writeToSDCard(String fileName, InputStream input) {

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File directory = Environment.getExternalStorageDirectory();
			File file = new File(directory, fileName);
			// if(file.exists()){
			// Log.i("tag", "The file has already exists.");
			// return;
			// }
			try {
				FileOutputStream fos = new FileOutputStream(file);
				byte[] b = new byte[2048];
				int j = 0;
				while ((j = input.read(b)) != -1) {
					fos.write(b, 0, j);
					System.out.println("while");
				}
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("tag", "文件位置" + file.getAbsolutePath());
			Log.i("tag", "下载到SD卡");
		} else {
			Log.i("tag", "NO SDCard.");
		}
	}

	private String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		/* 鍙栧緱鎵╁睍锟�*/
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		/* 渚濇墿灞曞悕鐨勭被鍨嬪喅瀹歁imeType */
		if (end.equals("pdf")) {
			type = "application/pdf";//
		} else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio/*";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video/*";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image/*";
		} else if (end.equals("apk")) {
			/* android.permission.INSTALL_PACKAGES */
			type = "application/vnd.android.package-archive";
		}
		// else if(end.equals("pptx")||end.equals("ppt")){
		// type = "application/vnd.ms-powerpoint";
		// }else if(end.equals("docx")||end.equals("doc")){
		// type = "application/vnd.ms-word";
		// }else if(end.equals("xlsx")||end.equals("xls")){
		// type = "application/vnd.ms-excel";
		// }
		else {
			// /*濡傛灉鏃犳硶鐩存帴鎵撳紑锛屽氨璺冲嚭杞欢鍒楄〃缁欑敤鎴凤拷?锟�*/
			type = "*/*";
		}
		return type;
	}

}
