package com.example.minireader.file;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.minireader.R;

public class ChooseFileAdapter extends BaseAdapter {
	private final LinkedList<ChooseFileItem> mItems;	//填充的数据
	private final LayoutInflater mInflater;
	private static HashMap<Integer, Boolean> mIsSelected;  // 用来控制CheckBox的选中状况  

	
	
	public ChooseFileAdapter(Context context) {
		mItems = new LinkedList<ChooseFileItem>();
		mInflater = LayoutInflater.from(context);
		mIsSelected = new HashMap<Integer, Boolean>();
	}

	//初始化mIsSelected数据
	private void initSelectData(int position) {
			getmIsSelected().put(position, false);
	}

	public void clear() {
		mItems.clear();
	}

	int i =0;
	public void add(ChooseFileItem list) {
		mItems.add(list);
		initSelectData(i);
		i++;
		notifyDataSetChanged();
	}
	
	public List<ChooseFileItem> getData() {
		return mItems;
	}

	public int getCount() {
		return mItems.size();
	}

	public Object getItem(int i) {
		return mItems.get(i);
	}

	public long getItemId(int position) {
		return position;
	}

	private int iconForType(ChooseFileItem.Type type) {
		switch (type) {
		case PARENT: return R.drawable.ic_arrow_up;
		case DIR: return R.drawable.ic_dir;
		case DOC: return R.drawable.ic_doc;
		default: return 0;
		}
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			// 导入布局并赋值给convertview 
			convertView = mInflater.inflate(R.layout.picker_entry, null);
			holder.name = (TextView)convertView.findViewById(R.id.name);
			holder.icon = (ImageView)convertView.findViewById(R.id.icon);
			holder.isSelected = (CheckBox)convertView.findViewById(R.id.check_file);
			// 为view设置标签 
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		ChooseFileItem item = mItems.get(position);
		
		holder.name.setText(item.name.substring(item.name.lastIndexOf("/")+1));
		holder.icon.setImageResource(iconForType(item.type));
		holder.icon.setColorFilter(Color.argb(0, 17, 194, 238));
		holder.isSelected.setChecked(getmIsSelected().get(position));
		
		if(!(item.type == ChooseFileItem.Type.DOC)) {
			holder.isSelected.setVisibility(View.INVISIBLE);
		} else {
			holder.isSelected.setVisibility(View.VISIBLE);
		}
	
		return convertView;
	}

	public static HashMap<Integer, Boolean> getmIsSelected() {
		return mIsSelected;
	}

	public static void setmIsSelected(HashMap<Integer, Boolean> mIsSelected) {
		ChooseFileAdapter.mIsSelected = mIsSelected;
	}

	
}
