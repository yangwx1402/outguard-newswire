package com.chenlb.mmseg4j;

import java.util.ArrayList;
import java.util.List;

public abstract class Seg {

	protected Dictionary dic;
	
	public Seg(){
		dic = Dictionary.getInstance("data");
	}
	
	public Seg(Dictionary dic) {
		super();
		this.dic = dic;
	}


	protected void printChunk(List<Chunk> chunks) {
		for(Chunk ck : chunks) {
			System.out.println(ck+" -> "+ck.toFactorString());
		}
	}

	protected boolean isUnit(int codePoint) {
		return dic.isUnit((char) codePoint);
	}
	

	protected int search(char[] chs, int offset, int tailLen) {
		if(tailLen == 0) {
			return -1;
		}
		CharNode cn = dic.head(chs[offset]);
		
		return search(cn, chs, offset, tailLen);
	}

	protected int search(CharNode cn, char[] chs, int offset, int tailLen) {
		if(tailLen == 0 || cn == null) {
			return -1;
		}
		return dic.search(cn, chs, offset, tailLen);
	}
	

	protected int maxMatch(CharNode[] cns, int cnIdx, char[] chs, int offset) {
		CharNode cn = null;
		if(offset < chs.length) {
			cn = dic.head(chs[offset]);
		}
		cns[cnIdx] = cn;
		return dic.maxMatch(cn, chs, offset);
	}
	

	protected void maxMatch(CharNode[] cns, int cnIdx, char[] chs, int offset, ArrayList<Integer>[] tailLens, int tailLensIdx) {
		CharNode cn = null;
		if(offset < chs.length) {
			cn = dic.head(chs[offset]);
		}
		cns[cnIdx] = cn;
		dic.maxMatch(cn, tailLens[tailLensIdx], chs, offset);
	}
	

	public abstract Chunk seg(Sentence sen);
}
