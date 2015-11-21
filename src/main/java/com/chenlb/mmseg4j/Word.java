package com.chenlb.mmseg4j;


public class Word {

	public static final String TYPE_WORD = "word";
	public static final String TYPE_LETTER = "letter";
	public static final String TYPE_LETTER_OR_DIGIT = "letter_or_digit";
	public static final String TYPE_DIGIT = "digit";
	public static final String TYPE_DIGIT_OR_LETTER = "digit_or_letter";
	public static final String TYPE_LETTER_NUMBER = "letter_number";
	public static final String TYPE_OTHER_NUMBER = "other_number";
	
	private int degree = -1;
	private int startOffset;
	
	private char[] sen;
	private int offset;
	private int len;
	
	private String type = TYPE_WORD;	

	
	public Word(char[] word, int startOffset) {
		super();
		this.sen = word;
		this.startOffset = startOffset;
		offset = 0;
		len = word.length;
	}
	
	
	public Word(char[] word, int startOffset, String wordType) {
		this(word, startOffset);
		this.type = wordType;
	}
	
	
	public Word(char[] sen, int senStartOffset, int offset, int len) {
		super();
		this.sen = sen;
		this.startOffset = senStartOffset;
		this.offset = offset;
		this.len = len;
	}

	public String getString() {
		return new String(getSen(), getWordOffset(), getLength());
	}
	
	public String toString() {
		return getString();
	}
	
	public int getWordOffset() {
		return offset;
	}
	
	public int getLength() {
		return len;
	}

	public char[] getSen() {
		return sen;
	}
	
	public int getStartOffset() {
		return startOffset+offset;
	}
	public int getEndOffset() {
		return getStartOffset() + getLength();
	}
	public int getDegree() {
		return degree;
	}
	public void setDegree(int degree) {
		this.degree = degree;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
