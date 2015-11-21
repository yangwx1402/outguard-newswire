package com.chenlb.mmseg4j;

import java.util.ArrayList;
import java.util.List;


public class MaxWordSeg extends ComplexSeg {

	public MaxWordSeg(Dictionary dic) {
		super(dic);
	}

	public Chunk seg(Sentence sen) {

		Chunk chunk = super.seg(sen);
		if(chunk != null) {
			List<Word> cks = new ArrayList<Word>();
			for(int i=0; i<chunk.getCount(); i++) {
				Word word = chunk.words[i];

				if(word.getLength() < 3) {
					cks.add(word);
				} else {
					char[] chs = word.getSen();
					int offset = word.getWordOffset(), n = 0, wordEnd = word.getWordOffset()+word.getLength();
					int senStartOffset = word.getStartOffset() - offset;	
					int end = -1;
					for(; offset<wordEnd-1; offset++) {
						int idx = search(chs, offset, 1);
						if(idx > -1) {
							cks.add(new Word(chs, senStartOffset, offset, 2));
							end = offset+2;
							n++;
						} else if(offset >= end) {	
							cks.add(new Word(chs, senStartOffset, offset, 1));
							end = offset+1;

						}
					}
					if(end > -1 && end < wordEnd) {
						cks.add(new Word(chs, senStartOffset, offset, 1));
					}
				}

			}
			chunk.words = cks.toArray(new Word[cks.size()]);
			chunk.count = cks.size();
		}

		return chunk;
	}

}
