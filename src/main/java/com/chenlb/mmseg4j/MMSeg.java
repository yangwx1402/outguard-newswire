package com.chenlb.mmseg4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.Queue;


public class MMSeg {
	
	private PushbackReader reader;
	private Seg seg;
	
	private StringBuilder bufSentence = new StringBuilder(256);
	private Sentence currentSentence;
	private Queue<Word> bufWord;	
	
	public MMSeg(Reader input, Seg seg) {
		this.seg = seg;
		
		reset(input);
	}

	private int readedIdx = 0;
	
	public void reset(Reader input) {
		this.reader = new PushbackReader(new BufferedReader(input), 20);
		currentSentence = null;
		bufWord = new LinkedList<Word>();
		bufSentence.setLength(0);
		readedIdx = -1;
	}
	
	private int readNext() throws IOException {
		int d = reader.read();
		if(d > -1) {
			readedIdx++;
			d = Character.toLowerCase(d);
		}
		return d;
	}
	
	private void pushBack(int data) throws IOException {
		readedIdx--;
		reader.unread(data);
	}

	
	public Word next() throws IOException {
		Word word = bufWord.poll();;
		if(word == null) {
			bufSentence.setLength(0);

			int data = -1;
			boolean read = true;
			while(read && (data=readNext()) != -1) {
				read = false;	
				int type = Character.getType(data);
				String wordType = Word.TYPE_WORD;
				switch(type) {
				case Character.UPPERCASE_LETTER:
				case Character.LOWERCASE_LETTER:
				case Character.TITLECASE_LETTER:
				case Character.MODIFIER_LETTER:
				
					data = toAscii(data);
					NationLetter nl = getNation(data);
					if(nl == NationLetter.UNKNOW) {
						read = true;
						break;
					}
					wordType = Word.TYPE_LETTER;
					bufSentence.appendCodePoint(data);
					switch(nl) {
					case EN:
						
						ReadCharByAsciiOrDigit rcad = new ReadCharByAsciiOrDigit();
						readChars(bufSentence, rcad);
						if(rcad.hasDigit()) {
							wordType = Word.TYPE_LETTER_OR_DIGIT;
						}
						//only english
						//readChars(bufSentence, new ReadCharByAscii());
						break;
					case RA:
						readChars(bufSentence, new ReadCharByRussia());
						break;
					case GE:
						readChars(bufSentence, new ReadCharByGreece());
						break;
					}
					bufWord.add(createWord(bufSentence, wordType));

					bufSentence.setLength(0);

					break;
				case Character.OTHER_LETTER:
					
					bufSentence.appendCodePoint(data);
					readChars(bufSentence, new ReadCharByType(Character.OTHER_LETTER));

					currentSentence = createSentence(bufSentence);

					bufSentence.setLength(0);

					break;
				case Character.DECIMAL_DIGIT_NUMBER:
					bufSentence.appendCodePoint(toAscii(data));
					readChars(bufSentence, new ReadCharDigit());	
					wordType = Word.TYPE_DIGIT;
					int d = readNext();
					if(d > -1) {
						if(seg.isUnit(d)) {	
							bufWord.add(createWord(bufSentence, startIdx(bufSentence)-1, Word.TYPE_DIGIT));

							bufSentence.setLength(0);

							bufSentence.appendCodePoint(d);
							wordType = Word.TYPE_WORD;	
						} else {	
							pushBack(d);
							if(readChars(bufSentence, new ReadCharByAsciiOrDigit()) > 0) {	
								wordType = Word.TYPE_DIGIT_OR_LETTER;
							}
						}
					}

					bufWord.add(createWord(bufSentence, wordType));


					bufSentence.setLength(0);	

					break;
				case Character.LETTER_NUMBER:
					
					bufSentence.appendCodePoint(data);
					readChars(bufSentence, new ReadCharByType(Character.LETTER_NUMBER));

					int startIdx = startIdx(bufSentence);
					for(int i=0; i<bufSentence.length(); i++) {
						bufWord.add(new Word(new char[] {bufSentence.charAt(i)}, startIdx++, Word.TYPE_LETTER_NUMBER));
					}

					bufSentence.setLength(0);	

					break;
				case Character.OTHER_NUMBER:
					
					bufSentence.appendCodePoint(data);
					readChars(bufSentence, new ReadCharByType(Character.OTHER_NUMBER));

					bufWord.add(createWord(bufSentence, Word.TYPE_OTHER_NUMBER));
					bufSentence.setLength(0);
					break;
				default :
					
					read = true;
				}
			}
				
			
			if(currentSentence != null) {
				do {
					Chunk chunk = seg.seg(currentSentence);
					for(int i=0; i<chunk.getCount(); i++) {
						bufWord.add(chunk.getWords()[i]);
					}
				} while (!currentSentence.isFinish());
				
				currentSentence = null;
			}
			
			word = bufWord.poll();
		}
		
		return word;
	}
	
	

