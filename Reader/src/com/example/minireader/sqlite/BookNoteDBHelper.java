
package com.example.minireader.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.minireader.entity.BookNote;

/**
 *@author fengjuan, 2014-4-9
 *
 */

public class BookNoteDBHelper {
	private final static String TABLE_NOTE = "book_note";
	private DbHelper mDbHelper;
	private SQLiteDatabase db;
	
	public BookNoteDBHelper(Context context) {
		this.mDbHelper = new DbHelper(context);
	}
	
	public void saveNote(BookNote bookNote) {
		db = mDbHelper.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put("bookName", bookNote.getBookName());
		cv.put("noteTitle", bookNote.getNoteTitle());
		cv.put("noteContent", bookNote.getNoteContent().toString());
		
		db.insertWithOnConflict(TABLE_NOTE, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
		db.close();
	}
	
	public void updateNote(BookNote bookNote) {
		db = mDbHelper.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put("noteTitle", bookNote.getNoteTitle());
		cv.put("noteContent", bookNote.getNoteContent());
		
		db.update(TABLE_NOTE, cv, "_id=?", new String[] {String.valueOf(bookNote.getNoteId())});
		db.close();
	}
	
	public void deleteNote(int noteId) {
		db = mDbHelper.getWritableDatabase();
		db.delete(TABLE_NOTE, "_id=?", new String[]{String.valueOf(noteId)});
		db.close();
		
	}
	
	public BookNote findNoteById(int noteId) {
		db = mDbHelper.getReadableDatabase();
		BookNote bookNote = new BookNote();
		
		Cursor cursor = db.query(TABLE_NOTE, null, "_id=?", new String[]{String.valueOf(noteId)}, null, null, null);
		if(cursor.moveToFirst()) {
			bookNote.setNoteId(cursor.getInt(cursor.getColumnIndex("_id")));
			bookNote.setBookName(cursor.getString(cursor.getColumnIndex("bookName")));
			bookNote.setNoteTitle(cursor.getString(cursor.getColumnIndex("noteTitle")));
			bookNote.setNoteContent(cursor.getString(cursor.getColumnIndex("noteContent")));
		}
		
		db.close();
		return bookNote;
		
	}
	
	public BookNote findNoteByBookName(String bookName) {
		db = mDbHelper.getReadableDatabase();
		BookNote bookNote = new BookNote();
		
		Cursor cursor = db.query(TABLE_NOTE, null, "bookName=?", new String[]{bookName}, null, null, null);
		if(cursor.moveToFirst()) {
			bookNote.setNoteId(cursor.getInt(cursor.getColumnIndex("_id")));
			bookNote.setBookName(cursor.getString(cursor.getColumnIndex("bookName")));
			bookNote.setNoteTitle(cursor.getString(cursor.getColumnIndex("noteTitle")));
			bookNote.setNoteContent(cursor.getString(cursor.getColumnIndex("noteContent")));
			return bookNote;
		}
		
		db.close();
		return null;
	}
	
	public List<BookNote> findAllNote() {
		db = mDbHelper.getReadableDatabase();
		List<BookNote> noteList = new ArrayList<BookNote>();
		
		Cursor cursor = db.query(TABLE_NOTE, null, null, null, null, null, "bookName");
		while(cursor.moveToNext()) {
			BookNote bookNote = new BookNote();
			bookNote.setNoteId(cursor.getInt(cursor.getColumnIndex("_id")));
			bookNote.setBookName(cursor.getString(cursor.getColumnIndex("bookName")));
			bookNote.setNoteTitle(cursor.getString(cursor.getColumnIndex("noteTitle")));
			bookNote.setNoteContent(cursor.getString(cursor.getColumnIndex("noteContent")));
			
			noteList.add(bookNote);
		}
		
		db.close();
		return noteList;
	}
	
}
