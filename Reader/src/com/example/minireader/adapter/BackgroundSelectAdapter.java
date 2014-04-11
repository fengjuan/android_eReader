
package com.example.minireader.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * 
 *±≥æ∞—°‘Ò  ≈‰∆˜
 *@author fengjuan
 *
 */
public class BackgroundSelectAdapter extends BaseAdapter {

	private Context mContext;
	private int[] mImages;
	
	public BackgroundSelectAdapter(Context context, int[] images) {
		mContext = context;
		mImages = images;
	}
	
	@Override
	public int getCount() {
		return mImages.length;
	}

	@Override
	public Object getItem(int position) {
		return mImages[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView img = new ImageView(mContext);
		img.setImageResource(mImages[position]);
		img.setAdjustViewBounds(true);
		img.setLayoutParams(new Gallery.LayoutParams(80, 80));
		img.setPadding(20, 10, 20, 10);
		
		return img;
	}

}
