package com.chenlb.mmseg4j.rule;

import com.chenlb.mmseg4j.Chunk;


public class MaxMatchRule extends Rule{

	private int maxLen;
	
	public void addChunk(Chunk chunk) {
		if(chunk.getLen() >= maxLen) {
			maxLen = chunk.getLen();
			super.addChunk(chunk);
		}
	}
	
	@Override
	protected boolean isRemove(Chunk chunk) {
		
		return chunk.getLen() < maxLen;
	}

	public void reset() {
		maxLen = 0;
		super.reset();
	}
}
