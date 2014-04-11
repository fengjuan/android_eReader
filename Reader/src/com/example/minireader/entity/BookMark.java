/**
 *  Author :  hmg25
 *  Description :
 */
package com.example.minireader.entity;

import java.io.Serializable;

/**
 * hmg25's android Type
 *
 *@author fengjuan
 *
 */
public class BookMark implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String bookName;
	private String markName;
	private int begin;
	
	public BookMark() {
	}
	
	public BookMark(String bookName, String markName, int begin) {
		super();
		this.bookName = bookName;
		this.markName = markName;
		this.begin = begin;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getBookName() {
		return bookName;
	}
	
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	
	public String getMarkName() {
		return markName;
	}
	
	public void setMarkName(String markName) {
		this.markName = markName;
	}
	
	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	@Override
	public String toString() {
		return markName;
	}
	
	
}
