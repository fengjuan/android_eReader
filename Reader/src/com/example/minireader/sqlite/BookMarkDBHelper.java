
package com.example.minireader.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.minireader.entity.BookMark;

/**
 *@author fengjuan, 2014-4-9
 *
 */

public class BookMarkDBHelper {
	private final static String TABLE_MARK = "book_mark";
	private DbHelper mDbHelper;
	private SQLiteDatabase db;
	
	public BookMarkDBHelper(Context context) {
		this.mDbHelper = new DbHelper(context);
	}
	
	public void saveMark(BookMark bookMark) {
		db = mDbHelper.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put("bookName", bookMark.getBookName());
		cv.put("markName", bookMark.getMarkName());
		cv.put("begin", bookMark.getBegin());
		
		db.insert(TABLE_MARK, null, cv);
		db.close();
	}
	
	public void deleteMark(int id) {
		db = mDbHelper.getWritableDatabase();
		db.delete(TABLE_MARK, "_id=?", new String[]{String.valueOf(id)});
		db.close();
		
	}
	
	public List<BookMark> findMark(String bookName) {
		db = mDbHelper.getReadableDatabase();
		List<BookMark> markList = new ArrayList<BookMark>();
		
		Cursor cursor = db.query(TABLE_MARK, null, "bookName=?", new String[]{bookName}, null, null, null);
		while(cursor.moveToNext()) {
			BookMark bookMark = new BookMark();
			bookMark.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			bookMark.setBookName(cursor.getString(cursor.getColumnIndex("bookName")));
			bookMark.setMarkName(cursor.getString(cursor.getColumnIndex("markName")));
			bookMark.setBegin(cursor.getInt(cursor.getColumnIndex("begin")));
			markList.add(bookMark);
		}
		
		db.close();
		return markList;
	}
	
}
