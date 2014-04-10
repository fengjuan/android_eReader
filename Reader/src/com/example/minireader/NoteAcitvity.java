
package com.example.minireader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.minireader.entity.BookNote;
import com.example.minireader.sqlite.BookNoteDBHelper;
import com.example.minireader.util.NoteEditText;

/**
 *@author fengjuan, 2014-4-9
 *
 */

public class NoteAcitvity extends Activity{
	
	private EditText mNoteTitle;
	private NoteEditText mNoteContent;
	private Button mBtnSave;
	private Button mBtnCancel;
	private Button mBtnDelete;
	
	private BookNoteDBHelper mNoteDBHelper;
	
	private BookNote mNote;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);
		
		this.init();
		
		BookNote note = (BookNote) getIntent().getSerializableExtra("note");
		if(note == null) {
			mBtnDelete.setVisibility(View.GONE);
		}  else {
			mNote = note;
			mNoteTitle.setText(note.getNoteTitle());
			mNoteContent.setText(note.getNoteContent());
		}
		//为按钮添加监听
		mBtnSave.setOnClickListener(new SaveNoteListener());
		mBtnCancel.setOnClickListener(new CancelNoteListener());
		mBtnDelete.setOnClickListener(new DeleteNoteListener());
		
	}
	
	private void init() {
		//找到找到找到所有View控件
		mNoteTitle = (EditText) findViewById(R.id.et_note_title);
		mNoteContent = (NoteEditText) findViewById(R.id.et_note_content);
		mBtnSave = (Button) findViewById(R.id.btn_note_save);
		mBtnCancel = (Button) findViewById(R.id.btn_note_cancel);
		mBtnDelete = (Button) findViewById(R.id.btn_note_delete);
		
		mNoteDBHelper = new BookNoteDBHelper(this);
	}
	
	//保存按钮监听
	class SaveNoteListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String noteTitle = mNoteTitle.getText().toString();
			String noteContent = mNoteContent.getText().toString();
			String bookName = getIntent().getStringExtra("bookName");
			
			mNote = new BookNote(bookName, noteTitle, noteContent);
			mNoteDBHelper.saveNote(mNote);
			Toast.makeText(NoteAcitvity.this, "保存成功", Toast.LENGTH_SHORT).show();
		}
	}
	
	//取消按钮监听
	class CancelNoteListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			exitDialog();
		}
	}
	
	//删除按钮监听
	class DeleteNoteListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			mNoteDBHelper.deleteNote(mNote.getNoteId());
			mNoteTitle.setText("");
			mNoteContent.setText("");
			mBtnDelete.setVisibility(View.GONE);
			Toast.makeText(NoteAcitvity.this, "删除成功", Toast.LENGTH_SHORT).show();
		}
	}
	
	 private void exitDialog() {
			Dialog dialog = new AlertDialog.Builder(
					NoteAcitvity.this)	 				// 实例化对象
					.setTitle("取消？") 								// 设置显示标题
					.setMessage("您确定不保存笔记吗？") 					// 设置显示内容
					.setPositiveButton("确定", 							// 增加一个确定按钮
							new DialogInterface.OnClickListener() {		// 设置操作监听
								public void onClick(DialogInterface dialog,
										int whichButton) { 				// 单击事件
									NoteAcitvity.this.finish();// 程序结束
								}
							}).setNegativeButton("取消", 				// 增加取消按钮
							new DialogInterface.OnClickListener() {		// 设置操作监听
								public void onClick(DialogInterface dialog,
										int whichButton) { 				// 单击事件
								}
							}).create(); 								// 创建Dialog
			dialog.show(); 												// 显示对话框
		}
}
