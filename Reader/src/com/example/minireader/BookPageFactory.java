
package com.example.minireader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;

public class BookPageFactory {

	private File book_file = null;
	private MappedByteBuffer m_mbBuf = null;
	private int m_mbBufLen = 0;
	private int m_mbBufBegin = 4; //50
	private int m_mbBufEnd = 0;
	private String m_strCharsetName = "GBK";
	private Bitmap m_book_bg = null;
	private int mWidth;
	private int mHeight;

	private Vector<String> m_lines = new Vector<String>();

	private int m_fontSize = 30;
	private int m_textColor = Color.BLACK;
	private int m_backColor = 0xffff9e85; // ������ɫ
	private int marginWidth = 15; // �������Ե�ľ���
	private int marginHeight = 20; // �������Ե�ľ���
	private int youmiHeight = 0;//������Ŀ��

	private int mLineCount; // ÿҳ������ʾ������
	private float mVisibleHeight; // �������ݵĿ�
	private float mVisibleWidth; // �������ݵĿ�
	private boolean m_isfirstPage, m_islastPage;
	private int b_FontSize = 16;//�ײ����ִ�С
	private int spaceSize = 20;//�м���С
	private int curProgress = 0;//��ǰ�Ľ���
	private String fileName = "";

	private Paint mPaint;
	private Paint bPaint;//�ײ����ֻ���
	private Paint spactPaint;//�м�����
	private Paint titlePaint;//�������

	public BookPageFactory(int w, int h) {
		// TODO Auto-generated constructor stub
		mWidth = w;
		mHeight = h;
		//��������
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextAlign(Align.LEFT);
		mPaint.setTextSize(m_fontSize);
		mPaint.setColor(m_textColor);
	
		mVisibleWidth = mWidth - marginWidth * 2;
		mVisibleHeight = mHeight - marginHeight * 2 - youmiHeight;
		int totalSize = m_fontSize+spaceSize;
		mLineCount = (int) ((mVisibleHeight)/ totalSize); // ����ʾ������
		
		//�ײ����ֻ���
		bPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		bPaint.setTextAlign(Align.LEFT);
		bPaint.setTextSize(b_FontSize);
		bPaint.setColor(m_textColor);
		
		//�м������
		spactPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		spactPaint.setTextAlign(Align.LEFT);
		spactPaint.setTextSize(spaceSize);
		spactPaint.setColor(m_textColor);
		
		//
		titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		titlePaint.setTextAlign(Align.LEFT);
		titlePaint.setTextSize(30);
		titlePaint.setColor(m_textColor);
		
	}

