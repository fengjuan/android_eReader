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

	public final static String SHEN_READER_PREF = "com.shen.shenreader";	// PREF�ı�ʶ
	public final static String PREF_TAG_FONT_SIZE = "tagFontSize";					// �����С�ı�ʶ
	public final static String PREF_TAG_FONT_COLOR = "tagFontColor";				// ������ɫ�ı�ʶ
	public final static String PREF_TAG_BACKGROUND_COLOR = "tagBgColor";			// ������ɫ�ı�ʶ
	public final static String PREF_TAG_SCREEN_BRIGHTNESS = "tagScrBrightness";		// ��Ļ���ȵı�ʶ

	/**
	 * �û�����
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
		adapterFontColor.add("��ɫ");
		adapterFontColor.add("��ɫ");
		adapterFontColor.add("��ɫ");
		adapterFontColor.add("��ɫ");
		adapterFontColor.add("��ɫ");
		spFontColor.setAdapter(adapterFontColor);

		adapterBgColor=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
		adapterBgColor.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		adapterBgColor.add("��ɫ");
		adapterBgColor.add("��ɫ");
		adapterBgColor.add("��ɫ");
		adapterBgColor.add("��ɫ");
		adapterBgColor.add("��ɫ");
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

		if( fontColor=="��ɫ")  spFontColor.setSelection(0);
		else if( fontColor=="��ɫ") spFontColor.setSelection(1);
		else if( fontColor=="��ɫ") spFontColor.setSelection(2);
		else if( fontColor=="��ɫ") spFontColor.setSelection(3);
		else if( fontColor=="��ɫ") spFontColor.setSelection(4);
		changeFontColor();

		if( bgColor=="��ɫ") spBgColor.setSelection(0);
		else if( bgColor=="��ɫ") spBgColor.setSelection(1);
		else if( bgColor=="��ɫ") spBgColor.setSelection(2);
		else if( bgColor=="��ɫ") spBgColor.setSelection(3);
		else if( bgColor=="��ɫ") spBgColor.setSelection(4);
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
		if( fontColor=="��ɫ" )
			tvPre.setTextColor(Color.WHITE);
		else if( fontColor=="��ɫ")
			tvPre.setTextColor(Color.BLACK);
		else if( fontColor=="��ɫ")
			tvPre.setTextColor(Color.RED);
		else if( fontColor=="��ɫ")
			tvPre.setTextColor(Color.GREEN);
		else if( fontColor=="��ɫ")
			tvPre.setTextColor(Color.BLUE);
	}

	private void changeBgColor()
	{
		if( bgColor=="��ɫ" )
			tvPre.setBackgroundColor(Color.WHITE);
		else if( bgColor=="��ɫ")
			tvPre.setBackgroundColor(Color.BLACK);
		else if( bgColor=="��ɫ")
			tvPre.setBackgroundColor(Color.RED);
		else if( bgColor=="��ɫ")
			tvPre.setBackgroundColor(Color.GREEN);
		else if( bgColor=="��ɫ")
			tvPre.setBackgroundColor(Color.BLUE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_SAVE_AND_RETURN, 0, "���沢����")
		.setIcon(android.R.drawable.ic_menu_save);

		return true;
	}

	// ���˵�����¼�
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
	 * ��ȡ֮ǰ�����õ�����
	 */
	 private void initFromSp()
	 {
		 sp=getSharedPreferences(SHEN_READER_PREF, Activity.MODE_PRIVATE);
		 fontColor=sp.getString(PREF_TAG_FONT_COLOR, "��ɫ");
		 bgColor=sp.getString(PREF_TAG_BACKGROUND_COLOR, "��ɫ");
		 fontSize=sp.getFloat(PREF_TAG_FONT_SIZE, 25.0f);
		 scrBright=sp.getFloat(PREF_TAG_SCREEN_BRIGHTNESS, 1.0f);
	 }


}
