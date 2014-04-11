package com.example.minireader;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingActivity extends Activity {

	final int MENU_SAVE_AND_RETURN = 1;

	public final static String SHEN_READER_PREF = "com.shen.shenreader";	// PREF的标识
	public final static String PREF_TAG_FONT_SIZE = "tagFontSize";					// 字体大小的标识
	public final static String PREF_TAG_FONT_COLOR = "tagFontColor";				// 字体颜色的标识
	public final static String PREF_TAG_BACKGROUND_COLOR = "tagBgColor";			// 背景颜色的标识
	public final static String PREF_TAG_SCREEN_BRIGHTNESS = "tagScrBrightness";		// 屏幕亮度的标识

	/**
	 * 用户设置
	 */
	SharedPreferences sp;

	String fontColor;
	String bgColor;
	float fontSize;
	float scrBright;

	Spinner spFontColor;
	Spinner spBgColor;
	SeekBar sbFontSize;
	SeekBar sbScrBright;

	TextView tvPre;

	ArrayAdapter<String> adapterFontColor;
	ArrayAdapter<String> adapterBgColor;
	OnSeekBarChangeListener osl;

	@Override
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		setContentView(R.layout.setting);
		initFromSp();

		spFontColor=(Spinner)findViewById(R.id.setting_spinner_font_color);
		spBgColor=(Spinner)findViewById(R.id.setting_spinner_bg_color);
		sbFontSize=(SeekBar)findViewById(R.id.setting_seekbar_font_size);
		sbScrBright=(SeekBar)findViewById(R.id.setting_seekbar_scr_brightness);
		tvPre=(TextView)findViewById(R.id.setting_textview_setting_prev);

		adapterFontColor=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
		adapterFontColor.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		adapterFontColor.add("白色");
		adapterFontColor.add("黑色");
		adapterFontColor.add("红色");
		adapterFontColor.add("绿色");
		adapterFontColor.add("蓝色");
		spFontColor.setAdapter(adapterFontColor);

		adapterBgColor=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
		adapterBgColor.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		adapterBgColor.add("白色");
		adapterBgColor.add("黑色");
		adapterBgColor.add("红色");
		adapterBgColor.add("绿色");
		adapterBgColor.add("蓝色");
		spBgColor.setAdapter(adapterBgColor);

		spFontColor.setOnItemSelectedListener
		(
				new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Spinner sp =(Spinner) arg0;
						fontColor=sp.getSelectedItem().toString();
						changeFontColor();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				}
				);

		spBgColor.setOnItemSelectedListener
		(
				new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Spinner sp =(Spinner) arg0;
						bgColor=sp.getSelectedItem().toString();
						changeBgColor();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				}
				);

		if( fontColor=="白色")  spFontColor.setSelection(0);
		else if( fontColor=="黑色") spFontColor.setSelection(1);
		else if( fontColor=="红色") spFontColor.setSelection(2);
		else if( fontColor=="绿色") spFontColor.setSelection(3);
		else if( fontColor=="蓝色") spFontColor.setSelection(4);
		changeFontColor();

		if( bgColor=="白色") spBgColor.setSelection(0);
		else if( bgColor=="黑色") spBgColor.setSelection(1);
		else if( bgColor=="红色") spBgColor.setSelection(2);
		else if( bgColor=="绿色") spBgColor.setSelection(3);
		else if( bgColor=="蓝色") spBgColor.setSelection(4);
		changeBgColor();

		tvPre.setTextSize(fontSize);
		tvPre.setMovementMethod(ScrollingMovementMethod.getInstance());

		WindowManager.LayoutParams lp=getWindow().getAttributes();
		lp.screenBrightness=scrBright;
		getWindow().setAttributes(lp);

		osl = new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				switch(seekBar.getId())
				{
				case R.id.setting_seekbar_font_size:
					tvPre.setTextSize((progress+1)*5.0f);
					fontSize=(progress+1)*5.0f;
					break;
				case R.id.setting_seekbar_scr_brightness:
					WindowManager.LayoutParams lp=getWindow().getAttributes();
					lp.screenBrightness=progress / 10.0f;
					getWindow().setAttributes(lp);
					scrBright = progress / 10.0f;
					break;
				}
			}
		};

		sbFontSize.setOnSeekBarChangeListener(osl);
		sbScrBright.setOnSeekBarChangeListener(osl);
	}

	private void changeFontColor()
	{
		if( fontColor=="白色" )
			tvPre.setTextColor(Color.WHITE);
		else if( fontColor=="黑色")
			tvPre.setTextColor(Color.BLACK);
		else if( fontColor=="红色")
			tvPre.setTextColor(Color.RED);
		else if( fontColor=="绿色")
			tvPre.setTextColor(Color.GREEN);
		else if( fontColor=="蓝色")
			tvPre.setTextColor(Color.BLUE);
	}

	private void changeBgColor()
	{
		if( bgColor=="白色" )
			tvPre.setBackgroundColor(Color.WHITE);
		else if( bgColor=="黑色")
			tvPre.setBackgroundColor(Color.BLACK);
		else if( bgColor=="红色")
			tvPre.setBackgroundColor(Color.RED);
		else if( bgColor=="绿色")
			tvPre.setBackgroundColor(Color.GREEN);
		else if( bgColor=="蓝色")
			tvPre.setBackgroundColor(Color.BLUE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_SAVE_AND_RETURN, 0, "保存并返回")
		.setIcon(android.R.drawable.ic_menu_save);

		return true;
	}

	// 主菜单点击事件
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_SAVE_AND_RETURN:
			saveToPref();
			this.finish();
			break;
		}

		return false;
	}

	private void saveToPref() {
		sp = getSharedPreferences(SHEN_READER_PREF, 0);
		SharedPreferences.Editor ed = sp.edit();

		ed.putString(PREF_TAG_FONT_COLOR, fontColor);
		ed.putString(PREF_TAG_BACKGROUND_COLOR, bgColor);
		ed.putFloat(PREF_TAG_FONT_SIZE, fontSize);
		ed.putFloat(PREF_TAG_SCREEN_BRIGHTNESS, scrBright);
		ed.commit();
	}

	/**
	 * 获取之前的设置的数据
	 */
	 private void initFromSp()
	 {
		 sp=getSharedPreferences(SHEN_READER_PREF, Activity.MODE_PRIVATE);
		 fontColor=sp.getString(PREF_TAG_FONT_COLOR, "白色");
		 bgColor=sp.getString(PREF_TAG_BACKGROUND_COLOR, "黑色");
		 fontSize=sp.getFloat(PREF_TAG_FONT_SIZE, 25.0f);
		 scrBright=sp.getFloat(PREF_TAG_SCREEN_BRIGHTNESS, 1.0f);
	 }


}
