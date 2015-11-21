package com.chenlb.mmseg4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chenlb.mmseg4j.rule.LargestAvgLenRule;
import com.chenlb.mmseg4j.rule.LargestSumDegreeFreedomRule;
import com.chenlb.mmseg4j.rule.MaxMatchRule;
import com.chenlb.mmseg4j.rule.Rule;
import com.chenlb.mmseg4j.rule.SmallestVarianceRule;


public class ComplexSeg extends Seg implements Serializable{
	private static final long serialVersionUID = -4789457166053687505L;
	private MaxMatchRule mmr = new MaxMatchRule();
	private List<Rule> otherRules = new ArrayList<Rule>();
	
	private static boolean showChunk = false;
	
	public ComplexSeg(){
		super();
		otherRules.add(new LargestAvgLenRule());
		otherRules.add(new SmallestVarianceRule());
		otherRules.add(new LargestSumDegreeFreedomRule());
	}
	
	public ComplexSeg(Dictionary dic) {
		super(dic);
		otherRules.add(new LargestAvgLenRule());
		otherRules.add(new SmallestVarianceRule());
		otherRules.add(new LargestSumDegreeFreedomRule());
	}
	
	public Chunk seg(Sentence sen) {
		char[] chs = sen.getText();
		int[] tailLen = new int[3];	
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] tailLens = new ArrayList[2];	
		for(int i=0; i<2; i++) {
			tailLens[i] = new ArrayList<Integer>();
		}
		CharNode[] cns = new CharNode[3];
		
		int[] offsets = new int[3];	
		mmr.reset();
		if(!sen.isFinish()) {	
			if(showChunk) {
				System.out.println();
			}
			int maxLen = 0;
			offsets[0] = sen.getOffset();
			maxMatch(cns, 0, chs, offsets[0], tailLens, 0);
			for(int aIdx=tailLens[0].size()-1; aIdx>=0; aIdx--) {

				tailLen[0] = tailLens[0].get(aIdx);

				offsets[1] = offsets[0]+1+tailLen[0];	

				maxMatch(cns, 1, chs, offsets[1], tailLens, 1);
				for(int bIdx=tailLens[1].size()-1; bIdx>=0; bIdx--) {

					tailLen[1] = tailLens[1].get(bIdx);
					offsets[2] = offsets[1]+1+tailLen[1];

					tailLen[2] = maxMatch(cns, 2, chs, offsets[2]);

					int sumChunkLen = 0;
					for(int i=0; i<3; i++) {
						sumChunkLen += tailLen[i]+1;
					}
					Chunk ck = null;
					if(sumChunkLen >= maxLen) {
						maxLen = sumChunkLen;	
						ck = createChunk(sen, chs, tailLen, offsets, cns);
						mmr.addChunk(ck);

					}
					if(showChunk) {
						if(ck == null) {
							ck = createChunk(sen, chs, tailLen, offsets, cns);
							mmr.addChunk(ck);
						}
						System.out.println(ck);
					}

				}
			}
			sen.addOffset(maxLen);	
			List<Chunk> chunks = mmr.remainChunks();
			for(Rule rule : otherRules) {	
				if(showChunk) {
					printChunk(chunks);
				}
				if(chunks.size() > 1) {
					rule.reset();
					rule.addChunks(chunks);
					chunks = rule.remainChunks();
				} else {
					break;
				}
			}
			if(showChunk) {
				printChunk(chunks);
			}
			if(chunks.size() > 0) {
				return chunks.get(0);
			}
		}
		return null;
	}

	private Chunk createChunk(Sentence sen, char[] chs, int[] tailLen, int[] offsets, CharNode[] cns/*, char[][] cks*/) {
		Chunk ck = new Chunk();
		
		for(int i=0; i<3; i++) {

			if(offsets[i] < chs.length) {
				ck.words[i] = new Word(chs, sen.getStartOffset(), offsets[i], tailLen[i]+1);//new Word(cks[i], sen.getStartOffset()+offsets[i]);
				if(tailLen[i] == 0) {	
					CharNode cn = cns[i];	
					if(cn !=null) {
						ck.words[i].setDegree(cn.getFreq());
					}
				}
			}
		}
		return ck;
	}
	
	public static boolean isShowChunk() {
		return showChunk;
	}

	public static void setShowChunk(boolean showChunk) {
		ComplexSeg.showChunk = showChunk;
	}
}
