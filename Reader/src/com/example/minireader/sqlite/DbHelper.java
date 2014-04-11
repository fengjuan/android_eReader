package com.example.minireader.sqlite;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.minireader.entity.BookInfo;

public class DbHelper extends SQLiteOpenHelper {
	private final static String DATABASE_NAME = "minireader_db";	//���ݿ���
	private final static int DATABASE_VERSION = 1;				//�汾��
	
	private final static String TABLE_NAME = "book_shelf";		//����
	private final static String TABLE_SETUP = "book_setupinfo";
	
	
	public final static String FIELD_ID = "_id";
	public final static String INDEX_NAME_ON_BOOKMARK = "uq_bookshelf_index";

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		StringBuffer sqlCreateCountTb = new StringBuffer();
		//�������
		sqlCreateCountTb.append("create table ").append(TABLE_NAME)
					   .append("(_id integer primary key autoincrement,")		   
					   .append(" bookname text,")   
					   .append(" url text,")
					   .append(" bookmark integer,")
					   .append("downloadurl text,")
					   .append("date DATETIME DEFAULT CURRENT_TIMESTAMP)");
		db.execSQL(sqlCreateCountTb.toString());

		//ϵͳ���ñ�
		StringBuffer setupTb = new StringBuffer();
		setupTb.append("create table ").append(TABLE_SETUP)
			   .append("(_id integer primary key autoincrement,")	
			   .append("fontsize text,") 
			   .append("fontcolor text,")
				.append("background text)");
		db.execSQL(setupTb.toString());
		
		//�ʼǱ�
		StringBuffer bookNoteTb = new StringBuffer();
		bookNoteTb.append("create table book_note")
					.append("(_id integer primary key autoincrement,")
					.append("bookName text,")
					.append("noteTitle text, ")
					.append("noteContent text)");
		db.execSQL(bookNoteTb.toString());
		
		//��ǩ��
		StringBuffer bookMarkTb = new StringBuffer();
		bookMarkTb.append("create table book_mark")
					.append("(_id integer primary key autoincrement,")
					.append("bookName text,")
					.append("markName text,")
					.append("begin integer)");
		db.execSQL(bookMarkTb.toString());

		//���Ψһ����
		String bookIndexSql="CREATE UNIQUE INDEX IF NOT EXISTS " + INDEX_NAME_ON_BOOKMARK + " ON " + TABLE_NAME + " (bookname)";
		String noteIndexSql="CREATE UNIQUE INDEX IF NOT EXISTS uq_booknote_index ON book_note(bookName)";
		
		db.execSQL(bookIndexSql);
		db.execSQL(noteIndexSql);
		
		//Ĭ������
		String setup = "insert into " + TABLE_SETUP + "(fontsize,fontcolor,background) values('3','0','1')";
		db.execSQL(setup);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {

		String sql = " DROP TABLE IF EXISTS " + TABLE_NAME;
		String bookIndexSql ="DROP INDEX IF EXISTS " + INDEX_NAME_ON_BOOKMARK;
		String noteIndexSql = "DROP	INDEX IF EXISTS uq_booknote_index";
		db.execSQL(sql);
		db.execSQL(bookIndexSql);
		db.execSQL(noteIndexSql);
		onCreate(db);
	}

	/**
	 * �����ѯ
	 * @return
	 */
	public Cursor select() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
				" _id desc");
		return cursor;
	}

	/**
	 * ����id��ѯ
	 * @param id
	 * @return
	 */
	public BookInfo getBookInfo(int id){
		BookInfo book = new BookInfo();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		cursor = db.query(TABLE_NAME,  new String[]{"_id", "bookname", "bookmark", "datetime(date,'localtime') as time", "url", "downloadurl"}, "_id=?", new String[]{String.valueOf(id)}, null, null, null);
		cursor.moveToPosition(0);
		book.id = id;
		book.bookname = cursor.getString(cursor.getColumnIndex("bookname"));
		book.bookmark = cursor.getInt(cursor.getColumnIndex("bookmark"));
		book.date = cursor.getString(cursor.getColumnIndex("time"));
		book.url = cursor.getString(cursor.getColumnIndex("url"));
		book.downloadUrl = cursor.getString(cursor.getColumnIndex("downloadurl"));
		db.close();
		return book;
	}
	
	
	
	/**
	 * �õ����еĵ�����
	 * @return
	 */
	public List<BookInfo> getAllBookInfo(){
		List<BookInfo> books = new LinkedList<BookInfo>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[]{"_id", "bookname", "bookmark", "datetime(date,'localtime') as time", "url", "downloadurl"}, null, null, null, null, " date desc");
		int count = cursor.getCount();
		for (int i = 0; i < count; i++) {
			cursor.moveToPosition(i);
			BookInfo book = new BookInfo();
			book.id = cursor.getInt(0);
			book.bookname = cursor.getString(cursor.getColumnIndex("bookname"));
			book.bookmark = cursor.getInt(cursor.getColumnIndex("bookmark"));
			book.date = cursor.getString(cursor.getColumnIndex("time"));
			book.url = cursor.getString(cursor.getColumnIndex("url"));
			book.downloadUrl = cursor.getString(cursor.getColumnIndex("downloadurl"));
			books.add(book);
		}
		cursor.close();
		db.close();
		
		return books;
	}
	
	/**
	 * ������ǩ
	 * @param Title ��ǩ��
	 * @return
	 */
	public long insert(String Title) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("bookmark", Title);
		long row = db.insert(TABLE_NAME, null, cv);
		return row;
	}
	
	/**
	 * ���������ļ�������� 
	 * @param filename
	 * @param bookmark
	 * @return
	 */
	public long insert(BookInfo bookInfo) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("bookname", bookInfo.bookname);
		cv.put("bookmark", bookInfo.bookmark);
		cv.put("url", bookInfo.url);
		cv.put("downloadurl", bookInfo.downloadUrl);
		long row = db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
		return row;
	}
	
	/**
	 * ɾ��
	 * @param id
	 */
	public void delete(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = FIELD_ID + "=?";
		String[] whereValue = { Integer.toString(id) };
		db.delete(TABLE_NAME, where, whereValue);
	}

	/**
	 * ���µ�������Ϣ
	 * @param id
	 * @param filename
	 * @param bookmark
	 */
	public void update(int id, String filename, String bookmark) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = FIELD_ID + "=?";
		String[] whereValue = { Integer.toString(id) };
		ContentValues cv = new ContentValues();
		cv.put("bookname", filename);
		cv.put("bookmark", bookmark);
		cv.put("date", new Date(System.currentTimeMillis()).toString());
		db.update(TABLE_NAME, cv, where, whereValue);
	}
}
