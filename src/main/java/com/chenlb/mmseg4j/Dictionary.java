package com.chenlb.mmseg4j;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import scala.Serializable;


public class Dictionary implements Serializable{
	
	private static final long serialVersionUID = 6160232049577998333L;

	private static final Logger log = Logger.getLogger(Dictionary.class.getName());

	private File dicPath;	
	private volatile Map<Character, CharNode> dict;
	private volatile Map<Character, Object> unit;


	private Map<File, Long> wordsLastTime = null;
	private long lastLoadTime = 0;

	
	private static File defalutPath = null;
	private static final ConcurrentHashMap<File, Dictionary> dics = new ConcurrentHashMap<File, Dictionary>();

	protected void finalize() throws Throwable {
	
		destroy();
	}


	public static Dictionary getInstance() {
		File path = getDefalutPath();
		return getInstance(path);
	}

	
	public static Dictionary getInstance(String path) {
		return getInstance(new File(path));
	}


	public static Dictionary getInstance(File path) {
		log.info("try to load dir="+path);
		File normalizeDir = normalizeFile(path);
		Dictionary dic = dics.get(normalizeDir);
		if(dic == null) {
			dic = new Dictionary(normalizeDir);
			dics.put(normalizeDir, dic);
		}
		return dic;
	}

	public static File normalizeFile(File file) {
		if(file == defalutPath) {
			return defalutPath;
		}
		try {
			return file.getCanonicalFile();
		} catch (IOException e) {
			throw new RuntimeException("normalize file=["+file+"] fail", e);
		}
	}


	void destroy() {
		clear(dicPath);

		dicPath = null;
		dict = null;
		unit = null;
	}


	public static Dictionary clear(String path) {
		return clear(new File(path));
	}


	public static Dictionary clear(File path) {
		File normalizeDir = normalizeFile(path);
		return dics.remove(normalizeDir);
	}


	private Dictionary(File path) {
		init(path);
	}

	private void init(File path) {
		dicPath = path;
		wordsLastTime = new HashMap<File, Long>();

		reload();	
	}

	private static long now() {
		return System.currentTimeMillis();
	}

