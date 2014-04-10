
package com.example.minireader.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.minireader.R;
import com.example.minireader.entity.BookInfo;

/**
 *@author fengjuan, 2014-4-3
 *
 */

public class BookShelfGVAdapter extends BaseAdapter {
	
	private int[] mBookBg = {R.drawable.book_bg1, R.drawable.book_bg2, 
							 R.drawable.book_bg3, R.drawable.book_bg4, 
							 R.drawable.book_bg5, R.drawable.book_bg6,};
	
	private Context mContext;
	private LinkedList<BookInfo> mBookList;
	
	public BookShelfGVAdapter(LinkedList<BookInfo> bookList, Context context) {
		super();
		this.mContext = context;
		this.mBookList = bookList;
	}

	@Override
	public int getCount() {
		return mBookList.size();
	}

	@Override
	public Object getItem(int arg0) {
		
		return mBookList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View contentView, ViewGroup viewGroup) {
		
		contentView = LayoutInflater.from(mContext).inflate(R.layout.activity_book_item, null);
		
		ImageView imgBookImg = (ImageView) contentView.findViewById(R.id.img_book_img);
		TextView tvImgText =(TextView) contentView.findViewById(R.id.tv_book_img_text);
		TextView tvBookName = (TextView) contentView.findViewById(R.id.tv_book_name);
		
		imgBookImg.setImageResource(mBookBg[(int) (Math.random()*6)]);
		tvImgText.setText(mBookList.get(position).bookname);
		tvBookName.setText(mBookList.get(position).bookname);
		
		return contentView;
	}
	
}