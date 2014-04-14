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
	private GridView mGridviewToolbar; 								// 定义GridView工具条
	private ToolBarGVAdapter mToolbarAdapter = null; 							// 图片适配器
	private LinearLayout mContentLayout = null; 							// 显示内容的布局管理器
	private String mTexts[] = new String[] {"书架", "本地", "在线", "笔记"}; 								// 图片显示资源ID
	private int mWidth = 0 ;											// 保存每个菜单项的图片宽度
	private int mHeight = 0 ;										// 保存菜单项的高度
	private Intent mIntent = null;									// 要操作的Intent
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE); 		// 不显示标题
		super.setContentView(R.layout.main); 						
		
		this.mGridviewToolbar = (GridView) super.findViewById(R.id.gridviewbar); 
		this.mContentLayout = (LinearLayout) super.findViewById(R.id.content); 
		
		this.mGridviewToolbar.setNumColumns(this.mTexts.length);	// 设置每行的显示列数
		this.mGridviewToolbar.setSelector(new ColorDrawable(Color.TRANSPARENT));// 选中的时候为透明色
		this.mGridviewToolbar.setGravity(Gravity.CENTER);			
		this.mGridviewToolbar.setVerticalSpacing(0);					// 垂直间隔为0
		this.mGridviewToolbar.setBackgroundResource(R.drawable.bg); 		// 背景颜色设置
		
		this.mWidth = super.getWindowManager().getDefaultDisplay().getWidth() / this.mTexts.length; // 计算平均宽度
				
		this.mHeight = super.getWindowManager().getDefaultDisplay().getHeight() / 10;		
				
		this.mToolbarAdapter = new ToolBarGVAdapter(this, this.mTexts, this.mWidth,
								this.mHeight, R.drawable.menu_selected); 			// 实例化适配器
		
		this.mGridviewToolbar.setAdapter(this.mToolbarAdapter);					
		
		this.switchActivity(0); 									// 默认打开书架
		
		this.mGridviewToolbar.setOnItemClickListener(
				new OnItemClickListenerImpl());						// 选中监听
	}

	private class OnItemClickListenerImpl implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			MyActivityGroupProjectDemo.this.switchActivity(position);	// 切换选项
		}

	}

	/**
	 * 根据ID打开指定的Activity
	 * @param id GridView选中项的序号
	 */
	public void switchActivity(int id) {							// 切换视图
		this.mToolbarAdapter.setFocus(id); 									// 选中项获得高亮
		this.mContentLayout.removeAllViews();								// 先清除容器中所有View
		
		switch (id) {												// 根据操作实例化Intent
		case 0: 													// 指定操作的Intent
			this.mIntent = new Intent(MyActivityGroupProjectDemo.this,
					BookShelfActivity.class);
			break;
		case 1: 													// 指定操作的Intent
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
		
		this.mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);		// 增加标记 
		Window subActivity = this.getLocalActivityManager().startActivity(
				"subActivity"+id, this.mIntent);						// Activity 转为 View
		
		this.mContentLayout.addView(subActivity.getDecorView(),
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); // 容器添加View
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