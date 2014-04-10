package com.example.minireader;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minireader.entity.BookInfo;
import com.example.minireader.entity.BookNote;
import com.example.minireader.entity.SetupInfo;
import com.example.minireader.sqlite.BookNoteDBHelper;
import com.example.minireader.sqlite.DbHelper;

public class ReadBookActivity extends Activity {
	/** Called when the activity is first created. */
	public final static int OPENMARK = 0;
	public final static int SAVEMARK = 1;
	public final static int TEXTSET = 2;
	
	private PageWidget mPageWidget;
	private Bitmap mCurPageBitmap, mNextPageBitmap;
	private Canvas mCurPageCanvas, mNextPageCanvas;
	private BookPageFactory mPagefactory;
	private int whichSize=6;//当前的字体大小
	private int txtProgress = 0;//当前阅读的进度
	private final String[] mFont = new String[] {"20","24","26","30","32","36",
			"40","46","50","56","60","66","70"};
	private int mCurPostion;
	private DbHelper mDbHelper; 
	private Context mContext;
	private Cursor mCursor;
	private BookInfo mBook = null; 
	private SetupInfo mSetup = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Display display = getWindowManager().getDefaultDisplay();
		int w = display.getWidth();
		int h = display.getHeight(); 
		System.out.println(w + "\t" + h);
		
		mCurPageBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);
		
		mPagefactory = new BookPageFactory(w, h); 
		mPagefactory.setBgBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.backg1));
		
		//取得传递的参数
		Intent intent = getIntent();
		int bookid = intent.getIntExtra("bookid", 1);
			mContext = this;
			mDbHelper = new DbHelper(mContext);
			try {
				mBook = mDbHelper.getBookInfo(bookid);
				mSetup = mDbHelper.getSetupInfo();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(mBook != null){
				mPagefactory.setFileName(mBook.bookname);
				mPageWidget = new PageWidget(this, w, h);
				setContentView(mPageWidget);
				mPagefactory.openbook(mBook.url);
				int m_mbBufLen = mPagefactory.getBufLen();
				
				if (mBook.bookmark > 0) { 
					whichSize = mSetup.fontsize;
					mPagefactory.setFontSize(Integer.parseInt(mFont[mSetup.fontsize]));
					//pos = String.valueOf(m_mbBufLen*0.1);
					int begin = m_mbBufLen*100/100;
					mPagefactory.setBeginPos(Integer.valueOf(mBook.bookmark));
					try {
						mPagefactory.prePage();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//setContentView(mPageWidget);
					mPagefactory.draw(mNextPageCanvas);
					mPageWidget.setBitmaps(mNextPageBitmap, mNextPageBitmap);
					//mPageWidget.invalidate();
					mPageWidget.postInvalidate();
					mDbHelper.close(); 
				}else{
					mPagefactory.draw(mCurPageCanvas);
					//setContentView(mPageWidget);
					mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);
				} 

				mPageWidget.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent e) {
						boolean ret = false;
						if (v == mPageWidget) {
							if (e.getAction() == MotionEvent.ACTION_DOWN) {
								mPageWidget.abortAnimation();
								mPageWidget.calcCornerXY(e.getX(), e.getY());

								mPagefactory.draw(mCurPageCanvas);
								if (mPageWidget.DragToRight()) {
									try {
										mPagefactory.prePage();
									} catch (IOException e1) {
										e1.printStackTrace();
									}
									if (mPagefactory.isfirstPage()){
										Toast.makeText(mContext, "已经是第一页",Toast.LENGTH_SHORT).show(); 
										return false;
									}
									mPagefactory.draw(mNextPageCanvas);
								} else {
									try {
										mPagefactory.nextPage();
									} catch (IOException e1) {
										e1.printStackTrace();
									}
									if (mPagefactory.islastPage()){
										Toast.makeText(mContext, "已经是最后一页",Toast.LENGTH_SHORT).show();
										return false;
									}
									mPagefactory.draw(mNextPageCanvas);
								}
								mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
							}
							ret = mPageWidget.doTouchEvent(e);
							return ret;
						}
						return false;
					}
				});
			}else{
				Toast.makeText(mContext, "电子书不存在！可能已经删除",Toast.LENGTH_SHORT).show(); 
				ReadBookActivity.this.finish();
			}
			
		//mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);
	}
 
	public boolean onCreateOptionsMenu(Menu menu) {// 创建菜单
		 super.onCreateOptionsMenu(menu);
        //通过MenuInflater将XML 实例化为 Menu Object
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item) {// 操作菜单
		int ID = item.getItemId();
		switch (ID) { 
		case R.id.fontsize:
			new AlertDialog.Builder(this)
			.setTitle("请选择")
			.setIcon(android.R.drawable.ic_dialog_info)                
			.setSingleChoiceItems(mFont, whichSize, 
			  new DialogInterface.OnClickListener() {
			     public void onClick(DialogInterface dialog, int which) {
			    	 dialog.dismiss();
			    	 setFontSize(Integer.parseInt(mFont[which]));
			    	 whichSize = which;
			    	 //Toast.makeText(mContext, "您选中的是"+font[which], Toast.LENGTH_SHORT).show();
			       // dialog.dismiss();
			     }
			  }
			)
			.setNegativeButton("取消", null)
			.show();
			break;
		case R.id.nowprogress:
			LayoutInflater inflater = getLayoutInflater();
			   final View layout = inflater.inflate(R.layout.bar,
			     (ViewGroup) findViewById(R.id.seekbar));
			   SeekBar seek = (SeekBar)layout.findViewById(R.id.seek);
			   final TextView textView = (TextView)layout.findViewById(R.id.textprogress);
			   txtProgress = mPagefactory.getCurProgress();
			   seek.setProgress(txtProgress);
			   textView.setText(String.format(getString(R.string.progress), txtProgress+"%"));
			   seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				   int progressBar = 0;
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						int progressBar = seekBar.getProgress();
						int m_mbBufLen = mPagefactory.getBufLen();
						int pos = m_mbBufLen*progressBar/100;
						if(progressBar == 0){
							pos = 1;
						}
						mPagefactory.setBeginPos(Integer.valueOf(pos));
						try {
							mPagefactory.prePage();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//setContentView(mPageWidget);
						mPagefactory.draw(mCurPageCanvas);
						mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);
						//mPageWidget.invalidate();
						mPageWidget.postInvalidate();
					}
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						//Toast.makeText(mContext, "StartTouch", Toast.LENGTH_SHORT).show();
					}
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						if(fromUser){
							textView.setText(String.format(getString(R.string.progress), progress+"%"));
						}
					}
				});
			   new AlertDialog.Builder(this).setTitle("跳转").setView(layout)
			     .setPositiveButton("确定", 
			    		 new DialogInterface.OnClickListener() {
						     public void onClick(DialogInterface dialog, int which) {
						    	 //Toast.makeText(mContext, "您选中的是", Toast.LENGTH_SHORT).show();
						        dialog.dismiss();
						     }
						  }
			    		 ).show();
			break;
		case R.id.note:
			Intent intent = new Intent(ReadBookActivity.this, NoteAcitvity.class);
			intent.putExtra("bookName", mBook.bookname);
			
			BookNoteDBHelper noteDBHelper = new BookNoteDBHelper(ReadBookActivity.this);
			BookNote note = noteDBHelper.findNoteByBookName(mBook.bookname);
			if(note != null) {
				intent.putExtra("note", note);
			}
			
			ReadBookActivity.this.startActivity(intent);
			break;
			
		case R.id.book_mark:
			break;
		default:
			break;

		}
		return true;
	}
	
	private void setFontSize(int size){
		mPagefactory.setFontSize(size);
		int pos = mPagefactory.getCurPostionBeg();
		mPagefactory.setBeginPos(pos);
		try {
			mPagefactory.nextPage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setContentView(mPageWidget);
		mPagefactory.draw(mNextPageCanvas);
		//mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);
		mPageWidget.setBitmaps(mNextPageBitmap, mNextPageBitmap);
		mPageWidget.invalidate();
		//mPageWidget.postInvalidate();
	}
	  
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		  if (keyCode == KeyEvent.KEYCODE_BACK) {
			  addBookMark();
			  this.finish();
		  }
		  return false;
	}
	//添加书签
	public void addBookMark(){
		Message msg = new Message();
		msg.what = SAVEMARK;
		msg.arg1 = whichSize;
		mCurPostion = mPagefactory.getCurPostion();
		msg.arg2 = mCurPostion;
		mhHandler.sendMessage(msg);
	} 
	Handler mhHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case TEXTSET:
				mPagefactory.changBackGround(msg.arg1);
				mPagefactory.draw(mCurPageCanvas);
				mPageWidget.postInvalidate();
				break;

			case OPENMARK:
				try {
					mCursor = mDbHelper.select();

				} catch (Exception e) {
					e.printStackTrace();
				}
				if (mCursor.getCount() > 0) {
					mCursor.moveToPosition(mCursor.getCount() - 1);
					String pos = mCursor.getString(2);
					String tmp = mCursor.getString(1);
					 
					mPagefactory.setBeginPos(Integer.valueOf(pos));
					try {
						mPagefactory.prePage();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mPagefactory.draw(mNextPageCanvas);
					mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
					mPageWidget.invalidate();
					mDbHelper.close(); 
				}
				break;

			case SAVEMARK:
				try {
					mDbHelper.update(mBook.id, mBook.bookname, String.valueOf(msg.arg2));
					mDbHelper.updateSetup(mSetup.id,String.valueOf(msg.arg1), "0", "0");
					//mCursor = mDbHelper.select();
				} catch (Exception e) {
					e.printStackTrace();
				}
				mDbHelper.close();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
}