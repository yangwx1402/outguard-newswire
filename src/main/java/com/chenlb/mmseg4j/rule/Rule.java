package com.chenlb.mmseg4j.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.chenlb.mmseg4j.Chunk;


public abstract class Rule implements Serializable{


	private static final long serialVersionUID = -6172578204314990129L;
	protected List<Chunk> chunks;
	
	public void addChunks(List<Chunk> chunks) {
		for(Chunk chunk : chunks) {
			addChunk(chunk);
		}
	}
	

	public void addChunk(Chunk chunk) {
		chunks.add(chunk);
	}
	

	public List<Chunk> remainChunks() {
		for(Iterator<Chunk> it=chunks.iterator(); it.hasNext();) {
			Chunk chunk = it.next();
			if(isRemove(chunk)) {
				it.remove();
			}
		}
		return chunks;
	}
	

	protected abstract boolean isRemove(Chunk chunk);
	
	public void reset() {
		chunks = new ArrayList<Chunk>();
	}
}
