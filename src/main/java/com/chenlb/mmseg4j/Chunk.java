package com.chenlb.mmseg4j;



public class Chunk {

	Word[] words = new Word[3];
	
	int count = -1;
	
	private int len = -1;
	private double avgLen = -1;
	private double variance = -1;
	private int sumDegree = -1;
	
	public int getLen() {
		if(len < 0) {
			len = 0;
			count = 0;
			for(Word word : words) {
				if(word != null) {
					len += word.getLength();
					count++;
				}
			}
		}
		return len;
	}
	
	public int getCount() {
		if(count < 0) {
			count = 0;
			for(Word word : words) {
				if(word != null) {
					count++;
				}
			}
		}
		return count;
	}
	
	public double getAvgLen() {
		if(avgLen < 0) {
			avgLen = (double)getLen()/getCount();
		}
		return avgLen;
	}
	
	public double getVariance() {
		if(variance < 0) {
			double sum = 0;
			for(Word word : words) {
				if(word != null) {
					sum += Math.pow(word.getLength()-getAvgLen(), 2);
				}
			}
			variance = sum/getCount();
		}
		return variance;
	}
	
	public int getSumDegree() {
		if(sumDegree < 0) {
			int sum = 0;
			for(Word word : words) {
				if(word != null && word.getDegree() > -1) {
					sum += word.getDegree();
				}
			}
			sumDegree = sum;
		}
		return sumDegree;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Word word : words) {
			if(word != null) {
				sb.append(word.getString()).append('_');
			}
		}
		return sb.toString();
	}
	
	public String toFactorString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append("len=").append(getLen()).append(", ");
		sb.append("avgLen=").append(getAvgLen()).append(", ");
		sb.append("variance=").append(getVariance()).append(", ");
		sb.append("sum100log=").append(getSumDegree()).append("]");
		return sb.toString();
	}

	public Word[] getWords() {
		return words;
	}
	
	public void setWords(Word[] words) {
		this.words = words;
		count = words.length;
	}
}
