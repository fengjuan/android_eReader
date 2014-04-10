package com.example.minireader;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.olivephone.sdk.word.demo.api.CommentListener;
import com.olivephone.sdk.word.demo.api.HyperlinkListener;
import com.olivephone.sdk.word.demo.api.NoteListener;
import com.olivephone.sdk.word.demo.api.OliveWordOperator;
import com.olivephone.sdk.word.demo.api.OliveWordView;
import com.olivephone.sdk.word.demo.api.ProgressListener;

/**
 * @author fengjuan, 2014-4-3
 * 
 */

public class WordReadActivity extends Activity implements OnClickListener,
		CommentListener, NoteListener, HyperlinkListener, ProgressListener {

	OliveWordOperator viu;

	EditText searchEditText;
	ArrayList<String> bookmarks;
	Handler handler;

	protected void onCreate(Bundle savedInstanceState) {
		viu = new OliveWordOperator(this, this);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setProgressBarVisibility(true);
		getWindow().setFeatureInt(Window.FEATURE_PROGRESS,
				Window.PROGRESS_VISIBILITY_ON);
		setContentView(R.layout.demo_view);
		Toast.makeText(this, "ÎÄ¼þÄÚÈÝ:"+ getIntent().getData().toString(), Toast.LENGTH_LONG).show();
		OliveWordView view = (OliveWordView) findViewById(R.id.test_view);

		try {
			
			viu.init(view, getIntent().getData());
			viu.start(viu.isEncrypted(), "111");
		} catch (Exception e) {
			e.printStackTrace();
		}
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				setProgress(msg.what * 10);
				super.handleMessage(msg);
			}

		};

		searchEditText = (EditText) findViewById(R.id.edittext1);
		Button bt1 = (Button) findViewById(R.id.button1);
		Button bt2 = (Button) findViewById(R.id.button2);
		Button bt3 = (Button) findViewById(R.id.button3);
		Button bt4 = (Button) findViewById(R.id.button4);
		Button bt5 = (Button) findViewById(R.id.button5);
		Button bt6 = (Button) findViewById(R.id.button6);
		Button bt7 = (Button) findViewById(R.id.button7);
		// Button bt8 = (Button) findViewById(R.id.button8);
		bt1.setOnClickListener(this);
		bt2.setOnClickListener(this);
		bt3.setOnClickListener(this);
		bt4.setOnClickListener(this);
		bt5.setOnClickListener(this);
		bt6.setOnClickListener(this);
		bt7.setOnClickListener(this);
		// bt8.setOnClickListener(this);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void getComment(ArrayList<String[]> comments) {
		for (int i = 0; i < comments.size(); i++) {
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle(comments.get(i)[0]).setMessage(comments.get(i)[1])
					.show();
		}
	}

	@Override
	public void getHyperlink(String hyperlink) {
		if (Uri.parse(hyperlink).getScheme().contains("mailto")) {
			try {
				startActivity(new Intent(Intent.ACTION_SENDTO,
						Uri.parse(hyperlink)));
			} catch (ActivityNotFoundException anfe) {
				Toast.makeText(this, "can't found email application",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(hyperlink)));
		}
	}

	@Override
	public void getNote(SparseArray<String> notes) {
		for (int i = 0; i < notes.size(); i++) {
			AlertDialog.Builder builder = new Builder(this);
			if (notes.keyAt(i) == NoteListener.FOOTNOTE) {
				builder.setTitle("footnote").setMessage(notes.valueAt(i))
						.show();
			} else if (notes.keyAt(i) == NoteListener.ENDNOTE) {
				builder.setTitle("endnote").setMessage(notes.valueAt(i)).show();
			}
		}

	}

	public void goToBookmarks(String name) {
		viu.goToBookmark(name);
	}

	public void listBookmarks() {
		this.bookmarks = viu.listBookmarks();
	}

	@Override
	public void notifyProgress(int progress) {
		handler.sendEmptyMessage(progress);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			viu.GoToTop();
			break;
		case R.id.button2:
			viu.GoToBottom();
			break;
		case R.id.button3:
			listBookmarks();
			if (bookmarks == null) {
				Toast.makeText(this, "no bookmark", Toast.LENGTH_SHORT).show();
			} else {
				AlertDialog.Builder builder = new Builder(this);
				builder.setMessage(Arrays.toString(bookmarks.toArray())).show();
				goToBookmarks(bookmarks.get(0));
			}
			break;
		case R.id.button4:
			viu.setSearchPattern(searchEditText.getEditableText().toString(), 0);
			viu.findNext();
			break;
		case R.id.button5:
			viu.setSearchPattern(searchEditText.getEditableText().toString(), 0);
			viu.findPrev();
			break;
		case R.id.button6:
			viu.setZoom(1.2f);
			break;
		case R.id.button7:
			viu.setZoom(1 / 1.2f);
			break;
		// case R.id.button8:
		// AlertDialog.Builder builder = new Builder(this);
		// builder.setMessage(Arrays.toString(viu.documentStatic())).show();
		// break;
		default:
			break;
		}
	}

}