	protected File[] listWordsFiles() {
		return dicPath.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {

				return name.startsWith("words") && name.endsWith(".dic");
			}

		});
	}

	private Map<Character, CharNode> loadDic(File wordsPath) throws IOException {
		InputStream charsIn = null;
		File charsFile = new File(wordsPath, "chars.dic");
		if(charsFile.exists()) {
			charsIn = new FileInputStream(charsFile);
			addLastTime(charsFile);	
		} else {	
			charsIn = this.getClass().getResourceAsStream("/data/chars.dic");
			charsFile = new File(this.getClass().getResource("/data/chars.dic").getFile());	//only for log
		}
		final Map<Character, CharNode> dic = new HashMap<Character, CharNode>();
		int lineNum = 0;
		long s = now();
		long ss = s;
		lineNum = load(charsIn, new FileLoading() {	

			public void row(String line, int n) {
				if(line.length() < 1) {
					return;
				}
				String[] w = line.split(" ");
				CharNode cn = new CharNode();
				switch(w.length) {
				case 2:
					try {
						cn.setFreq((int)(Math.log(Integer.parseInt(w[1]))*100));//字频计算出自由度
					} catch(NumberFormatException e) {
						
					}
				case 1:

					dic.put(w[0].charAt(0), cn);
				}
			}
		});
		log.info("chars loaded time="+(now()-s)+"ms, line="+lineNum+", on file="+charsFile);

		InputStream wordsDicIn = this.getClass().getResourceAsStream("/data/words.dic");
		if(wordsDicIn != null) {
			File wordsDic = new File(this.getClass().getResource("/data/words.dic").getFile());
			loadWord(wordsDicIn, dic, wordsDic);
		}

		File[] words = listWordsFiles();	
		if(words != null) {	
			for(File wordsFile : words) {
				loadWord(new FileInputStream(wordsFile), dic, wordsFile);

				addLastTime(wordsFile);
			}
		}

		log.info("load all dic use time="+(now()-ss)+"ms");
		return dic;
	}


	private void loadWord(InputStream is, Map<Character, CharNode> dic, File wordsFile) throws IOException {
		long s = now();
		int lineNum = load(is, new WordsFileLoading(dic)); 
		log.info("words loaded time="+(now()-s)+"ms, line="+lineNum+", on file="+wordsFile);
	}

	private Map<Character, Object> loadUnit(File path) throws IOException {
		InputStream fin = null;
		File unitFile = new File(path, "units.dic");
		if(unitFile.exists()) {
			fin = new FileInputStream(unitFile);
			addLastTime(unitFile);
		} else {	
			fin = Dictionary.class.getResourceAsStream("/data/units.dic");
			unitFile = new File(Dictionary.class.getResource("/data/units.dic").getFile());
		}

		final Map<Character, Object> unit = new HashMap<Character, Object>();

		long s = now();
		int lineNum = load(fin, new FileLoading() {

			public void row(String line, int n) {
				if(line.length() != 1) {
					return;
				}
				unit.put(line.charAt(0), Dictionary.class);
			}
		});
		log.info("unit loaded time="+(now()-s)+"ms, line="+lineNum+", on file="+unitFile);

		return unit;
	}


	private static class WordsFileLoading implements FileLoading {
		final Map<Character, CharNode> dic;

	
		public WordsFileLoading(Map<Character, CharNode> dic) {
			this.dic = dic;
		}

		public void row(String line, int n) {
			if(line.length() < 2) {
				return;
			}
			CharNode cn = dic.get(line.charAt(0));
			if(cn == null) {
				cn = new CharNode();
				dic.put(line.charAt(0), cn);
			}
			cn.addWordTail(tail(line));
		}
	}


	public static int load(InputStream fin, FileLoading loading) throws IOException {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(new BufferedInputStream(fin), "UTF-8"));
		String line = null;
		int n = 0;
		while((line = br.readLine()) != null) {
			if(line == null || line.startsWith("#")) {
				continue;
			}
			n++;
			loading.row(line, n);
		}
		return n;
	}


	private static char[] tail(String str) {
		char[] cs = new char[str.length()-1];
		str.getChars(1, str.length(), cs, 0);
		return cs;
	}

	public static interface FileLoading {
	
		void row(String line, int n);
	}


	private synchronized void addLastTime(File wordsFile) {
		if(wordsFile != null) {
			wordsLastTime.put(wordsFile, wordsFile.lastModified());
		}
	}

	public synchronized boolean wordsFileIsChange() {
		for(Entry<File, Long> flt : wordsLastTime.entrySet()) {
			File words = flt.getKey();
			if(!words.canRead()) {	
				return true;
			}
			if(words.lastModified() > flt.getValue()) {	
				return true;
			}
		}

		File[] words = listWordsFiles();
		if(words != null) {
			for(File wordsFile : words) {
				if(!wordsLastTime.containsKey(wordsFile)) {
					return true;
				}
			}
		}
		return false;
	}


	public synchronized boolean reload() {
		Map<File, Long> oldWordsLastTime = new HashMap<File, Long>(wordsLastTime);
		Map<Character, CharNode> oldDict = dict;
		Map<Character, Object> oldUnit = unit;

		try {
			wordsLastTime.clear();
			dict = loadDic(dicPath);
			unit = loadUnit(dicPath);
			lastLoadTime = System.currentTimeMillis();
		} catch (IOException e) {
			
			wordsLastTime.putAll(oldWordsLastTime);
			dict = oldDict;
			unit = oldUnit;

			if(log.isLoggable(Level.WARNING)) {
				log.log(Level.WARNING, "reload dic error! dic="+dicPath+", and rollbacked.", e);
			}

			return false;
		}
		return true;
	}


	public boolean match(String word) {
		if(word == null || word.length() < 2) {
			return false;
		}
		CharNode cn = dict.get(word.charAt(0));
		return search(cn, word.toCharArray(), 0, word.length()-1) >= 0;
	}

	public CharNode head(char ch) {
		return dict.get(ch);
	}


	public int search(CharNode node, char[] sen, int offset, int tailLen) {
		if(node != null) {
			return node.indexOf(sen, offset, tailLen);
		}
		return -1;
	}

	public int maxMatch(char[] sen, int offset) {
		CharNode node = dict.get(sen[offset]);
		return maxMatch(node, sen, offset);
	}

	public int maxMatch(CharNode node, char[] sen, int offset) {
		if(node != null) {
			return node.maxMatch(sen, offset+1);
		}
		return 0;
	}

	public ArrayList<Integer> maxMatch(CharNode node, ArrayList<Integer> tailLens, char[] sen, int offset) {
		tailLens.clear();
		tailLens.add(0);
		if(node != null) {
			return node.maxMatch(tailLens, sen, offset+1);
		}
		return tailLens;
	}

	public boolean isUnit(Character ch) {
		return unit.containsKey(ch);
	}


	public static File getDefalutPath() {
		if(defalutPath == null) {
			String defPath = System.getProperty("mmseg.dic.path");
			log.info("look up in mmseg.dic.path="+defPath);
			if(defPath == null) {
				URL url = Dictionary.class.getClassLoader().getResource("data");
				if(url != null) {
					defPath = url.getFile();
					log.info("look up in classpath="+defPath);
				} else {
					defPath = System.getProperty("user.dir")+"/data";
					log.info("look up in user.dir="+defPath);
				}

			}

			defalutPath = new File(defPath);
			if(!defalutPath.exists()) {
				log.warning("defalut dic path="+defalutPath+" not exist");
			}
		}
		return defalutPath;
	}


	public Map<Character, CharNode> getDict() {
		return dict;
	}

	
	public File getDicPath() {
		return dicPath;
	}

	public long getLastLoadTime() {
		return lastLoadTime;
	}
}
