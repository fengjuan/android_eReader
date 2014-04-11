
package com.example.minireader.entity;

import java.io.Serializable;

/**
 *@author fengjuan, 2014-4-2
 *
 */

public class SetupInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int fontsize;
	private int fontcolor;
	private int backgroud;
	
	public SetupInfo() {
	}
	
	
	public SetupInfo(int fontsize, int fontcolor, int backgroud) {
		this.fontsize = fontsize;
		this.fontcolor = fontcolor;
		this.backgroud = backgroud;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFontsize() {
		return fontsize;
	}
	public void setFontsize(int fontsize) {
		this.fontsize = fontsize;
	}
	public int getFontcolor() {
		return fontcolor;
	}
	public void setFontcolor(int fontcolor) {
		this.fontcolor = fontcolor;
	}
	public int getBackgroud() {
		return backgroud;
	}
	public void setBackgroud(int backgroud) {
		this.backgroud = backgroud;
	}


	@Override
	public String toString() {
		return "SetupInfo [id=" + id + ", fontsize=" + fontsize
				+ ", fontcolor=" + fontcolor + ", backgroud=" + backgroud + "]";
	}

	
}
