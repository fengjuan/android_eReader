
package com.example.minireader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.minireader.entity.BookNote;
import com.example.minireader.sqlite.BookNoteDBHelper;

/**
 * 显示所有笔记
 *@author fengjuan, 2014-4-9
 *
 */

public class NoteListAcitvity extends Activity{
	
	private ListView mLvNoteList;
	
	private BookNoteDBHelper mNoteDBHelper;
	private List<BookNote> mNoteList;
	
 	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_list);
		
		
		mLvNoteList = (ListView) findViewById(R.id.lv_note_list);
		
		mNoteDBHelper = new BookNoteDBHelper(this);
		mNoteList= mNoteDBHelper.findAllNote();
		
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		for (BookNote  bookNote : mNoteList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("bookName", bookNote.getBookName());
			map.put("noteTitle", "笔记：" + bookNote.getNoteTitle());
			data.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.activity_note_list_item, 
													new String[]{"bookName", "noteTitle"}, 
													new int[]{R.id.tv_book_name, R.id.tv_note_title});
		
		
		mLvNoteList.setAdapter(adapter);
		
		mLvNoteList.setOnItemClickListener(new LisViewItemClickListener());
	}
 	
 	//ListView Item监听类
 	class LisViewItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position,
				long id) {
			
			BookNote note = mNoteList.get(position);
			
			Intent intent = new Intent(NoteListAcitvity.this, NoteAcitvity.class);
			intent.putExtra("note", note);
			intent.putExtra("bookName", note.getBookName());
			NoteListAcitvity.this.startActivity(intent);
		}
 		
 	}
	
}