	//�򿪵�����
	public void openbook(String strFilePath) {
		try {
			book_file = new File(strFilePath);
			long lLen = book_file.length();
			//�õ��ļ�����
			m_mbBufLen = (int) lLen;
			m_mbBuf = new RandomAccessFile(book_file, "r").getChannel().map(
					FileChannel.MapMode.READ_ONLY, 0, lLen);
			m_strCharsetName = getFilecharset(book_file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 /**
	  * �ж��ļ��ı���
	  * 
	  * @param sourceFile ��Ҫ�жϱ�����ļ� 
	  * @return String �ļ�����
	  */
	protected  String getFilecharset(File sourceFile) {
	  String charset = "GBK";
	  byte[] first3Bytes = new byte[3];
	  try {
	   
	   BufferedInputStream bis = new BufferedInputStream(
	     new FileInputStream(sourceFile));
	   bis.mark(0);
	   
	   int read = bis.read(first3Bytes, 0, 3);
	   System.out.println("�ֽڴ�С��"+read);
	   
	   if (read == -1) {
		   charset ="GBK"; //�ļ�����Ϊ ANSI
	   } else if (first3Bytes[0] == (byte) 0xFF
	     && first3Bytes[1] == (byte) 0xFE) {
	    
	    charset = "UTF-16LE"; //�ļ�����Ϊ Unicode
	   } else if (first3Bytes[0] == (byte) 0xFE
	     && first3Bytes[1] == (byte) 0xFF) {
	    
	    charset = "UTF-16BE"; //�ļ�����Ϊ Unicode big endian
	   } else if (first3Bytes[0] == (byte) 0xEF
	     && first3Bytes[1] == (byte) 0xBB
	     && first3Bytes[2] == (byte) 0xBF) {
	    
	    charset = "UTF-8"; //�ļ�����Ϊ UTF-8
	   }
	   bis.reset();
	   
	   bis.close();
	  } catch (Exception e) {
	   e.printStackTrace();
	  }
	  System.out.println("�ļ���ʽΪ��" + charset);
	  return charset;
	}


	//��ȡ��һ��
	protected byte[] readParagraphBack(int nFromPos) {
		int nEnd = nFromPos;
		int i;
		byte b0, b1;
		if (m_strCharsetName.equals("UTF-16LE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}

		} else if (m_strCharsetName.equals("UTF-16BE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}
		} else {
			i = nEnd - 1;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				if (b0 == 0x0a && i != nEnd - 1) {
					i++;
					break;
				}
				i--;
			}
		}
		if (i < 0)
			i = 0;
		int nParaSize = nEnd - i;
		int j;
		byte[] buf = new byte[nParaSize];
		for (j = 0; j < nParaSize; j++) {
			buf[j] = m_mbBuf.get(i + j);
		}
		return buf;
	}

	// ��ȡ��һ����
	protected byte[] readParagraphForward(int nFromPos) {
		int nStart = nFromPos;
		int i = nStart;
		byte b0, b1;
		// ���ݱ����ʽ�жϻ���
		if (m_strCharsetName.equals("UTF-16LE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x0a && b1 == 0x00) {
					break;
				}
			}
		} else if (m_strCharsetName.equals("UTF-16BE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x00 && b1 == 0x0a) {
					break;
				}
			}
		} else {
			while (i < m_mbBufLen) {
				b0 = m_mbBuf.get(i++);
				if (b0 == 0x0a) {
					break;
				}
			}
		}
		int nParaSize = i - nStart;
		byte[] buf = new byte[nParaSize];
		for (i = 0; i < nParaSize; i++) {
			buf[i] = m_mbBuf.get(nFromPos + i);
		}
		return buf;
	}

	//�õ���һҳ����
	protected Vector<String> pageDown() {
		String strParagraph = "";
		Vector<String> lines = new Vector<String>();
		while (lines.size() < mLineCount && m_mbBufEnd < m_mbBufLen) {
			byte[] paraBuf = readParagraphForward(m_mbBufEnd); // ��ȡһ������
			m_mbBufEnd += paraBuf.length;
			try {
				strParagraph = new String(paraBuf, m_strCharsetName);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String strReturn = "";
			if (strParagraph.indexOf("\r\n") != -1) {
				strReturn = "\r\n";
				strParagraph = strParagraph.replaceAll("\r\n", "");
			} else if (strParagraph.indexOf("\n") != -1) {
				strReturn = "\n";
				strParagraph = strParagraph.replaceAll("\n", "");
			}

			if (strParagraph.length() == 0) {
				lines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				lines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
				if (lines.size() >= mLineCount) {
					break;
				}
			}
			if (strParagraph.length() != 0) {
				try {
					m_mbBufEnd -= (strParagraph + strReturn)
							.getBytes(m_strCharsetName).length;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return lines;
	}

	//�õ���һҳ����
	protected void pageUp() {
		if (m_mbBufBegin < 0)
			m_mbBufBegin = 0;
		Vector<String> lines = new Vector<String>();
		String strParagraph = "";
		while (lines.size() < mLineCount && m_mbBufBegin > 0) {
			Vector<String> paraLines = new Vector<String>();
			byte[] paraBuf = readParagraphBack(m_mbBufBegin);
			m_mbBufBegin -= paraBuf.length;
			try {
				strParagraph = new String(paraBuf, m_strCharsetName);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			strParagraph = strParagraph.replaceAll("\r\n", "");
			strParagraph = strParagraph.replaceAll("\n", "");

			if (strParagraph.length() == 0) {
				//paraLines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				paraLines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
			}
			lines.addAll(0, paraLines);
		}
		while (lines.size() > mLineCount) {
			try {
				m_mbBufBegin += lines.get(0).getBytes(m_strCharsetName).length;
				lines.remove(0);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		m_mbBufEnd = m_mbBufBegin;
		return;
	}

	//��һҳ
	protected void prePage() throws IOException {
		if (m_mbBufBegin <= 0) {
			m_mbBufBegin = 0;
			m_isfirstPage = true;
			return;
		} else
			m_isfirstPage = false;
		m_lines.clear();
		pageUp();
		m_lines = pageDown();
	}

	//��һҳ
	public void nextPage() throws IOException {
		if (m_mbBufEnd >= m_mbBufLen) {
			m_islastPage = true;
			return;
		} else
			m_islastPage = false;
		m_lines.clear();
		m_mbBufBegin = m_mbBufEnd;
		m_lines = pageDown();
	}

	public void draw(Canvas c) {
		if (m_lines.size() == 0)
			m_lines = pageDown();
		if (m_lines.size() > 0) {
			if (m_book_bg == null)
				c.drawColor(m_backColor);
			else
				c.drawBitmap(m_book_bg, 0, 0, null);
			int y = marginHeight + youmiHeight;
			int i = 0;
			for (String strLine : m_lines) {
				y += m_fontSize;
				//mPaint.setTypeface(Typeface.DEFAULT_BOLD);
				c.drawText(strLine, marginWidth, y, mPaint);
				y+=spaceSize;
				if(i!=m_lines.size()-1){
					c.drawText("", marginWidth, y, spactPaint);
				}
				i++;
			}
		}
		float fPercent = (float) (m_mbBufBegin * 1.0 / m_mbBufLen);
		DecimalFormat df = new DecimalFormat("#0.0");
		String strPercent = df.format(fPercent * 100) + "%";
		
		curProgress = (int)round1(fPercent * 100,0);
		int nPercentWidth = (int) bPaint.measureText("99.9%") + 1;
		c.drawText(strPercent, mWidth - nPercentWidth, mHeight-5, bPaint);
		
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");      
		Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��      
		String str = formatter.format(curDate);  
		c.drawText(str, 5, mHeight-5, bPaint);
		int titleWidth = (int) bPaint.measureText("��"+fileName+"��") + 1;
		c.drawText("��"+fileName+"��", (mWidth-titleWidth)/2, mHeight-5, bPaint);
	}

	private static double round1(double v, int scale) {
		if (scale < 0)
		return v;
		String temp = "#####0.";
		for (int i = 0; i < scale; i++) {
		temp += "0";
		}
		System.out.println("bookpageFactory temp:++++++++" + temp);
		return Double.valueOf(new java.text.DecimalFormat(temp).format(v));
		}

	public void setBgBitmap(Bitmap BG) {
		if (BG.getWidth() != mWidth || BG.getHeight() != mHeight)
			m_book_bg = Bitmap.createScaledBitmap(BG, mWidth, mHeight, true);
		else
			m_book_bg = BG;
	}
	 
	public boolean isfirstPage() {
		return m_isfirstPage;
	}

	public void setIslastPage(boolean islast){
		m_islastPage = islast;
	}
	public boolean islastPage() {
		return m_islastPage;
	} 
	public int getCurPostion() {
		return m_mbBufEnd;
	}
	
	public int getCurPostionBeg(){
		return m_mbBufBegin;
	}
	public void setBeginPos(int pos) {
		m_mbBufEnd = pos;
		m_mbBufBegin = pos;
	}
	
	public int getBufLen() {
		return m_mbBufLen;
	}
	
	public int getCurProgress(){
		return curProgress;
	}
	public String getOneLine() {
		return m_lines.get(0);
	}
	
	public void changBackGround(int color) {
		mPaint.setColor(color);
	}
	

	public void setFontSize(int size) {
		m_fontSize = size;
		mPaint.setTextSize(size);
		int totalSize = m_fontSize+spaceSize;
		mLineCount = (int) (mVisibleHeight / totalSize); // ����ʾ������
	}
	
	public void setFileName(String fileName){
		fileName = fileName.substring(0,fileName.indexOf("."));
		this.fileName = fileName; 
	}
	
}