	private static abstract class ReadChar {
		
		abstract boolean isRead(int codePoint);
		int transform(int codePoint) {
			return codePoint;
		}
	}
	

	private int readChars(StringBuilder bufSentence, ReadChar readChar) throws IOException {
		int num = 0;
		int data = -1;
		while((data = readNext()) != -1) {
			int d = readChar.transform(data);
			if(readChar.isRead(d)) {
				bufSentence.appendCodePoint(d);
				num++;
			} else {	
				pushBack(data);
				break;
			}
		}
		return num;
	}

	private static class ReadCharDigit extends ReadChar {

		boolean isRead(int codePoint) {
			int type = Character.getType(codePoint);
			return isDigit(type);
		}
		
		int transform(int codePoint) {
			return toAscii(codePoint);
		}
		
	}
	

	private static class ReadCharByAsciiOrDigit extends ReadCharDigit {

		private boolean hasDigit = false;
		boolean isRead(int codePoint) {
			boolean isRead = super.isRead(codePoint);
			hasDigit |= isRead;
			return isAsciiLetter(codePoint) || isRead;
		}
		boolean hasDigit() {
			return hasDigit;
		}
	}
	
	@SuppressWarnings("unused")
	private static class ReadCharByAscii extends ReadCharDigit {
		boolean isRead(int codePoint) {
			return isAsciiLetter(codePoint);
		}
	}
	
	private static class ReadCharByRussia extends ReadCharDigit {

		boolean isRead(int codePoint) {
			return isRussiaLetter(codePoint);
		}
		
	}
	
	private static class ReadCharByGreece extends ReadCharDigit {

		boolean isRead(int codePoint) {
			return isGreeceLetter(codePoint);
		}
		
	}
	
	private static class ReadCharByType extends ReadChar {
		int charType;
		public ReadCharByType(int charType) {
			this.charType = charType;
		}

		boolean isRead(int codePoint) {
			int type = Character.getType(codePoint);
			return type == charType;
		}
		
	}
	
	private Word createWord(StringBuilder bufSentence, String type) {
		return new Word(toChars(bufSentence), startIdx(bufSentence), type);
	}
	
	private Word createWord(StringBuilder bufSentence, int startIdx, String type) {
		return new Word(toChars(bufSentence), startIdx, type);
	}
	
	private Sentence createSentence(StringBuilder bufSentence) {
		return new Sentence(toChars(bufSentence), startIdx(bufSentence));
	}
	
	private int startIdx(StringBuilder bufSentence) {
		return readedIdx - bufSentence.length() + 1;
	}
	
	private static char[] toChars(StringBuilder bufSentence) {
		char[] chs = new char[bufSentence.length()];
		bufSentence.getChars(0, bufSentence.length(), chs, 0);
		return chs;
	}

	private static int toAscii(int codePoint) {
		if((codePoint>=65296 && codePoint<=65305)	
				|| (codePoint>=65313 && codePoint<=65338)	
				|| (codePoint>=65345 && codePoint<=65370)	
				) {	
			codePoint -= 65248;
		}
		return codePoint;
	}
	
	private static boolean isAsciiLetter(int codePoint) {
		return (codePoint >= 'A' && codePoint <= 'Z') || (codePoint >= 'a' && codePoint <= 'z');
	}
	
	private static boolean isRussiaLetter(int codePoint) {
		return (codePoint >= 'А' && codePoint <= 'я') || codePoint=='Ё' || codePoint=='ё';
	}
	
	private static boolean isGreeceLetter(int codePoint) {
		return (codePoint >= 'Α' && codePoint <= 'Ω') || (codePoint >= 'α' && codePoint <= 'ω');
	}

	private static enum NationLetter {EN, RA, GE, UNKNOW};
	
	private NationLetter getNation(int codePoint) {
		if(isAsciiLetter(codePoint)) {
			return NationLetter.EN;
		}
		if(isRussiaLetter(codePoint)) {
			return NationLetter.RA;
		}
		if(isGreeceLetter(codePoint)) {
			return NationLetter.GE;
		}
		return NationLetter.UNKNOW;
	}
	
	@SuppressWarnings("unused")
	private static boolean isCJK(int type) {
		return type == Character.OTHER_LETTER;
	}
	private static boolean isDigit(int type) {
		return type == Character.DECIMAL_DIGIT_NUMBER;
	}
	@SuppressWarnings("unused")
	private static boolean isLetter(int type) {
		return type <= Character.MODIFIER_LETTER && type >= Character.UPPERCASE_LETTER;
	}
}
