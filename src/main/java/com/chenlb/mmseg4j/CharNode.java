package com.chenlb.mmseg4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CharNode implements Serializable{

	private static final long serialVersionUID = 4386091054856238888L;
	private int freq = -1;	
	private int maxLen = 0;

	private KeyTree ktWordTails = new KeyTree();
	private int wordNum = 0;
	
	public CharNode() {
		
	}
	
	public void addWordTail(char[] wordTail) {
		ktWordTails.add(wordTail);
		wordNum++;
		if(wordTail.length > maxLen) {
			maxLen = wordTail.length;
		}
	}
	public int getFreq() {
		return freq;
	}
	
	public void setFreq(int freq) {
		this.freq = freq;
	}
	
	public int wordNum() {
		return wordNum;
	}
	
	
	public int indexOf(char[] sen, int offset, int tailLen) {
		//return binarySearch(wordTails, sen, offset+1, tailLen, casc);
		return ktWordTails.match(sen, offset+1, tailLen) ? 1 : -1;
	}
	
	
	public int maxMatch(char[] sen, int wordTailOffset) {
		return ktWordTails.maxMatch(sen, wordTailOffset);
	}
	
	public ArrayList<Integer> maxMatch(ArrayList<Integer> tailLens, char[] sen, int wordTailOffset) {
		return ktWordTails.maxMatch(tailLens, sen, wordTailOffset);
	}
	
	public int getMaxLen() {
		return maxLen;
	}
	public void setMaxLen(int maxLen) {
		this.maxLen = maxLen;
	}
	
	public static class KeyTree implements Serializable{
		
		private static final long serialVersionUID = -2911121626868647781L;
		TreeNode head = new TreeNode(' ');
		
		public void add(char[] w) {
			if(w.length < 1) {
				return;
			}
			TreeNode p = head;
			for(int i=0; i<w.length; i++) {
				TreeNode n = p.subNode(w[i]);
				if(n == null) {
					n = new TreeNode(w[i]);
					p.born(w[i], n);
				}
				p = n;
			}
			p.alsoLeaf = true;
		}
		
		
		public int maxMatch(char[] sen, int offset) {
			int idx = offset - 1;
			TreeNode node = head;
			for(int i=offset; i<sen.length; i++) {
				node = node.subNode(sen[i]);
				if(node != null) {
					if(node.isAlsoLeaf()) {
						idx = i; 
					}
				} else {
					break;
				}
			}
			return idx - offset + 1;
		}
		
		public ArrayList<Integer> maxMatch(ArrayList<Integer> tailLens, char[] sen, int offset) {
			TreeNode node = head;
			for(int i=offset; i<sen.length; i++) {
				node = node.subNode(sen[i]);
				if(node != null) {
					if(node.isAlsoLeaf()) {
						tailLens.add(i-offset+1); 
					}
				} else {
					break;
				}
			}
			return tailLens;
		}
		
		public boolean match(char[] sen, int offset, int len) {
			TreeNode node = head;
			for(int i=0; i<len; i++) {
				node = node.subNode(sen[offset+i]);
				if(node == null) {
					return false;
				}
			}
			return node.isAlsoLeaf();
		}
	}
	
	private static class TreeNode implements Serializable{
		
		private static final long serialVersionUID = 1554966590187034207L;
		char key;
		Map<Character, TreeNode> subNodes;
		boolean alsoLeaf;
		public TreeNode(char key) {
			this.key = key;
			subNodes = new HashMap<Character, TreeNode>();
		}
		
		public void born(char k, TreeNode sub) {
			subNodes.put(k, sub);
		}
		
		public TreeNode subNode(char k) {
			return subNodes.get(k);
		}
		public boolean isAlsoLeaf() {
			return alsoLeaf;
		}
	}
}
