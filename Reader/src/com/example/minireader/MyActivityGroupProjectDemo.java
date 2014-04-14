package com.example.minireader;


import android.app.ActivityGroup;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.example.minireader.adapter.ToolBarGVAdapter;
import com.example.minireader.file.ChooseFileActivity;
import com.example.minireader.online.WebViewActivity;

public class MyActivityGroupProjectDemo extends ActivityGroup {		
	private GridView mGridviewToolbar; 								// ����GridView������
	private ToolBarGVAdapter mToolbarAdapter = null; 							// ͼƬ������
	private LinearLayout mContentLayout = null; 							// ��ʾ���ݵĲ��ֹ�����
	private String mTexts[] = new String[] {"���", "����", "����", "�ʼ�"}; 								// ͼƬ��ʾ��ԴID
	private int mWidth = 0 ;											// ����ÿ���˵����ͼƬ���
	private int mHeight = 0 ;										// ����˵���ĸ߶�
	private Intent mIntent = null;									// Ҫ������Intent
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE); 		// ����ʾ����
		super.setContentView(R.layout.main); 						
		
		this.mGridviewToolbar = (GridView) super.findViewById(R.id.gridviewbar); 
		this.mContentLayout = (LinearLayout) super.findViewById(R.id.content); 
		
		this.mGridviewToolbar.setNumColumns(this.mTexts.length);	// ����ÿ�е���ʾ����
		this.mGridviewToolbar.setSelector(new ColorDrawable(Color.TRANSPARENT));// ѡ�е�ʱ��Ϊ͸��ɫ
		this.mGridviewToolbar.setGravity(Gravity.CENTER);			
		this.mGridviewToolbar.setVerticalSpacing(0);					// ��ֱ���Ϊ0
		this.mGridviewToolbar.setBackgroundResource(R.drawable.bg); 		// ������ɫ����
		
		this.mWidth = super.getWindowManager().getDefaultDisplay().getWidth() / this.mTexts.length; // ����ƽ�����
				
		this.mHeight = super.getWindowManager().getDefaultDisplay().getHeight() / 10;		
				
		this.mToolbarAdapter = new ToolBarGVAdapter(this, this.mTexts, this.mWidth,
								this.mHeight, R.drawable.menu_selected); 			// ʵ����������
		
		this.mGridviewToolbar.setAdapter(this.mToolbarAdapter);					
		
		this.switchActivity(0); 									// Ĭ�ϴ����
		
		this.mGridviewToolbar.setOnItemClickListener(
				new OnItemClickListenerImpl());						// ѡ�м���
	}

	private class OnItemClickListenerImpl implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			MyActivityGroupProjectDemo.this.switchActivity(position);	// �л�ѡ��
		}

	}

	/**
	 * ����ID��ָ����Activity
	 * @param id GridViewѡ��������
	 */
	public void switchActivity(int id) {							// �л���ͼ
		this.mToolbarAdapter.setFocus(id); 									// ѡ�����ø���
		this.mContentLayout.removeAllViews();								// ���������������View
		
		switch (id) {												// ���ݲ���ʵ����Intent
		case 0: 													// ָ��������Intent
			this.mIntent = new Intent(MyActivityGroupProjectDemo.this,
					BookShelfActivity.class);
			break;
		case 1: 													// ָ��������Intent
			this.mIntent = new Intent(MyActivityGroupProjectDemo.this,
					ChooseFileActivity.class);
			break;
		case 2: 													
			this.mIntent = new Intent(MyActivityGroupProjectDemo.this,
					WebViewActivity.class);
			this.mGridviewToolbar.setVisibility(View.GONE);
			break;
		case 3: 													
			this.mIntent = new Intent(MyActivityGroupProjectDemo.this, NoteListAcitvity.class);										
			break;
		}
		
		this.mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);		// ���ӱ�� 
		Window subActivity = this.getLocalActivityManager().startActivity(
				"subActivity"+id, this.mIntent);						// Activity תΪ View
		
		this.mContentLayout.addView(subActivity.getDecorView(),
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); // �������View
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(keyCode == KeyEvent.KEYCODE_BACK && !getLocalActivityManager().getCurrentId().equals("subActivity0")){
			this.mGridviewToolbar.setVisibility(View.VISIBLE);
			switchActivity(0);
            return false;
		}
		return getLocalActivityManager().getCurrentActivity().onKeyDown(keyCode, event);
	}

	
}