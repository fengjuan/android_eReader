package com.example.minireader;

import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.example.minireader.adapter.BookShelfGVAdapter;
import com.example.minireader.entity.BookInfo;
import com.example.minireader.online.WebViewActivity;
import com.example.minireader.sqlite.DbHelper;

public class BookShelfActivity extends Activity {
	private static final String TAG = "BookShelfActivity";
	
	private GridView mGvShowBooks; 
	private DbHelper mDbHelper;
	
	private LinkedList<BookInfo> mBookList;
	private BookShelfGVAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book); 
       
        mDbHelper = new DbHelper(this);
        
        mBookList = (LinkedList<BookInfo>) mDbHelper.getAllBookInfo();
        
        mGvShowBooks = (GridView) findViewById(R.id.gv_book_shelf);
        mAdapter = new BookShelfGVAdapter(mBookList, this);
        mGvShowBooks.setAdapter(mAdapter);
        
        mGvShowBooks.setOnItemClickListener(new GridViewItemClickListener());
        mGvShowBooks.setOnItemLongClickListener(new GridViewItemLongClickListener());
        
    }
    
    
    private void reflushGridView() {
    	mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.book, menu);
        return true;
    }
    

    //短按事件监听
    class GridViewItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapter, View v, int position,
				long id) {
			BookInfo bookInfo = mBookList.get(position);
			
			if(bookInfo.url.startsWith("http")) {
				Intent intent = new Intent(BookShelfActivity.this, WebViewActivity.class);
				intent.putExtra("url", bookInfo.url);
				intent.setAction(Intent.ACTION_VIEW);
				startActivity(intent);
			}else if(bookInfo.bookname.endsWith(".txt")) {
				Intent intent = new Intent(BookShelfActivity.this, ReadBookActivity.class);
				intent.putExtra("bookid", bookInfo.id);
				intent.setAction(Intent.ACTION_VIEW);
				startActivity(intent);
				
			} else if(bookInfo.bookname.endsWith(".pdf")) {
				
				Uri uri = Uri.parse(bookInfo.url);
				Intent intent = new Intent(BookShelfActivity.this, MuPDFActivity.class);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(uri);
				startActivity(intent);
				
			} else if(bookInfo.bookname.endsWith(".doc")){
				Intent intent = new Intent(BookShelfActivity.this,WordReadActivity.class);
				intent.setAction(Intent.ACTION_VIEW);
				Uri uri = Uri.parse("file:/" + bookInfo.url);
				intent.setData(uri);
			
				startActivity(intent);
			} 
			
		}
    }
    
    //长按事件监听
    class GridViewItemLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> adapter, View v, int position,
				long id) {
			final BookInfo bookInfo = mBookList.get(position);
			final int pos = position;
			Dialog dialog = new AlertDialog.Builder(BookShelfActivity.this).setTitle("从书架移除")
								.setMessage("确定将此书从书架移除？")
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										mDbHelper.delete(bookInfo.id);
										mBookList.remove(pos);
										reflushGridView();
										Toast.makeText(BookShelfActivity.this, "删除成功" , Toast.LENGTH_SHORT).show();
									}
								}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										
									}
								}).create();
			dialog.show();
			return false;
		}
    	
    }
    
    private void exitDialog() {
		Dialog dialog = new AlertDialog.Builder(
				BookShelfActivity.this)	 				// 实例化对象
				.setTitle("程序退出？") 								// 设置显示标题
				.setMessage("您确定要退出本程序吗？") 					// 设置显示内容
				.setPositiveButton("确定", 							// 增加一个确定按钮
						new DialogInterface.OnClickListener() {		// 设置操作监听
							public void onClick(DialogInterface dialog,
									int whichButton) { 				// 单击事件
								BookShelfActivity.this.finish();// 程序结束
							}
						}).setNegativeButton("取消", 				// 增加取消按钮
						new DialogInterface.OnClickListener() {		// 设置操作监听
							public void onClick(DialogInterface dialog,
									int whichButton) { 				// 单击事件
							}
						}).create(); 								// 创建Dialog
		dialog.show(); 												// 显示对话框
	}
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
			exitDialog();
			return true;
		}
    	return super.onKeyDown(keyCode, event);
    }
    
}
