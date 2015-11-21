package com.chenlb.mmseg4j;


public class Sentence {

	private char[] text;
	private int startOffset;
	
	private int offset;

	public Sentence() {
		text = new char[0];
	}
	
	public Sentence(char[] text, int startOffset) {
		reinit(text, startOffset);
	}

	public void reinit(char[] text, int startOffset) {
		this.text = text;
		this.startOffset = startOffset;
		offset = 0;
	}
	
	public char[] getText() {
		return text;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void addOffset(int inc) {
		offset += inc;
	}

	public boolean isFinish() {
		return offset >= text.length;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}
}
