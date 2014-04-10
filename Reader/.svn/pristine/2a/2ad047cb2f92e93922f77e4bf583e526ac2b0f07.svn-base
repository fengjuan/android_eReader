package com.example.minireader.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class ToolBarGVAdapter extends BaseAdapter {	// 继承BaseAdapter
	private Context context; 						// 传递上下文对象
	private TextView[] menuBtn; 					// 保存所有标签的图片显示
	private int selectedMenuImg; 					// 保存选中的ImageView索引

	/**
	 * 创建一个MenuImageAdapter类的实例
	 * @param context 上下文对象
	 * @param imgIds 所有的图片资源的ID集合
	 * @param width ImageView组件的宽度
	 * @param height ImageView组件的显示高度
	 * @param selectedMenuImg 选中的ImageView的ID
	 */
	public ToolBarGVAdapter(Context context, String texts[], int width,
			int height, int selectedMenuImg) {		// 构造接收参数
		this.context = context; 					// 接收Context对象
		this.selectedMenuImg = selectedMenuImg; 	// 接收选中的ID
		this.menuBtn = new TextView[texts.length]; // 实例化Button数组
		for (int x = 0; x < texts.length; x++) { 	// 实例化每一个Button
			this.menuBtn[x] = new TextView(this.context); // 实例化Button对象
			this.menuBtn[x].setLayoutParams(new GridView.LayoutParams(width,
					height));						// 定义图片的布局参数
			this.menuBtn[x].setGravity(Gravity.CENTER);
			this.menuBtn[x].setPadding(3, 3, 3, 3); 	// 设置边距
			this.menuBtn[x].setText(texts[x]);
		}

	}

	@Override
	public int getCount() {								// 返回全部数据个数
		return this.menuBtn.length;
	}

	@Override
	public Object getItem(int position) {				// 取得指定位置的对象
		return this.menuBtn[position];
	}

	@Override
	public long getItemId(int position) {				// 取得对象的ID
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView btn = null;
		if (convertView == null) {						// 判断是否存在转换的视图
			btn = this.menuBtn[position];			// 取得已有的视图
		} else {
			btn = (TextView) convertView;			// 取得已有的视图
		}
		return btn;									// 返回视图
	}

	public void setFocus(int selId) {					// 选中选项时触发
		for (int x = 0; x < this.menuBtn.length; x++) {
			if (x != selId) { 							// 不是当前选中项
				this.menuBtn[x].setBackgroundResource(0);	// 取消背景图片
			}
		}
		this.menuBtn[selId].setBackgroundResource(this.selectedMenuImg);	// 设置背景
	}
}
