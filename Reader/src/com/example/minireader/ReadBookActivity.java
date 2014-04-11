package com.example.minireader;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.example.minireader.adapter.BackgroundSelectAdapter;
import com.example.minireader.entity.BookInfo;
import com.example.minireader.entity.BookMark;
import com.example.minireader.entity.BookNote;
import com.example.minireader.entity.SetupInfo;
import com.example.minireader.sqlite.BookMarkDBHelper;
import com.example.minireader.sqlite.BookNoteDBHelper;
import com.example.minireader.sqlite.DbHelper;
import com.example.minireader.sqlite.SetupInfoDBHelper;

public class ReadBookActivity extends Activity {
	/** Called when the activity is first created. */
	public final static int OPENMARK = 0;
	public final static int SAVEMARK = 1;
	public final static int TEXTSET = 2;
	
	private final String[] mFont = new String[] {"20","22","24","26","28","30",
			"32","34","36","38","40","42","44"};
	private int[] mBackground = new int[]{R.drawable.backg1, R.drawable.backg3, R.drawable.backg4};
	
	private PageWidget mPageWidget;
	private Bitmap mCurPageBitmap, mNextPageBitmap;
	private Canvas mCurPageCanvas, mNextPageCanvas;
	private BookPageFactory mPagefactory;
	
	private int whichSize=3;//��ǰ�������С
	private int whichBg=1;	//��ǰ����
	private int txtProgress = 0;//��ǰ�Ķ��Ľ���
	private int mCurPostion;

	private DbHelper mDbHelper; 
	private SetupInfoDBHelper mSetupDbHelper;
	private BookMarkDBHelper mMarkDBHelper;
	private Cursor mCursor;
	private Context mContext;
	
	private BookInfo mBook = null; 
	private SetupInfo mSetup = null;
	
	private Gallery mGalleryBg;
	private ImageSwitcher mImgSwitcherBg ;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Display display = getWindowManager().getDefaultDisplay();
		int w = display.getWidth();
		int h = display.getHeight(); 

