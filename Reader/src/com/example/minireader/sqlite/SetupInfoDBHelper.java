/**
 *  Author :  hmg25
 *  Description :
 */
package com.example.minireader.sqlite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.minireader.entity.SetupInfo;

/**
 * hmg25's android Type
 *
 *@author fengjuan
 *
 */
public class SetupInfoDBHelper {
	private final static String TABLE_SETUP = "book_setupinfo";
	private DbHelper mDbHelper;
	private SQLiteDatabase db;
	
	public SetupInfoDBHelper(Context context) {
		this.mDbHelper = new DbHelper(context);
	}
	
	/**
	 * 更新设置
	 * @param setupInfo
	 */
	public void updateSetupInfo(SetupInfo setupInfo) {
		db = mDbHelper.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put("fontsize", String.valueOf(setupInfo.getFontsize()));
		cv.put("background", String.valueOf(setupInfo.getBackgroud()));
		cv.put("fontcolor", String.valueOf(setupInfo.getFontcolor()));
		db.update(TABLE_SETUP, cv, "_id=?", new String[]{String.valueOf(setupInfo.getId())});
		db.close();
	}
	
	/**
	 * 查询设置相关信息
	 * @return
	 */
	public SetupInfo findSetupInfo(){
		SetupInfo setup = new SetupInfo();
		db = mDbHelper.getWritableDatabase();

		Cursor cursor = null;
		cursor = db.query(TABLE_SETUP, null, null, null, null, null, null);
		cursor.moveToPosition(0);
		
		setup.setId(cursor.getInt(0)); 
		setup.setFontsize(cursor.getInt(1));
		setup.setFontcolor(cursor.getInt(2));
		setup.setBackgroud(cursor.getInt(3));
		
		db.close();
		return setup;
	}
	
}
