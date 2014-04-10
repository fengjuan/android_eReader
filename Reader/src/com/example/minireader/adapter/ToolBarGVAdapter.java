package com.example.minireader.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class ToolBarGVAdapter extends BaseAdapter {	// �̳�BaseAdapter
	private Context context; 						// ���������Ķ���
	private TextView[] menuBtn; 					// �������б�ǩ��ͼƬ��ʾ
	private int selectedMenuImg; 					// ����ѡ�е�ImageView����

	/**
	 * ����һ��MenuImageAdapter���ʵ��
	 * @param context �����Ķ���
	 * @param imgIds ���е�ͼƬ��Դ��ID����
	 * @param width ImageView����Ŀ��
	 * @param height ImageView�������ʾ�߶�
	 * @param selectedMenuImg ѡ�е�ImageView��ID
	 */
	public ToolBarGVAdapter(Context context, String texts[], int width,
			int height, int selectedMenuImg) {		// ������ղ���
		this.context = context; 					// ����Context����
		this.selectedMenuImg = selectedMenuImg; 	// ����ѡ�е�ID
		this.menuBtn = new TextView[texts.length]; // ʵ����Button����
		for (int x = 0; x < texts.length; x++) { 	// ʵ����ÿһ��Button
			this.menuBtn[x] = new TextView(this.context); // ʵ����Button����
			this.menuBtn[x].setLayoutParams(new GridView.LayoutParams(width,
					height));						// ����ͼƬ�Ĳ��ֲ���
			this.menuBtn[x].setGravity(Gravity.CENTER);
			this.menuBtn[x].setPadding(3, 3, 3, 3); 	// ���ñ߾�
			this.menuBtn[x].setText(texts[x]);
		}

	}

	@Override
	public int getCount() {								// ����ȫ�����ݸ���
		return this.menuBtn.length;
	}

	@Override
	public Object getItem(int position) {				// ȡ��ָ��λ�õĶ���
		return this.menuBtn[position];
	}

	@Override
	public long getItemId(int position) {				// ȡ�ö����ID
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView btn = null;
		if (convertView == null) {						// �ж��Ƿ����ת������ͼ
			btn = this.menuBtn[position];			// ȡ�����е���ͼ
		} else {
			btn = (TextView) convertView;			// ȡ�����е���ͼ
		}
		return btn;									// ������ͼ
	}

	public void setFocus(int selId) {					// ѡ��ѡ��ʱ����
		for (int x = 0; x < this.menuBtn.length; x++) {
			if (x != selId) { 							// ���ǵ�ǰѡ����
				this.menuBtn[x].setBackgroundResource(0);	// ȡ������ͼƬ
			}
		}
		this.menuBtn[selId].setBackgroundResource(this.selectedMenuImg);	// ���ñ���
	}
}