		//��ʼ��
		mCurPageBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);
		
		mPagefactory = new BookPageFactory(w, h); 
		
		//ȡ�ô��ݵĲ���
		Intent intent = getIntent();
		int bookid = intent.getIntExtra("bookid", 1);
			mContext = this;
			mDbHelper = new DbHelper(mContext);
			mSetupDbHelper = new SetupInfoDBHelper(mContext);
			try {
				//�����ݿ��еõ�ͼ���ϵͳ������Ϣ
				mBook = mDbHelper.getBookInfo(bookid);
				mSetup = mSetupDbHelper.findSetupInfo();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//��������Ϊ��
			if(mBook != null){
				mPagefactory.setFileName(mBook.bookname);
				mPagefactory.setBgBitmap(BitmapFactory.decodeResource(getResources(), mBackground[mSetup.getBackgroud()]));
				//����View
				mPageWidget = new PageWidget(this, w, h);
				setContentView(mPageWidget);
				
				//��ͼ��
				mPagefactory.openbook(mBook.url);
				int m_mbBufLen = mPagefactory.getBufLen();
				
				if (mBook.bookmark > 0) { 
					
					//���õ�ǰ�����С �Ķ�����
					whichSize = mSetup.getFontsize();
					whichBg = mSetup.getBackgroud();
					
					//�����Ķ�����
					mPagefactory.setFontSize(Integer.parseInt(mFont[mSetup.getFontsize()]));
					mPagefactory.setBeginPos(Integer.valueOf(mBook.bookmark));
					
					int begin = m_mbBufLen*100/100;
					
					try {
						mPagefactory.prePage();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					mPagefactory.draw(mNextPageCanvas);
					mPageWidget.setBitmaps(mNextPageBitmap, mNextPageBitmap);
					//����View
					mPageWidget.postInvalidate();
					
					mDbHelper.close();
				}else{
					mPagefactory.draw(mCurPageCanvas);
					mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);
				} 

				mPageWidget.setOnTouchListener(new PageWidgtOnTouchListener());
			}else{
				Toast.makeText(mContext, "�����鲻���ڣ������Ѿ�ɾ��",Toast.LENGTH_SHORT).show(); 
				finish();
			}
	}
	
	//PageWidgt Touch�¼�����  ��ҳ
	class PageWidgtOnTouchListener implements OnTouchListener{

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
							Toast.makeText(mContext, "�Ѿ��ǵ�һҳ",Toast.LENGTH_SHORT).show(); 
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
							Toast.makeText(mContext, "�Ѿ������һҳ",Toast.LENGTH_SHORT).show();
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
		
	}
	
	
	// �����˵�
	public boolean onCreateOptionsMenu(Menu menu) {
		 super.onCreateOptionsMenu(menu);
        //ͨ��MenuInflater��XML ʵ����Ϊ Menu Object
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	// �����˵�
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) { 
		
		//���������С
		case R.id.fontsize:
			new AlertDialog.Builder(this)
			.setTitle("��ѡ��")
			.setIcon(android.R.drawable.ic_dialog_info)                
			.setSingleChoiceItems(mFont, whichSize, 
			  new DialogInterface.OnClickListener() {
			     public void onClick(DialogInterface dialog, int which) {
			    	 dialog.dismiss();
			    	 setFontSize(Integer.parseInt(mFont[which]));
			    	 whichSize = which;
			     }
			  }
			).setNegativeButton("ȡ��", null).show();
			break;
			
		//�����Ķ�����
		case R.id.background:
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("��ѡ�񱳾�");
			
			LayoutInflater bginflater = LayoutInflater.from(mContext);
			View view = bginflater.inflate(R.layout.select_background, null);
			mGalleryBg = (Gallery) view.findViewById(R.id.gallery_background);
			mImgSwitcherBg = (ImageSwitcher) view.findViewById(R.id.img_switcher_background);
			
			mGalleryBg.setAdapter(new BackgroundSelectAdapter(mContext, mBackground));
			mGalleryBg.setSelection(mBackground.length / 2);
			mGalleryBg.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					mImgSwitcherBg.setImageResource(mBackground[position]);
					whichBg = position;
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					
				}
			});
			
			mImgSwitcherBg.setFactory(new MyViewFactory(mContext));
			
			dialog.setView(view);
			
			dialog.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mPagefactory.setBgBitmap(BitmapFactory.decodeResource(getResources(),
							mBackground[whichBg]));
					setContentView(mPageWidget);
					mPagefactory.draw(mNextPageCanvas);
					mPageWidget.setBitmaps(mNextPageBitmap, mNextPageBitmap);
					mPageWidget.invalidate();
					mSetup.setBackgroud(whichBg);
					mSetupDbHelper.updateSetupInfo(mSetup);
				}
			});
			
			dialog.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			dialog.create().show();
			break;
			
		//��ת��ָ��λ��
		case R.id.nowprogress:
			LayoutInflater inflater = getLayoutInflater();
		    final View layout = inflater.inflate(R.layout.bar, (ViewGroup) findViewById(R.id.seekbar));
		    SeekBar seek = (SeekBar)layout.findViewById(R.id.seek);
		    final TextView textView = (TextView)layout.findViewById(R.id.textprogress);
		    
		    //�õ���ǰλ��
		    txtProgress = mPagefactory.getCurProgress();
		    //���õ�ǰ��������Ϣ
		    seek.setProgress(txtProgress);
		    textView.setText(String.format(getString(R.string.progress), txtProgress+"%"));
		    
		    //�����������仯
		    seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					//�õ�����
					int progressBar = seekBar.getProgress();
					int m_mbBufLen = mPagefactory.getBufLen();
					//�õ���ʼ��ȡλ��
					int pos = m_mbBufLen*progressBar/100;
					if(progressBar == 0){
						pos = 1;
					}
					//����ָ��λ�õĿ�ʼ��ȡλ��
					mPagefactory.setBeginPos(Integer.valueOf(pos));
					try {
						mPagefactory.prePage();
					} catch (IOException e) {
						e.printStackTrace();
					}
					//����ˢ��View
					mPagefactory.draw(mCurPageCanvas);
					mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);
					mPageWidget.postInvalidate();
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					if(fromUser){
						textView.setText(String.format(getString(R.string.progress), progress+"%"));
					}
				}
			});
		   new AlertDialog.Builder(this).setTitle("��ת").setView(layout)
		     .setPositiveButton("ȷ��", 
		    		 new DialogInterface.OnClickListener() {
					     public void onClick(DialogInterface dialog, int which) {
					        dialog.dismiss();
					     }
					  }
		    		 ).show();
			break;
			
		//�ʼ�	
		case R.id.note:
			Intent intent = new Intent(mContext, NoteAcitvity.class);
			intent.putExtra("bookName", mBook.bookname);
			
			BookNoteDBHelper noteDBHelper = new BookNoteDBHelper(mContext);
			BookNote note = noteDBHelper.findNoteByBookName(mBook.bookname);
			if(note != null) {
				intent.putExtra("note", note);
			}
			
			mContext.startActivity(intent);
			break;
		
		//��ǩ
		case R.id.book_mark:
			mMarkDBHelper = new BookMarkDBHelper(mContext);
			final BookMark bookMark = new BookMark(mBook.bookname, mPagefactory.getOneLine(), mPagefactory.getCurPostionBeg());
			List<BookMark> markList = mMarkDBHelper.findMark(mBook.bookname);
			if(null == markList) {
				mMarkDBHelper.saveMark(bookMark);
				Toast.makeText(mContext, "��ǩ��ӳɹ�:"+mPagefactory.getCurPostionBeg()+":"+mPagefactory.getCurPostion(), 0).show();
			} else {
				LinearLayout markLayout = new LinearLayout(mContext);
				markLayout.setOrientation(LinearLayout.VERTICAL);
				markLayout.setBackgroundColor(Color.WHITE);
				
				ListView lvMark = new ListView(mContext);
				ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.select_dialog_item, markList);
				lvMark.setAdapter(adapter);
				markLayout.addView(lvMark);
				
				Dialog markDialog = new AlertDialog.Builder(mContext)
					.setTitle("��ǩ")
					.setView(markLayout)
					.setPositiveButton("���", new DialogInterface.OnClickListener() {
					
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mMarkDBHelper.saveMark(bookMark);
							Toast.makeText(mContext, "��ǩ��ӳɹ�:"+":"+mPagefactory.getCurPostion(), 1).show();
						}
					}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					}).create();;
				markDialog.show();
				
				lvMark.setOnItemClickListener(new MarkClickListener(markList, markDialog));
				lvMark.setOnItemLongClickListener(new MarkLongClickListener(markList,markDialog));
				
			}
			
			break;
		case R.id.setting:
			Intent setIntent = new Intent(mContext, SettingActivity.class);
			startActivity(setIntent);
			break;
		default:
			break;

		}
		return true;
	}
	
	class MyViewFactory implements ViewFactory {

		private Context context;
		
		public MyViewFactory(Context context) {
			this.context = context;
		}
		
		@Override
		public View makeView() {
			ImageView img = new ImageView(mContext);
			img.setFocusable(true);
			img.setLayoutParams(new ImageSwitcher.LayoutParams(80, 80));
			return img;
		}
		
	}
	
	//��ǩ�б����
	class MarkClickListener implements OnItemClickListener{
		
		private List<BookMark> markList;
		private Dialog markDialog;
		
		public MarkClickListener(List<BookMark> markList, Dialog markDialog) {
			this.markList = markList;
			this.markDialog = markDialog;
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mPagefactory.setBeginPos(markList.get(position).getBegin());
			try {
				mPagefactory.nextPage();
			} catch (IOException e) {
				e.printStackTrace();
			}
			setContentView(mPageWidget);
			mPagefactory.draw(mNextPageCanvas);
			mPageWidget.setBitmaps(mNextPageBitmap, mNextPageBitmap);
			//ˢ��View
			mPageWidget.postInvalidate();
			markDialog.dismiss();
		}
		
	}
	
	//������ǩ�¼�������
	class MarkLongClickListener implements OnItemLongClickListener {
		private List<BookMark> markList;
		private Dialog markDialog;
		public MarkLongClickListener(List<BookMark> markList, Dialog markDialog) {
			this.markList = markList;
			this.markDialog = markDialog;
		}
		
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			final BookMark bookMark = markList.get(position);
			final int pos = position;
			markDialog.dismiss();
			Dialog dialog = new AlertDialog.Builder(mContext).setTitle("ɾ����ǩ")
								.setMessage("ȷ��ɾ������ǩ��")
								.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										mMarkDBHelper.deleteMark(bookMark.getId());
										Toast.makeText(mContext, "ɾ���ɹ�" , Toast.LENGTH_SHORT).show();
										markList.remove(pos);
										
									}
								}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
									}
								}).create();
			dialog.show();
			return false;
		}
		
	}
	
	/**
	 * ���������С 
	 * 
	 */
	private void setFontSize(int size){
		mPagefactory.setFontSize(size);
		int pos = mPagefactory.getCurPostionBeg();
		//�������ÿ�ʼλ��
		mPagefactory.setBeginPos(pos);
		try {
			mPagefactory.nextPage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		setContentView(mPageWidget);
		mPagefactory.draw(mNextPageCanvas);
		mPageWidget.setBitmaps(mNextPageBitmap, mNextPageBitmap);
		//ˢ��View
		mPageWidget.invalidate();
	}
	  
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		  if (keyCode == KeyEvent.KEYCODE_BACK) {
			  addBookMark();
			  this.finish();
		  }
		  return false;
	}
	
	//�������һ�ζ�ȡλ��
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
					System.out.println("��ǰ�����С �����ݿ������С" + whichSize + "====" + mSetup.getFontsize());
					if(whichSize != mSetup.getFontsize()) {
						mSetup.setFontsize(whichSize);
						mSetupDbHelper.updateSetupInfo(mSetup);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					mDbHelper.close();
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
}