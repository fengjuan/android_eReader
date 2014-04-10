package com.example.minireader.file;


public class ChooseFileItem {
	enum Type {
		PARENT, DIR, DOC
	}

	final public Type type;
	final public String name;

	public ChooseFileItem (Type t, String n) {
		type = t;
		name = n;
	}
}
