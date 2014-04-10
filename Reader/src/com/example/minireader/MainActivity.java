package com.example.minireader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.example.minireader.file.ChooseFileActivity;
import com.example.minireader.online.WebViewActivity;

public class MainActivity extends Activity {
    
    private ImageButton mImgBtnLocal;
    private ImageButton mImgBtnOnLine;
    private ImageButton mImgBtnBook;
    private ImageButton mImgBtnExit;
    
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // find view 
        mImgBtnLocal = (ImageButton) findViewById(R.id.img_btn_local);
        mImgBtnOnLine = (ImageButton) findViewById(R.id.img_btn_online);
        mImgBtnBook = (ImageButton) findViewById(R.id.img_btn_book);
        mImgBtnExit = (ImageButton) findViewById(R.id.img_btn_exit);
        
        
        mImgBtnLocal.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View view) {
                mIntent = new Intent(MainActivity.this, ChooseFileActivity.class);
                startActivity(mIntent);
            }
        });
        
        mImgBtnOnLine.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View view) {
                mIntent = new Intent(MainActivity.this, WebViewActivity.class);
                startActivity(mIntent);
            }
        });
        
        mImgBtnBook.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View view) {
                mIntent = new Intent(MainActivity.this, BookShelfActivity.class);
                startActivity(mIntent);
            }
        });
        
        mImgBtnExit.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View view) {
            	exitDialog();
            }
        });
    }
    
    private void exitDialog() {
    	 Dialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("退出程序").setMessage("确定退出阅读器吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					System.exit(0);
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			}).create();
            dialog.show();
    }
    

    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
    		exitDialog();
    	}
		return super.onKeyDown(keyCode, event);
	}




	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
