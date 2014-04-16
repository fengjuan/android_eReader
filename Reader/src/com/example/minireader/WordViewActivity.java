/* Copyright 2010 Bian LiJun
2  *
3  * Licensed under the Apache License, Version 2.0 (the "License");
4  * you may not use this file except in compliance with the License.
5  * You may obtain a copy of the License at
6  *
7  * http://www.apache.org/licenses/LICENSE-2.0
8  *
9  * Unless required by applicable law or agreed to in writing, software
10  * distributed under the License is distributed on an "AS IS" BASIS,
11  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
12  * See the License for the specific language governing permissions and
13  * limitations under the License.
14  */

package com.example.minireader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

public class WordViewActivity extends Activity{
	
	private String nameStr = null;
	
	private Range range = null;
	private HWPFDocument hwpf = null;
	
	private String htmlPath;
	private String picturePath;

	private WebView view;
	
	private List pictures;
	
	private TableIterator tableIterator;
	
	private int presentPicture = 0;
	
	private int screenWidth;
	
	private FileOutputStream output;
	
	private File myFile;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.word_view);
		
		view = (WebView)findViewById(R.id.show);
		view.getSettings().setBuiltInZoomControls(true);
		
		screenWidth = this.getWindowManager().getDefaultDisplay().getWidth() - 10;
		
		Intent intent = this.getIntent();
		
		nameStr = intent.getStringExtra("filePath");
	
		getRange();	
		
		makeFile();
		
		readAndWrite();
		
		view.loadUrl("file://" + htmlPath);
		System.out.println("htmlPath" + htmlPath);
	}
	
	/**
	 * 创建html文件
	 */
	public void makeFile(){
		
		try{
			File path = WordViewActivity.this.getDir("word", 4);
			if(!path.exists()){
				path.mkdir();
			}
			String temp = path.getAbsolutePath() + File.separator + "templete.html";
			File myFile = new File(temp);
			
			if(!myFile.exists()){
				myFile.createNewFile();
			}
			
			htmlPath = myFile.getAbsolutePath();
		}
		catch(Exception e){

		}
	}
	
	/**用来在sdcard上创建图片*/
	public void makePictureFile(){
			File path = WordViewActivity.this.getDir("word", 4);
			try{
				if(!path.exists()){
					path.mkdir();
				}
				File pictureFile = new File(path.getAbsoluteFile() + File.separator + presentPicture + ".jpg");
				
				if(!pictureFile.exists()){
					pictureFile.createNewFile();
				}
				
				picturePath = pictureFile.getAbsolutePath();
				
			}
			catch(Exception e){
				System.out.println("PictureFile Catch Exception");
			}
	}
	
	public void onDestroy(){
		super.onDestroy();
	}
	
	/*读取word中的内容写到sdcard上的.html文件中*/
	public void readAndWrite(){
		
		try{
			myFile = new File(htmlPath);
			output = new FileOutputStream(myFile);
			String head = "<html><body>";
			String tagBegin = "<p>";
			String tagEnd = "</p>";
			
			output.write(head.getBytes());
			
			//得到段落数
			int numParagraphs = range.numParagraphs();
			
			//一段一段写文件
			for(int i = 0; i < numParagraphs; i++){
				Paragraph p = range.getParagraph(i);
				
				//如果是表格
				if(p.isInTable()){
					int temp = i;
					if(tableIterator.hasNext()){
						String tableBegin = "<table style=\"border-collapse:collapse\" border=1 bordercolor=\"black\">";
						String tableEnd = "</table>";
						String rowBegin = "<tr>";
						String rowEnd = "</tr>";
						String colBegin = "<td>";
						String colEnd = "</td>";
						
						Table table = tableIterator.next();
						//写表头
						output.write(tableBegin.getBytes());
						
						int rows = table.numRows();
						//表格行
						for( int r = 0; r < rows; r++){
							output.write(rowBegin.getBytes());
							TableRow row = table.getRow(r);
							int cols = row.numCells();
							int rowNumParagraphs = row.numParagraphs();
							int colsNumParagraphs = 0;
							//表格列
							for( int c = 0; c < cols; c++){
								output.write(colBegin.getBytes());
								TableCell cell = row.getCell(c);
								int max = temp + cell.numParagraphs();
								colsNumParagraphs = colsNumParagraphs + cell.numParagraphs();
								for(int cp = temp; cp < max; cp++){
									Paragraph p1 = range.getParagraph(cp);
									output.write(tagBegin.getBytes());
									writeParagraphContent(p1);
									output.write(tagEnd.getBytes());
									temp++;
								}
								output.write(colEnd.getBytes());
							}
							int max1 = temp + rowNumParagraphs;
							for(int m = temp + colsNumParagraphs; m < max1; m++){
								Paragraph p2 = range.getParagraph(m);
								temp++;
							}
							output.write(rowEnd.getBytes());
						}
						output.write(tableEnd.getBytes());
					}
					i = temp;
				}
				else{
					output.write(tagBegin.getBytes());
					writeParagraphContent(p);
					output.write(tagEnd.getBytes());
				}
			}

			String end = "</body></html>";
			output.write(end.getBytes());
			output.close();
		}
		catch(Exception e){
			System.out.println("readAndWrite Exception" );
			e.printStackTrace();
		}
	}
	
	/**以段落的形式来往html文件中写内容*/
	public void writeParagraphContent(Paragraph paragraph){
		Paragraph p = paragraph;
		int pnumCharacterRuns = p.numCharacterRuns();
		
		for( int j = 0; j < pnumCharacterRuns; j++){
	
			CharacterRun run = p.getCharacterRun(j);
			
			if(run.getPicOffset() == 0 || run.getPicOffset() >= 1000){
				if(presentPicture < pictures.size()){
					//写图片
					writePicture();
				}
			}else {
				try{
					String text = run.text();
					if(text.length() >= 2 && pnumCharacterRuns < 2){
						//写文本
						output.write(text.getBytes());
					}else {
						//字体颜色处理
						int size = run.getFontSize();
						int color = run.getColor();
						String fontSizeBegin = "<font size=\"" + decideSize(size) + "\">";
						String fontColorBegin = "<font color=\"" + decideColor(color) + "\">";
						String fontEnd = "</font>";
						String boldBegin = "<b>";
						String boldEnd = "</b>";
						String islaBegin = "<i>";
						String islaEnd = "</i>";
	
						output.write(fontSizeBegin.getBytes());
						output.write(fontColorBegin.getBytes());
						
						if(run.isBold()){
							output.write(boldBegin.getBytes());
						}
						if(run.isItalic()){
							output.write(islaBegin.getBytes());
						}
						
						output.write(text.getBytes());
						
						if(run.isBold()){
							output.write(boldEnd.getBytes());
						}
						if(run.isItalic()){
							output.write(islaEnd.getBytes());
						}
						output.write(fontEnd.getBytes());
						output.write(fontEnd.getBytes());
					}
				}
				catch(Exception e){
					System.out.println("Write File Exception");
				}
			}
		}
	}
	
	/**将word中的图片写入到.jpg文件中*/
	public void writePicture(){
		Picture picture = (Picture)pictures.get(presentPicture);
		
		byte[] pictureBytes = picture.getContent();
		
		Bitmap bitmap = BitmapFactory.decodeByteArray(pictureBytes, 0, pictureBytes.length);
		
		makePictureFile();
		presentPicture++;
		
		File myPicture = new File(picturePath);
		
		try{
		
			FileOutputStream outputPicture = new FileOutputStream(myPicture);
		
			outputPicture.write(pictureBytes);
		
			outputPicture.close();
		}
		catch(Exception e){
			System.out.println("outputPicture Exception");
		}
		
		String imageString = "<img src=\"" + picturePath + "\"";
		
		if(bitmap.getWidth() > screenWidth){
			imageString = imageString + " " + "width=\"" + screenWidth + "\"";
		}
		imageString = imageString + ">";
		
		try{
			output.write(imageString.getBytes());
		}
		catch(Exception e){
			System.out.println("output Exception");
		}
	}
	/*处理word和html字体的转换*/
	public int decideSize(int size){
		System.out.println(size);
		
		if(size >= 1 && size <= 14){
			return 1;
		}
		if(size >= 15 && size <= 20){
			return 2;
		}
		if(size >= 21 && size <= 25){
			return 3;
		}
		if(size >= 26 && size <= 30){
			return 4;
		}
		if(size >= 31){
			return 5;
		}
		return 2;
	}
	/*处理word和html颜色的转换*/
	private String decideColor(int a){
		int color = a;
		switch(color){
		case 1:
			return "#000000";
		case 2:
			return "#0000FF";
		case 3:
		case 4:
			return "#00FF00";
		case 5:
		case 6:
			return "#FF0000";
		case 7:
			return "#FFFF00";
		case 8:
			return "#FFFFFF";
		case 9:
			return "#CCCCCC";
		case 10:
		case 11:
			return "#00FF00";
		case 12:
			return "#080808";
		case 13:
		case 14: 
			return "#FFFF00";
		case 15: 
			return "#CCCCCC";
		case 16:
			return "#080808";
		default:
			return "#000000";
		}
	}
	
	private void getRange(){
		FileInputStream in = null;
		POIFSFileSystem pfs = null;
		try{
			in = new FileInputStream(nameStr);
			pfs = new POIFSFileSystem(in);
			hwpf = new HWPFDocument(pfs);
		}
		catch(Exception e){
			
		}
		range = hwpf.getRange();
		//得到所有的图片
		pictures = hwpf.getPicturesTable().getAllPictures();
		
		tableIterator = new TableIterator(range);
		
	}
	/*处理点击返回按钮*/
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	   	if (keyCode == KeyEvent.KEYCODE_BACK) {
	   		return false;
	   	}
		return false;	
	 }
}
