package com.fmyblack.word;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordDictionary implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static String wordFile = "/Users/fmyblack/javaproject/fmyblack-spark/src/main/resources/data/words.data";
	
	// 正在使用的词语类型
	public static final int usingWordType = 1;
	// 停止词语类型
	public static final int stopWordType = 2;

	// 当前使用词库
	private Set<String> usingWordLibrary = new HashSet<String>();
	// 预处理当前使用词库
	// private Map<String, Integer> compareUsingWordLibrary = new
	// HashMap<String, Integer>();
	// 最大词长
	private Integer maxLen;
	// 停止词库
	private Set<String> stopWordLibrary = new HashSet<String>();
	// 预处理停止词库词库
	// private Map<String, Integer> compareStopWordLibrary = new HashMap<String,
	// Integer>();

	// 数字正则列表
	private List<Pattern> numericRegexPatternList = null;
	// 英文单词正则列表
	private List<Pattern> englishRegexPatternList = null;
	// 标点符号配置列表
	private String marksReg = null;
	// 单例
	private static WordDictionary ins = null;

	public static Pattern wordPattern = Pattern.compile("([^\\#][^\\s|^0-9]+)");
	
	public static synchronized WordDictionary getIns() throws IOException {
		if (ins == null) {
			List<Word> words = getWordDictFromDB();
			ins = new WordDictionary(words);
		}
		return ins;
	}

	/**
	 * 构造函数
	 * 
	 * @throws IOException
	 */
	private WordDictionary(List<Word> words) throws IOException {
		// 从数据库中获取词库
		this.parseDictionaryData(words);
		// 从数据库中读出依赖的配置
		String numericRegexListStr = "([0-9]+)";
		List<String> numericRegex = new ArrayList<String>();
		numericRegex.add(numericRegexListStr);
		String englishRegexListStr = "([A-Z|a-z]*)";
		List<String> englishRegex = new ArrayList<String>();
		englishRegex.add(englishRegexListStr);
		String marksReg = "[\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]";
		this.initPattern(numericRegex,
				englishRegex, marksReg);
	}

	/**
	 * 测试用-构造函数
	 * 
	 * @param usingWordsDir
	 * @param stopWordsDir
	 * @param numericRegexList
	 * @param englishRegexList
	 * @param marksReg
	 * @throws IOException
	 */
	public WordDictionary(String usingWordsDir, String stopWordsDir,
			List<String> numericRegexList, List<String> englishRegexList,
			String marksReg) throws IOException {
		this.maxLen = 0;
		getWordDictFromFile(usingWordsDir, stopWordsDir);
		this.initPattern(numericRegexList, englishRegexList, marksReg);
	}

	private void initPattern(List<String> numericRegexList,
			List<String> englishRegexList, String marksReg) {
		// 编译正则
		this.numericRegexPatternList = Arrays
				.asList(new Pattern[numericRegexList.size()]);
		for (int i = 0; i < numericRegexList.size(); i++) {
			String regex = numericRegexList.get(i);
			Pattern pattern = Pattern.compile(regex);
			this.numericRegexPatternList.set(i, pattern);
		}
		this.englishRegexPatternList = Arrays
				.asList(new Pattern[englishRegexList.size()]);
		for (int i = 0; i < englishRegexList.size(); i++) {
			String regex = englishRegexList.get(i);
			Pattern pattern = Pattern.compile(regex);
			this.englishRegexPatternList.set(i, pattern);
		}
		this.marksReg = marksReg;
	}

	/**
	 * 是否存在数字串匹配的正则
	 * 
	 * @param dw
	 * @return
	 */
	public boolean existNumericStr(String dw) {
		boolean existFlag = false;
		for (int i = 0; i < numericRegexPatternList.size(); i++) {
			Pattern pattern = numericRegexPatternList.get(i);
			Matcher m = pattern.matcher(dw);
			if (m.find()) {
				existFlag = true;
				break;
			}
		}
		return existFlag;
	}

	/**
	 * 是否存在匹配英语词的正则
	 * 
	 * @param dw
	 * @return
	 */
	public boolean existEnglishStr(String dw) {
		// dw匹配英文正则列表
		boolean englishFlag = false;
		for (int i = 0; i < englishRegexPatternList.size(); i++) {
			Pattern pattern = englishRegexPatternList.get(i);
			Matcher m = pattern.matcher(dw);
			if (m.find()) {
				englishFlag = true;
				break;
			}
		}
		return englishFlag;
	}

	/**
	 * 从文件中读出词库
	 * 
	 * @param usingWordsDir
	 * @param stopWordsDir
	 * @throws IOException
	 */
	private void getWordDictFromFile(String usingWordsPath, String stopWordsPath)
			throws IOException {
		loadOneFile(usingWordsPath, WordDictionary.usingWordType);
		loadOneFile(stopWordsPath, WordDictionary.stopWordType);
	}

	// 加载一个字典文件作为词库
	private void loadOneFile(String stopWordsPath, int nWordType)
			throws IOException {
		FileInputStream fi = null;
		InputStreamReader bi = null;
		BufferedReader br = null;

		try {
			fi = new FileInputStream(stopWordsPath);
			bi = new InputStreamReader(fi, Charset.forName("UTF-8"));
			br = new BufferedReader(bi);
			String line = null;
			while ((line = br.readLine()) != null) {
				int currLen = loadDictLine(line, nWordType);
				this.maxLen = this.maxLen > currLen ? this.maxLen : currLen;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fi != null) {
				fi.close();
			}
			if (bi != null) {
				bi.close();
			}
			if (br != null) {
				br.close();
			}
		}
	}

	// 加载一行
	private int loadDictLine(String line, int nWordType) {
		Integer maxLength = 0;
		Matcher matcher = wordPattern.matcher(line);
		while (matcher.find()) {
			String word = matcher.group(1);
			// 取最长的词的长度
			int wordLen = word.length();
			if (wordLen > maxLength) {
				maxLength = wordLen;
			}
			if (usingWordType == nWordType) {
				usingWordLibrary.add(word);
				// compareUsingWordLibrary.put(word, wordLen);
			} else if (stopWordType == nWordType) {
				stopWordLibrary.add(word);
				// compareStopWordLibrary.put(word, wordLen);
			} else {
				throw new IllegalArgumentException("Unknown word type:"
						+ nWordType);
			}
		}
		return maxLength;
	}

	/**
	 * 从db中获取搜索词正则列表、数字正则列表、英文正则列表、标点符号正则、当前使用词库、停止词库
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	private static List<Word> getWordDictFromDB() throws IOException {
	    List<Word> words = new ArrayList<Word>();
//	    String words_file = ConfigHelper.getConf("train", "words.file");
	    String words_file = wordFile;
	    BufferedReader br = new BufferedReader(new FileReader(words_file));
	    String line = null;
	    while((line = br.readLine()) != null) {
	        String[] cols = line.split("\t");
	        Word word = new Word();
	        word.setWord_name(cols[0]);
	        word.setWord_type(Integer.parseInt(cols[1]));
	        words.add(word);
	    }
		return words;
	}

	private void parseDictionaryData(List<Word> words) {
		Integer maxLength = 0;
		for (Word word : words) {
			int nWordType = word.getWord_type();
			String wordName = word.getWord_name();
			// 取最长的词的长度
			int wordLen = wordName.length();
			if (wordLen <= 1) {
			    // 单字不用考虑
			    continue;
			}
			if (wordLen > maxLength) {
				maxLength = wordLen;
			}
			if (usingWordType == nWordType) {
				usingWordLibrary.add(wordName);
				// compareUsingWordLibrary.put(word, wordLen);
			} else if (stopWordType == nWordType) {
				stopWordLibrary.add(wordName);
				// compareStopWordLibrary.put(word, wordLen);
			} else {
				throw new IllegalArgumentException("Unknown word type:"
						+ nWordType);
			}
		}
		this.maxLen = maxLength;
	}

	public boolean containUsingWord(String word) {
		return usingWordLibrary.contains(word);
	}

	public Integer getMaxLen() {
		return maxLen;
	}

	public boolean containStopWord(String word) {
		return stopWordLibrary.contains(word);
	}

	public String[] splitByMarks(String inputKey) {
		try {
			String key = inputKey.replaceAll(marksReg, " ")
					.replaceAll("\\s+", " ").trim();
			return key.split(" ");
		} catch (Throwable e) {
			e.printStackTrace();
			throw new IllegalStateException("inputKey:[" + inputKey + "],msg:"
					+ e.getMessage());
		}
	}
	
	public Set<String> getStopWordsLib() {
	    return this.stopWordLibrary;
	}
}

