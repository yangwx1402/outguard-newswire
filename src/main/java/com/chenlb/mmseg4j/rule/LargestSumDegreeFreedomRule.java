package com.chenlb.mmseg4j.rule;

import com.chenlb.mmseg4j.Chunk;


public class LargestSumDegreeFreedomRule extends Rule {

	private int largestSumDegree = Integer.MIN_VALUE;
	@Override
	public void addChunk(Chunk chunk) {
		if(chunk.getSumDegree() >= largestSumDegree) {
			largestSumDegree = chunk.getSumDegree();
			super.addChunk(chunk);
		}
	}

	@Override
	public void reset() {
		largestSumDegree = Integer.MIN_VALUE;
		super.reset();
	}

	@Override
	protected boolean isRemove(Chunk chunk) {
		
		return chunk.getSumDegree() < largestSumDegree;
	}

}
